package com.samichlaus.api.controller;

import java.io.IOException;
import java.util.UUID;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.samichlaus.api.domain.constants.Group;
import com.samichlaus.api.domain.constants.Rayon;
import com.samichlaus.api.domain.constants.Version;
import com.samichlaus.api.domain.route.Route;
import com.samichlaus.api.domain.tour.Tour;
import com.samichlaus.api.domain.tour.TourRepository;
import com.samichlaus.api.domain.user.UserRepository;
import com.samichlaus.api.exception.IllegalCSVFileException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.samichlaus.api.domain.customer.Customer;
import com.samichlaus.api.domain.customer.CustomerDto;
import com.samichlaus.api.domain.customer.CustomerRepository;
import com.samichlaus.api.domain.user.User;


import com.samichlaus.api.domain.address.Address;
import com.samichlaus.api.domain.address.AddressRepository;

import com.samichlaus.api.exception.InternalServerErrorException;
import com.samichlaus.api.exception.ResourceNotFoundException;
import com.samichlaus.api.helpers.CSVHelper;
import com.samichlaus.api.services.CSVService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/customer")
public class CustomerController {

    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final TourRepository tourRepository;
    private final CSVService csvService;


    public CustomerController(CustomerRepository customerRepository, AddressRepository addressRepository, CSVService csvService, TourRepository tourRepository) {
        this.tourRepository = tourRepository;
        this.customerRepository = customerRepository;
        this.addressRepository = addressRepository;
        this.csvService = csvService;
    }

    @GetMapping("{id}")
    public ResponseEntity<Customer> getCustomerByUUID(@PathVariable("id") UUID uuid) throws ResourceNotFoundException {
        Optional<Customer> customer = customerRepository.findByUUID(uuid);
    
        if (customer.isPresent()) {
        return new ResponseEntity<>(customer.get(), HttpStatus.OK);
        } else {
        throw new ResourceNotFoundException("Customer not found");
        }
    }

    @PostMapping("")
    public ResponseEntity<Customer> createCustomer(@RequestBody @Valid CustomerDto customerDto, Authentication authentication) throws InternalServerErrorException {
        User user = (User) authentication.getPrincipal();

        Optional<Address> address = addressRepository.findByUUID(customerDto.getAddressId());
        
        if (address.isEmpty()) {
            throw new InternalServerErrorException("Address not found");
        }
        
        Customer _customer = customerDto.toCustomer();
        _customer.setUser(user);
        _customer.setAddress(address.get());
        _customer.setLastModified(new Date());
        if (_customer.getVisitRayon() == null) {
            _customer.setVisitRayon(address.get().getRayon());
        }  
        Customer customer = customerRepository.save(_customer);
        return new ResponseEntity<>(customer, HttpStatus.CREATED); 
    }

    @PostMapping("tour")
    public ResponseEntity<Customer> createCustomerAndAddToTour(@RequestBody @Valid CustomerDto customerDto, Authentication authentication) throws InternalServerErrorException {
        User user = (User) authentication.getPrincipal();

        Optional<Address> address = addressRepository.findByUUID(customerDto.getAddressId());

        if (address.isEmpty()) {
            throw new InternalServerErrorException("Address not found");
        }

        Customer _customer = customerDto.toCustomer();
        _customer.setUser(user);
        _customer.setAddress(address.get());
        _customer.setLastModified(new Date());
        if (_customer.getVisitRayon() == null) {
            _customer.setVisitRayon(address.get().getRayon());
        }
        Customer customer = null;
        Optional<Tour> tourOpt = tourRepository.findTourByYearAndRayonAndVersion(_customer.getYear(), _customer.getVisitRayon(), Version.TST);
        if (tourOpt.isPresent()) {
            Tour tour = tourOpt.get();
            List<Route> routes = tour.getRoutes();
            for (Route route : routes) {
                if (route.getGroup() == Group.Z) {
                    _customer.setRoute(route);
                    customer = customerRepository.save(_customer);
                    route.getCustomers().add(customer);
                    break;
                }
            }
        }
        if (customer == null) {
            Tour newTour = Tour.builder()
                    .rayon(_customer.getVisitRayon())
                    .year(_customer.getYear())
                    .version(Version.TST)
                    .build();
            customer = customerRepository.save(_customer);
            Route newRoute = Route.builder()
                    .customerStart(customer.getVisitTime())
                    .group(Group.Z)
                    .build();
            newRoute.getCustomers().add(customer);
            newTour.getRoutes().add(newRoute);
            tourRepository.save(newTour);
        }
        return new ResponseEntity<>(customer, HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable("id") UUID uuid, @Valid @RequestBody CustomerDto customerDto) throws InternalServerErrorException {
        Optional<Customer> customer = customerRepository.findByUUID(uuid);
        Optional<Address> address = addressRepository.findByUUID(customerDto.getAddressId());

        if (address.isEmpty()) {
            throw new InternalServerErrorException("Address not found");
        }
  
        if (customer.isPresent()) {
            Customer _customer = customer.get();
            _customer.setFirstName(customerDto.getFirstName());
            _customer.setLastName(customerDto.getLastName());
            _customer.setLastModified(new Date());
            _customer.setChildren(customerDto.getChildren());
            _customer.setSeniors(customerDto.getSeniors());
            _customer.setYear(customerDto.getYear());
            _customer.setVisitTime(customerDto.getVisitTime());
            _customer.setVisitRayon(Rayon.fromValue(customerDto.getVisitRayon()));
            _customer.setAddress(address.get());
            return new ResponseEntity<>(customerRepository.save(_customer), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
  
    @DeleteMapping("{id}")
    public ResponseEntity<HttpStatus> deleteCustomer(@PathVariable("id") UUID uuid) {
        Optional<Customer> customer = customerRepository.findByUUID(uuid);
        if (customer.isPresent()) {
            customerRepository.deleteById(customer.get().getId());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("years")
    public ResponseEntity<List<Integer>> getAvailableYears() {
        List<Integer> years = customerRepository.getAvailableYears();

        return new ResponseEntity<>(years, HttpStatus.OK);
    }

    @PostMapping("upload")
    public ResponseEntity<List<Customer>> uploadCustomers(@RequestParam MultipartFile file, Authentication authentication) throws IllegalCSVFileException, IOException {
        User user = (User) authentication.getPrincipal();

        if (CSVHelper.hasCSVFormat(file)) {
            List<Customer> customers = csvService.saveCustomers(file, user);

            return new ResponseEntity<>(customers, HttpStatus.CREATED);
        }

        throw new IllegalCSVFileException("Please upload a csv file, the provided file is not a valid csv");
    }
}
