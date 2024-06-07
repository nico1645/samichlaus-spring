package com.samichlaus.api.controller;

import com.samichlaus.api.domain.customer.Customer;
import com.samichlaus.api.domain.graphhopper.RouteTime;
import com.samichlaus.api.services.GraphhopperService;

import jakarta.validation.Valid;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.samichlaus.api.domain.constants.Constants;

@RestController
@RequestMapping("/api/v1/routing")
public class GraphhopperController {

    private final GraphhopperService graphhopperService;

    public GraphhopperController(GraphhopperService graphhopperService) {
        this.graphhopperService = graphhopperService;
    }

    @PostMapping("route")
    public ResponseEntity<RouteTime> calculateRoute(@Valid @RequestBody RouteTime routeTime) {

        List<Customer> visits = routeTime.getCustomers();
        LocalTime currentTime = routeTime.getStartTime();
        List<Double> depotLatLng = routeTime.getDepot();
        int timeInterval;
        Customer currentCustomer = visits.get(0);
        Customer currentCustomerPlusOne;
        timeInterval = Math.round((float) graphhopperService.calculateTime(depotLatLng.get(0), depotLatLng.get(1), currentCustomer.getAddress().getLatitude(), currentCustomer.getAddress().getLongitude(), currentCustomer.getTransport().toString()) / 1000 / 60 / 5) * 5;
        currentTime = currentTime.plusMinutes(timeInterval);
        currentCustomer.setVisitTime(Time.valueOf(currentTime).toLocalTime());
        List<Customer> updatedCustomers = new ArrayList<>();
        updatedCustomers.add(currentCustomer);

        for (int i = 0; i < routeTime.getCustomers().size()-1; i++) {
            currentCustomer = visits.get(i);
            currentCustomerPlusOne = visits.get(i+1);

            timeInterval = Math.round((float) graphhopperService.calculateTime(currentCustomerPlusOne.getAddress().getLatitude(), currentCustomerPlusOne.getAddress().getLongitude(), currentCustomer.getAddress().getLatitude(), currentCustomer.getAddress().getLongitude(), currentCustomer.getTransport().toString()) / 1000 / 60 / 5) * 5;
            timeInterval += Constants.getChildrenTime(currentCustomer.getChildren()) + Constants.getSeniorTime(currentCustomer.getSeniors());
            currentTime = currentTime.plusMinutes(timeInterval);
            currentCustomerPlusOne.setVisitTime(Time.valueOf(currentTime).toLocalTime());
            updatedCustomers.add(currentCustomerPlusOne);
        }
        currentCustomer = visits.get(visits.size()-1);
        timeInterval = Math.round((float) graphhopperService.calculateTime(currentCustomer.getAddress().getLatitude(), currentCustomer.getAddress().getLongitude(), depotLatLng.get(0), depotLatLng.get(1), currentCustomer.getTransport().toString()) / 1000 / 60 / 5) * 5;
        timeInterval += Constants.getChildrenTime(currentCustomer.getChildren()) + Constants.getSeniorTime(currentCustomer.getSeniors());
        LocalTime endTime = currentTime.plusMinutes(timeInterval);

        routeTime.setEndTime(endTime);
        routeTime.setCustomers(updatedCustomers);
        
        return new ResponseEntity<>(routeTime,  HttpStatus.OK);
    }
}
