package com.samichlaus.api.controller;

import com.samichlaus.api.domain.graphhopper.RouteTime;
import com.samichlaus.api.services.GraphhopperService;

import jakarta.validation.Valid;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/routing")
public class GraphhopperController {

    private final GraphhopperService graphhopperService;

    public GraphhopperController(GraphhopperService graphhopperService) {
        this.graphhopperService = graphhopperService;
    }

    @PostMapping("route")
    public ResponseEntity<RouteTime> calculateRoute(@Valid @RequestBody RouteTime routeTime) {

        int DEFAULT_MAX_WAIT_TIME = 50;

        HashMap<Integer, Integer> childrenMap = new HashMap<>();
        childrenMap.put(0, 0);
        childrenMap.put(1, 15);
        childrenMap.put(2, 20);
        childrenMap.put(3, 30);
        childrenMap.put(4, 40);

        HashMap<Integer, Integer> seniorMap = new HashMap<>();
        seniorMap.put(0, 0);
        seniorMap.put(1, 10);
        seniorMap.put(2, 20);
        seniorMap.put(3, 30);
        seniorMap.put(4, 40);

//        List<Visit> visits = routeTime.getVisits();
//        LocalTime currentTime = routeTime.getStartTime();
//        List<Double> depotLatLng = routeTime.getDepot();
//        int timeInterval;
//        Visit currentVisit = visits.get(0);
//        Visit currentVisitPlusOne;
//        timeInterval = Math.round((float) graphhopperService.calculateTime(depotLatLng.get(0), depotLatLng.get(1), currentVisit.getCustomer().getAddress().getLatitude(), currentVisit.getCustomer().getAddress().getLongitude()) / 1000 / 60 / 5) * 5;
//        currentTime = currentTime.plusMinutes(timeInterval);
//        currentVisit.setTimeVisit(Time.valueOf(currentTime));
//        List<Visit> updatedVisits = new ArrayList<>();
//        updatedVisits.add(currentVisit);
//
//        for (int i = 0; i < routeTime.getVisits().size()-1; i++) {
//            currentVisit = visits.get(i);
//            currentVisitPlusOne = visits.get(i+1);
//
//            timeInterval = Math.round((float) graphhopperService.calculateTime(currentVisitPlusOne.getCustomer().getAddress().getLatitude(), currentVisitPlusOne.getCustomer().getAddress().getLongitude(), currentVisit.getCustomer().getAddress().getLatitude(), currentVisit.getCustomer().getAddress().getLongitude()) / 1000 / 60 / 5) * 5;
//            timeInterval += childrenMap.getOrDefault(currentVisit.getCustomer().getChildren(), DEFAULT_MAX_WAIT_TIME) + seniorMap.getOrDefault(currentVisit.getCustomer().getSeniors(), DEFAULT_MAX_WAIT_TIME);
//            currentTime = currentTime.plusMinutes(timeInterval);
//            currentVisitPlusOne.setTimeVisit(Time.valueOf(currentTime));
//            updatedVisits.add(currentVisitPlusOne);
//        }
//        currentVisit = visits.get(visits.size()-1);
//        timeInterval = Math.round((float) graphhopperService.calculateTime(currentVisit.getCustomer().getAddress().getLatitude(), currentVisit.getCustomer().getAddress().getLongitude(), depotLatLng.get(0), depotLatLng.get(1)) / 1000 / 60 / 5) * 5;
//        timeInterval += childrenMap.getOrDefault(currentVisit.getCustomer().getChildren(), DEFAULT_MAX_WAIT_TIME) + seniorMap.getOrDefault(currentVisit.getCustomer().getSeniors(), DEFAULT_MAX_WAIT_TIME);
//        LocalTime endTime = currentTime.plusMinutes(timeInterval);
//
//        routeTime.setEndTime(endTime);
//        routeTime.setVisits(updatedVisits);
        
        return new ResponseEntity<>(routeTime,  HttpStatus.OK);
    }
}
