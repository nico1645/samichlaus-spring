package com.samichlaus.api.services;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.samichlaus.api.domain.constants.Group;
import com.samichlaus.api.domain.constants.Version;
import com.samichlaus.api.domain.route.Route;
import com.samichlaus.api.domain.tour.Tour;
import com.samichlaus.api.domain.tour.TourRepository;
import com.samichlaus.api.domain.user.UserRepository;
import com.samichlaus.api.domain.vrp.CSVObject;
import com.samichlaus.api.exception.IllegalCSVFileException;
import jakarta.transaction.Transactional;
import org.opengis.referencing.operation.TransformException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.samichlaus.api.helpers.CSVHelper;
import com.samichlaus.api.domain.address.Address;
import com.samichlaus.api.domain.address.AddressRepository;
import com.samichlaus.api.domain.customer.Customer;
import com.samichlaus.api.domain.customer.CustomerRepository;
import com.samichlaus.api.domain.user.User;

@Service
public class CSVService {
  final
  AddressRepository addressRepository;
  final
  CustomerRepository customerRepository;

  final UserRepository userRepository;
  final TourRepository tourRepository;

  public CSVService(AddressRepository addressRepository, CustomerRepository customerRepository, UserRepository userRepository, TourRepository tourRepository) {
    this.tourRepository = tourRepository;
    this.userRepository = userRepository;
    this.addressRepository = addressRepository;
    this.customerRepository = customerRepository;
  }

  public void saveAddresses(MultipartFile file) throws TransformException, IllegalCSVFileException {
    try {
      List<Address> addresses = CSVHelper.csvToAddresses(file.getInputStream(), addressRepository);
      addressRepository.saveAll(addresses);
     
    } catch (IOException e) {
      throw new RuntimeException("fail to store csv data: " + e.getMessage());
    }
  }
  public List<Customer> saveCustomers(MultipartFile file, User user) throws IllegalCSVFileException, IOException {
      CSVObject csvObject = CSVHelper.csvToCustomers(file.getInputStream(), addressRepository, user);
      List<Customer> customers = csvObject.getCustomers();
      customers = customerRepository.saveAll(customers);
      for (int i = 0; i < customers.size(); i++) {
        Customer _customer = customers.get(i);
        Optional<Tour> tourOpt = tourRepository.findTourByYearAndRayonAndVersion(_customer.getYear(), _customer.getVisitRayon(), Version.TST);
        if (tourOpt.isPresent()) {
          Tour tour = tourOpt.get();
          tour.setLastModified(new Date());
          List<Route> routes = tour.getRoutes();
          for (Route route : routes) {
            if (route.getGroup() == Group.Z) {
              _customer.setRoute(route);
              route.getCustomers().add(_customer);
              route.setLastModified(new Date());
              break;
            }
          }
          tourRepository.save(tour);
        } else {
          Tour newTour = Tour.builder()
                  .rayon(_customer.getVisitRayon())
                  .year(_customer.getYear())
                  .version(Version.TST)
                  .date(csvObject.getDates().get(i))
                  .lastModified(new Date())
                  .user(user)
                  .build();
          Route newRoute = Route.builder()
                  .customerStart(_customer.getVisitTime())
                  .user(user)
                  .group(Group.Z)
                  .tour(newTour)
                  .lastModified(new Date())
                  .build();
          newRoute.getCustomers().add(_customer);
          newTour.getRoutes().add(newRoute);
          tourRepository.save(newTour);
        }
      }
      return customers;
  }

  public List<Address> getAllAddresses() {
    return addressRepository.findAll();
  }
}
