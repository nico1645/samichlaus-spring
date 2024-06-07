package com.samichlaus.api.controller;

import com.samichlaus.api.domain.address.AddressRepository;
import com.samichlaus.api.domain.constants.Group;
import com.samichlaus.api.domain.customer.CustomerRepository;
import com.samichlaus.api.domain.route.Route;
import com.samichlaus.api.domain.route.RouteDto;
import com.samichlaus.api.domain.route.RoutePatchDto;
import com.samichlaus.api.domain.route.RouteRepository;
import com.samichlaus.api.domain.tour.Tour;
import com.samichlaus.api.domain.tour.TourRepository;
import com.samichlaus.api.domain.user.User;
import com.samichlaus.api.exception.InternalServerErrorException;
import com.samichlaus.api.exception.ResourceNotFoundException;
import com.samichlaus.api.services.CSVService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/route")
public class RouteController {
    private final CustomerRepository customerRepository;

    private final RouteRepository routeRepository;
    private final TourRepository tourRepository;

    public RouteController(CustomerRepository customerRepository, RouteRepository routeRepository, TourRepository tourRepository) {
        this.tourRepository = tourRepository;
        this.routeRepository = routeRepository;
        this.customerRepository = customerRepository;
    }

    @PutMapping("{id}")
    public ResponseEntity<Route> updateRoute(@PathVariable("id") UUID uuid, @Valid RouteDto routeDto) throws ResourceNotFoundException {
        Optional<Route> routeOpt = routeRepository.findByUUID(uuid);
        if (routeOpt.isPresent()) {
            Route route = routeOpt.get();
            Optional<Tour> tourOpt = tourRepository.findTourByUUID(routeDto.getTourId());
            if (tourOpt.isPresent()) {
                route.setLastModified(new Date());
                route.setTour(tourOpt.get());
                route.setCustomerEnd(routeDto.getCustomerEnd());
                route.setCustomerStart(routeDto.getCustomerStart());
                route.setGroup(routeDto.getGroup());
                route.setTransport(routeDto.getTransport());
                route.setSamichlaus(routeDto.getSamichlaus());
                route.setRuprecht(routeDto.getRuprecht());
                route.setSchmutzli(routeDto.getSchmutzli());
                route.setEngel1(routeDto.getEngel1());
                route.setEngel2(routeDto.getEngel2());
                route = routeRepository.save(route);

                return new ResponseEntity<>(route, HttpStatus.OK);
            } else {
                throw new ResourceNotFoundException("Tour doesn't exist");
            }
        } else {
            throw new ResourceNotFoundException("Route doesn't exist");
        }
    }

    @PatchMapping("")
    @Transactional
    public ResponseEntity<HttpStatus> updateRoute(@Valid @RequestBody RoutePatchDto routePatchDto) throws ResourceNotFoundException {
        Optional<Route> routeOpt = routeRepository.findByUUID(routePatchDto.getRouteId());
        if (routeOpt.isPresent()) {
            Route route = routeOpt.get();
            if (routePatchDto.getCustomerStart() != null) {
                route.setCustomerStart(routePatchDto.getCustomerStart());
            }
            if (routePatchDto.getCustomerEnd() != null) {
                route.setCustomerEnd(routePatchDto.getCustomerEnd());
            }
            if (routePatchDto.getGroup() != null) {
                route.setGroup(routePatchDto.getGroup());
            }
            if (routePatchDto.getTransport() != null) {
                route.setTransport(routePatchDto.getTransport());
            }
            if (routePatchDto.getSamichlaus() != null) {
                route.setSamichlaus(routePatchDto.getSamichlaus());
            }
            if (routePatchDto.getRuprecht() != null) {
                route.setRuprecht(routePatchDto.getRuprecht());
            }
            if (routePatchDto.getSchmutzli() != null) {
                route.setSchmutzli(routePatchDto.getSchmutzli());
            }
            if (routePatchDto.getEngel1() != null) {
                route.setEngel1(routePatchDto.getEngel1());
            }
            if (routePatchDto.getEngel2() != null) {
                route.setEngel2(routePatchDto.getEngel2());
            }
            if (routePatchDto.getTourId() != null) {
                Optional<Tour> tourOpt = tourRepository.findTourByUUID(routePatchDto.getTourId());
                if (tourOpt.isPresent()) {
                    route.setTour(tourOpt.get());
                } else {
                    throw new ResourceNotFoundException("Tour does not exist.");
                }
            }
            route.setLastModified(new Date());
            routeRepository.save(route);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            throw new ResourceNotFoundException("Route doesn't exist");
        }
    }

