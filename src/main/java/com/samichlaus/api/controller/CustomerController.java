package com.samichlaus.api.controller;

import com.samichlaus.api.domain.address.Address;
import com.samichlaus.api.domain.address.AddressRepository;
import com.samichlaus.api.domain.constants.Group;
import com.samichlaus.api.domain.constants.Version;
import com.samichlaus.api.domain.customer.Customer;
import com.samichlaus.api.domain.customer.CustomerDto;
import com.samichlaus.api.domain.customer.CustomerPatchDto;
import com.samichlaus.api.domain.customer.CustomerRepository;
import com.samichlaus.api.domain.route.Route;
import com.samichlaus.api.domain.route.RouteRepository;
import com.samichlaus.api.domain.tour.Tour;
import com.samichlaus.api.domain.tour.TourRepository;
import com.samichlaus.api.domain.user.User;
import com.samichlaus.api.exception.IllegalCSVFileException;
import com.samichlaus.api.exception.InternalServerErrorException;
import com.samichlaus.api.exception.ResourceNotFoundException;
import com.samichlaus.api.helpers.CSVHelper;
import com.samichlaus.api.services.CSVService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/customer")
public class CustomerController {

  private final CustomerRepository customerRepository;
  private final AddressRepository addressRepository;
  private final TourRepository tourRepository;
  private final RouteRepository routeRepository;
  private final CSVService csvService;

  public CustomerController(
      CustomerRepository customerRepository,
      AddressRepository addressRepository,
      CSVService csvService,
      TourRepository tourRepository,
      RouteRepository routeRepository) {
    this.routeRepository = routeRepository;
    this.tourRepository = tourRepository;
    this.customerRepository = customerRepository;
    this.addressRepository = addressRepository;
    this.csvService = csvService;
  }

  @GetMapping("{id}")
  public ResponseEntity<Customer> getCustomerByUUID(@PathVariable("id") UUID uuid)
      throws ResourceNotFoundException {
    Optional<Customer> customer = customerRepository.findByUUID(uuid);

    if (customer.isPresent()) {
      return new ResponseEntity<>(customer.get(), HttpStatus.OK);
    } else {
      throw new ResourceNotFoundException("Customer not found");
    }
  }

