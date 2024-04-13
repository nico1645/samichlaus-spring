package com.samichlaus.api.controller;

import com.samichlaus.api.domain.constants.Rayon;
import com.samichlaus.api.domain.constants.Version;
import com.samichlaus.api.domain.customer.CustomerRepository;
import com.samichlaus.api.domain.route.Route;
import com.samichlaus.api.domain.route.RouteRepository;
import com.samichlaus.api.domain.tour.Tour;
import com.samichlaus.api.domain.tour.TourRepository;
import com.samichlaus.api.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/tour")
public class TourController {
    private final CustomerRepository customerRepository;
    private final RouteRepository routeRepository;
    private final TourRepository tourRepository;
    public TourController(CustomerRepository customerRepository, RouteRepository routeRepository, TourRepository tourRepository) {
        this.customerRepository = customerRepository;
        this.routeRepository = routeRepository;
        this.tourRepository = tourRepository;
    }

    @GetMapping("{version}/{year}/{rayon}")
    public ResponseEntity<Tour> getTourByVersionAndYearAndRayon(@PathVariable("year") int year, @PathVariable("rayon") int rayon, @PathVariable("version") String version) throws ResourceNotFoundException {
        Rayon rayonEnum = Rayon.fromValue(rayon);
        Version tourVersion = Version.fromValue(version);

        Optional<Tour> tour = tourRepository.findTourByYearAndRayonAndVersion(year, rayonEnum, tourVersion);
        if (tour.isPresent()) {
            Tour t = tour.get();
            return new ResponseEntity<>(t, HttpStatus.OK);
        } else {
            throw new ResourceNotFoundException("No tour exist for year and rayon");
        }
    }
}
