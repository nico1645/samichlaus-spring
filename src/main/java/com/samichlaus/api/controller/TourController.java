package com.samichlaus.api.controller;

import com.samichlaus.api.domain.constants.Rayon;
import com.samichlaus.api.domain.constants.Version;
import com.samichlaus.api.domain.customer.CustomerRepository;
import com.samichlaus.api.domain.route.RouteRepository;
import com.samichlaus.api.domain.tour.Tour;
import com.samichlaus.api.domain.tour.TourDto;
import com.samichlaus.api.domain.tour.TourRepository;
import com.samichlaus.api.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tour")
public class TourController {
  private final CustomerRepository customerRepository;
  private final RouteRepository routeRepository;
  private final TourRepository tourRepository;

  public TourController(
      CustomerRepository customerRepository,
      RouteRepository routeRepository,
      TourRepository tourRepository) {
    this.customerRepository = customerRepository;
    this.routeRepository = routeRepository;
    this.tourRepository = tourRepository;
  }

  @GetMapping("{version}/{year}/{rayon}")
  public ResponseEntity<Tour> getTourByVersionAndYearAndRayon(
      @PathVariable("year") int year,
      @PathVariable("rayon") int rayon,
      @PathVariable("version") String version)
      throws ResourceNotFoundException {
    Rayon rayonEnum = Rayon.fromValue(rayon);
    Version tourVersion = Version.fromValue(version);

    Optional<Tour> tour =
        tourRepository.findTourByYearAndRayonAndVersion(year, rayonEnum, tourVersion);
    if (tour.isPresent()) {
      Tour t = tour.get();
      return new ResponseEntity<>(t, HttpStatus.OK);
    } else {
      throw new ResourceNotFoundException("No tour exist for year and rayon");
    }
  }

  @PutMapping("{id}")
  public ResponseEntity<Tour> updateTour(
      @PathVariable("id") UUID uuid, @Valid @RequestBody TourDto tourDto)
      throws ResourceNotFoundException {
    Optional<Tour> tourOpt = tourRepository.findTourByUUID(uuid);
    if (tourOpt.isPresent()) {
      Tour tour = tourOpt.get();
      tour.setLastModified(new Date());
      tour.setDate(tourDto.getDate());
      tourRepository.save(tour);
      return new ResponseEntity<>(tour, HttpStatus.OK);
    } else {
      throw new ResourceNotFoundException("No tour with id exists");
    }
  }
}