  @PostMapping("")
  public ResponseEntity<Customer> createCustomer(
      @RequestBody @Valid CustomerDto customerDto, Authentication authentication)
      throws InternalServerErrorException {
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
  public ResponseEntity<Customer> createCustomerAndAddToTour(
      @RequestBody @Valid CustomerDto customerDto, Authentication authentication)
      throws InternalServerErrorException {
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
    Optional<Tour> tourOpt =
        tourRepository.findTourByYearAndRayonAndVersion(
            _customer.getYear(), _customer.getVisitRayon(), Version.TST);
    if (tourOpt.isPresent()) {
      Tour tour = tourOpt.get();
      List<Route> routes = tour.getRoutes();
      for (Route route : routes) {
        if (route.getGroup() == Group.Z) {
          _customer.setRoute(route);
          customer = customerRepository.save(_customer);
          break;
        }
      }
    }
    if (customer == null) {
      Tour newTour =
          Tour.builder()
              .rayon(_customer.getVisitRayon())
              .year(_customer.getYear())
              .date(LocalDate.of(_customer.getYear(), 1, 1))
              .lastModified(new Date())
              .user(user)
              .version(Version.TST)
              .build();
      customer = customerRepository.save(_customer);
      Route newRoute =
          Route.builder()
              .customerStart(customer.getVisitTime())
              .group(Group.Z)
              .tour(newTour)
              .user(user)
              .lastModified(new Date())
              .build();
      customer.setRoute(newRoute);
      newRoute.getCustomers().add(customer);
      newTour.getRoutes().add(newRoute);
      tourRepository.save(newTour);
    }
    return new ResponseEntity<>(customer, HttpStatus.CREATED);
  }

  @PutMapping("{id}")
  public ResponseEntity<Customer> updateCustomer(
      @PathVariable("id") UUID uuid, @Valid @RequestBody CustomerDto customerDto)
      throws InternalServerErrorException, ResourceNotFoundException {
    Optional<Customer> customer = customerRepository.findByUUID(uuid);
    Optional<Address> address = addressRepository.findByUUID(customerDto.getAddressId());

    if (address.isEmpty()) {
      throw new InternalServerErrorException("Address not found");
    }

    if (customer.isPresent()) {
      Customer _customer = customer.get();
      if (_customer.getVisitRayon().getValue() != customerDto.getVisitRayon().getValue()
          || _customer.getYear().intValue() != customerDto.getYear().intValue()) {
        Optional<Tour> tourOpt =
            tourRepository.findTourByYearAndRayonAndVersion(
                customerDto.getYear(), customerDto.getVisitRayon(), Version.TST);
        if (tourOpt.isPresent()) {
          Tour tour = tourOpt.get();
          List<Route> routes = tour.getRoutes();
          for (Route route : routes) {
            if (route.getGroup() == Group.Z) {
              _customer.setRoute(route);
              break;
            }
          }
        } else {
          throw new ResourceNotFoundException(
              "No Tour exist for the year and rayon. Please create one first");
        }
      }
      _customer.setFirstName(customerDto.getFirstName());
      _customer.setLastName(customerDto.getLastName());
      _customer.setLastModified(new Date());
      _customer.setChildren(customerDto.getChildren());
      _customer.setSeniors(customerDto.getSeniors());
      _customer.setYear(customerDto.getYear());
      _customer.setVisitTime(customerDto.getVisitTime());
      _customer.setVisitRayon(customerDto.getVisitRayon());
      _customer.setAddress(address.get());
      if (customerDto.getPhone() != null) _customer.setPhone(customerDto.getPhone());
      if (customerDto.getEmail() != null) _customer.setEmail(customerDto.getEmail());

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

  @PatchMapping("")
  @Transactional
  public ResponseEntity<HttpStatus> patchCustomer(
      @Valid @RequestBody CustomerPatchDto customerPatchDto) throws ResourceNotFoundException {
    Optional<Customer> customerOpt =
        customerRepository.findByUUID(customerPatchDto.getCustomerId());
    if (customerOpt.isPresent()) {
      Customer customer = customerOpt.get();
      if (customerPatchDto.getAddressId() != null) {
        Optional<Address> addressOpt =
            addressRepository.findByUUID(customerPatchDto.getAddressId());
        if (addressOpt.isPresent()) {
          customer.setAddress(addressOpt.get());
        } else {
          throw new ResourceNotFoundException("Address does not exist.");
        }
      }
      if (customerPatchDto.getFirstName() != null) {
        customer.setFirstName(customerPatchDto.getFirstName());
      }
      if (customerPatchDto.getLastName() != null) {
        customer.setLastName(customerPatchDto.getLastName());
      }
      if (customerPatchDto.getChildren() != null) {
        customer.setChildren(customerPatchDto.getChildren());
      }
      if (customerPatchDto.getSeniors() != null) {
        customer.setSeniors(customerPatchDto.getSeniors());
      }
      if (customerPatchDto.getYear() != null) {
        customer.setYear(customerPatchDto.getYear());
      }
      if (customerPatchDto.getVisitTime() != null) {
        customer.setVisitTime(customerPatchDto.getVisitTime());
      }
      if (customerPatchDto.getVisitRayon() != null) {
        customer.setVisitRayon(customerPatchDto.getVisitRayon());
      }
      if (customerPatchDto.getTransport() != null) {
        customer.setTransport(customerPatchDto.getTransport());
      }
      if (customerPatchDto.getRouteId() != null) {
        Optional<Route> routeOpt = routeRepository.findByUUID(customerPatchDto.getRouteId());
        if (routeOpt.isPresent()) {
          customer.setRoute(routeOpt.get());
        } else {
          throw new ResourceNotFoundException("Route does not exist.");
        }
      }
      customer.setLastModified(new Date());
      customerRepository.save(customer);
      return new ResponseEntity<>(HttpStatus.OK);
    } else {
      throw new ResourceNotFoundException("Customer does not exist");
    }
  }

  @PatchMapping("many")
  @Transactional
  public ResponseEntity<List<Customer>> patchManyCustomers(
      @Valid @RequestBody List<CustomerPatchDto> customerDtos) throws ResourceNotFoundException {
    List<Customer> customers = new ArrayList<>(customerDtos.size());
    for (CustomerPatchDto customerPatchDto : customerDtos) {
      Optional<Customer> customerOpt =
          customerRepository.findByUUID(customerPatchDto.getCustomerId());
      if (customerOpt.isPresent()) {
        Customer customer = customerOpt.get();
        if (customerPatchDto.getAddressId() != null) {
          Optional<Address> addressOpt =
              addressRepository.findByUUID(customerPatchDto.getAddressId());
          if (addressOpt.isPresent()) {
            customer.setAddress(addressOpt.get());
          } else {
            throw new ResourceNotFoundException("Address does not exist.");
          }
        }
        if (customerPatchDto.getFirstName() != null) {
          customer.setFirstName(customerPatchDto.getFirstName());
        }
        if (customerPatchDto.getLastName() != null) {
          customer.setLastName(customerPatchDto.getLastName());
        }
        if (customerPatchDto.getChildren() != null) {
          customer.setChildren(customerPatchDto.getChildren());
        }
        if (customerPatchDto.getSeniors() != null) {
          customer.setSeniors(customerPatchDto.getSeniors());
        }
        if (customerPatchDto.getYear() != null) {
          customer.setYear(customerPatchDto.getYear());
        }
        if (customerPatchDto.getVisitTime() != null) {
          customer.setVisitTime(customerPatchDto.getVisitTime());
        }
        if (customerPatchDto.getVisitRayon() != null) {
          customer.setVisitRayon(customerPatchDto.getVisitRayon());
        }
        if (customerPatchDto.getTransport() != null) {
          customer.setTransport(customerPatchDto.getTransport());
        }
        if (customerPatchDto.getRouteId() != null) {
          Optional<Route> routeOpt = routeRepository.findByUUID(customerPatchDto.getRouteId());
          if (routeOpt.isPresent()) {
            customer.setRoute(routeOpt.get());
          } else {
            throw new ResourceNotFoundException("Route does not exist.");
          }
        }
        customer.setLastModified(new Date());
        customers.add(customer);
      } else {
        throw new ResourceNotFoundException("Customer does not exist");
      }
    }

    customerRepository.saveAll(customers);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping("years")
  public ResponseEntity<List<Integer>> getAvailableYears() {
    List<Integer> years = customerRepository.getAvailableYears();

    return new ResponseEntity<>(years, HttpStatus.OK);
  }

  @PostMapping("upload")
  public ResponseEntity<List<Customer>> uploadCustomers(
      @RequestParam MultipartFile file, Authentication authentication)
      throws IllegalCSVFileException, IOException {
    User user = (User) authentication.getPrincipal();

    if (CSVHelper.hasCSVFormat(file)) {
      List<Customer> customers = csvService.saveCustomers(file, user);

      return new ResponseEntity<>(customers, HttpStatus.CREATED);
    }

    throw new IllegalCSVFileException(
        "Please upload a csv file, the provided file is not a valid csv");
  }

  @GetMapping("get/{year}")
  public ResponseEntity<List<Customer>> getCustomerByYear(@PathVariable Integer year) {
    List<Customer> customers = customerRepository.findByYear(year);
    if (!customers.isEmpty()) {
      return new ResponseEntity<>(customers, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }
}