    @PatchMapping("many")
    @Transactional
    public ResponseEntity<HttpStatus> updateManyRoutes(@Valid @RequestBody List<RoutePatchDto> routePatchDtos) throws ResourceNotFoundException {
        List<Route> routes = new ArrayList<>();
        for (RoutePatchDto routePatchDto: routePatchDtos) {

            Optional<Route> routeOpt = routeRepository.findByUUID(routePatchDto.getRouteId());
            if (routeOpt.isPresent()) {
                Route route = routeOpt.get();
                if (routePatchDto.getCustomerStart() != null) {
                    route.setCustomerStart(routePatchDto.getCustomerStart());
                }
                if (routePatchDto.getCustomerEnd() != null) {
                    route.setCustomerEnd(routePatchDto.getCustomerEnd());
                }
                if (routePatchDto.getGroup() != null) {
                    route.setGroup(routePatchDto.getGroup());
                }
                if (routePatchDto.getTransport() != null) {
                    route.setTransport(routePatchDto.getTransport());
                }
                if (routePatchDto.getSamichlaus() != null) {
                    route.setSamichlaus(routePatchDto.getSamichlaus());
                }
                if (routePatchDto.getRuprecht() != null) {
                    route.setRuprecht(routePatchDto.getRuprecht());
                }
                if (routePatchDto.getSchmutzli() != null) {
                    route.setSchmutzli(routePatchDto.getSchmutzli());
                }
                if (routePatchDto.getEngel1() != null) {
                    route.setEngel1(routePatchDto.getEngel1());
                }
                if (routePatchDto.getEngel2() != null) {
                    route.setEngel2(routePatchDto.getEngel2());
                }
                if (routePatchDto.getTourId() != null) {
                    Optional<Tour> tourOpt = tourRepository.findTourByUUID(routePatchDto.getTourId());
                    if (tourOpt.isPresent()) {
                        route.setTour(tourOpt.get());
                    } else {
                        throw new ResourceNotFoundException("Tour does not exist.");
                    }
                }
                route.setLastModified(new Date());
                routes.add(route);
            } else {
                throw new ResourceNotFoundException("Route doesn't exist");
            }
        }
        routeRepository.saveAll(routes);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("")
    @Transactional
    public ResponseEntity<Route> createRoute(@Valid @RequestBody RouteDto routeDto, Authentication authentication) throws ResourceNotFoundException {
        User user = (User) authentication.getPrincipal();

        Route route = routeDto.toRoute();
        route.setLastModified(new Date());
        route.setUser(user);
        Optional<Tour> tourOpt = tourRepository.findTourByUUID(routeDto.getTourId());
        if (tourOpt.isPresent()) {
            route.setTour(tourOpt.get());
            route = routeRepository.save(route);
            return new ResponseEntity<>(route, HttpStatus.CREATED);
        } else {
            throw new ResourceNotFoundException("Tour does not exist.");
        }
    }

    @DeleteMapping("{id}")
    @Transactional
    public ResponseEntity<HttpStatus> deleteRoute(@PathVariable("id") UUID uuid) throws ResourceNotFoundException, InternalServerErrorException {
        Optional<Route> routeOpt = routeRepository.findByUUID(uuid);
        if (routeOpt.isPresent()) {
           Route route = routeOpt.get();
           if (route.getCustomers().isEmpty()) {
               routeRepository.delete(route);
               return new ResponseEntity<>(HttpStatus.OK);
           } else {
               throw new InternalServerErrorException("Route is not empty.");
           }
        } else {
            throw new ResourceNotFoundException("Route does not exist.");
        }
    }

}
