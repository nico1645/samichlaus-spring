package com.samichlaus.api.controller;

import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.algorithm.termination.TimeTermination;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.job.Job;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import com.graphhopper.jsprit.core.util.Solutions;
import com.samichlaus.api.domain.constants.*;
import com.samichlaus.api.domain.customer.Customer;
import com.samichlaus.api.domain.customer.CustomerRepository;
import com.samichlaus.api.domain.route.Route;
import com.samichlaus.api.domain.route.RouteRepository;
import com.samichlaus.api.domain.tour.Tour;
import com.samichlaus.api.domain.tour.TourRepository;
import com.samichlaus.api.domain.user.User;
import com.samichlaus.api.domain.vrp.ConfigVRP;
import com.samichlaus.api.domain.vrp.VRPVisit;
import com.samichlaus.api.exception.ResourceNotFoundException;
import com.samichlaus.api.services.VRPService;
import com.samichlaus.api.services.XLSService;
import jakarta.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/vrp")
public class VRPController {

  private final CustomerRepository customerRepository;

  private final RouteRepository routeRepository;
  private final TourRepository tourRepository;

  private final XLSService xlsService;

  private final VRPService vrpService;

  public VRPController(
      VRPService vrpService,
      CustomerRepository customerRepository,
      XLSService xlsService,
      TourRepository tourRepository,
      RouteRepository routeRepository) {
    this.vrpService = vrpService;
    this.customerRepository = customerRepository;
    this.xlsService = xlsService;
    this.routeRepository = routeRepository;
    this.tourRepository = tourRepository;
  }

  @PostMapping("solve/{year}/{rayon}")
  public ResponseEntity<Tour> solveVRP(
      @PathVariable Integer year,
      @PathVariable Integer rayon,
      @Valid @RequestBody ConfigVRP config,
      Authentication authentication)
      throws ResourceNotFoundException {
    User user = (User) authentication.getPrincipal();

    List<Customer> customers =
        customerRepository.findByYearAndVisitRayonAndVersion(
            year, Rayon.fromValue(rayon), Version.TST);

    if (customers.isEmpty()) {
      throw new ResourceNotFoundException("No data for year and rayon");
    }

    for (Customer c : customers) {
      c.setRoute(null);
      c.setTransport(Transportation.foot);
    }
    customers = customerRepository.saveAllAndFlush(customers);

    LocalTime startTime = config.getStartTime();
    LocalDate visitDate = LocalDate.now();

    HashMap<Integer, VRPVisit> visitHashMap = new HashMap<>();
    for (Customer c : customers) {
      if (!visitHashMap.containsKey(c.getAddress().getId())) {
        VRPVisit vrpVisit =
            VRPVisit.builder()
                .address(c.getAddress())
                .customers(new ArrayList<>(List.of(c)))
                .capacity(Constants.getChildrenSeniorCapacity(c.getChildren(), c.getSeniors()))
                .build();
        visitHashMap.put(c.getAddress().getId(), vrpVisit);
      } else {
        VRPVisit vrpVisit = visitHashMap.get(c.getAddress().getId());
        vrpVisit.getCustomers().add(c);
        vrpVisit.setCapacity(
            vrpVisit.getCapacity()
                + Constants.getChildrenSeniorCapacity(c.getChildren(), c.getSeniors()));
      }
    }
    List<VRPVisit> visits = new ArrayList<>(visitHashMap.values());

    VehicleRoutingProblem problem =
        vrpService.createVehicleRoutingProblem(
            visits,
            config.getMaxVehiclesPerGroup(),
            config.getMaxGroup(),
            config.getMaxVisitTimePerGroup());

    /*
     * get the algorithm out-of-the-box.
     */
    TimeTermination prematureTermination = new TimeTermination(config.getMaxSeconds() * 1000);
    VehicleRoutingAlgorithm algorithm = Jsprit.createAlgorithm(problem);
    algorithm.setPrematureAlgorithmTermination(prematureTermination);
    algorithm.addListener(prematureTermination);

    /*
     * and search a solution which returns a collection of solutions (here only one solution is constructed)
     */
    Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();
    /*
     * use the static helper-method in the utility class Solutions to get the best solution (in terms of least costs)
     */
    VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);

    // Delete previous tour
    Optional<Tour> t =
        tourRepository.findTourByYearAndRayonAndVersion(year, Rayon.fromValue(rayon), Version.TST);
    if (t.isPresent()) {
      Tour tmpTour = t.get();
      visitDate = tmpTour.getDate();
      routeRepository.deleteAll(tmpTour.getRoutes());
      tourRepository.delete(tmpTour);
    }

    Tour tour =
        Tour.builder()
            .rayon(Rayon.fromValue(rayon))
            .year(year)
            .version(Version.TST)
            .date(visitDate)
            .user(user)
            .lastModified(new Date())
            .build();

    Collection<VehicleRoute> routes = bestSolution.getRoutes();
    List<String> groups = Arrays.asList("A", "B", "C", "D", "E", "F", "G");
    int routeNumber = 0;

    for (VehicleRoute vehicleRoute : routes) {
      Route route =
          Route.builder()
              .tour(tour)
              .customerStart(startTime)
              .group(Group.fromValue(groups.get(routeNumber)))
              .user(user)
              .lastModified(new Date())
              .build();
      int visitTimeInMinutes = 0;
      for (TourActivity activity : vehicleRoute.getActivities()) {
        String jobId;
        VRPVisit vrpVisit;
        if (activity instanceof TourActivity.JobActivity) {
          jobId = ((TourActivity.JobActivity) activity).getJob().getId();
          vrpVisit = visits.get(Integer.parseInt(jobId));
          for (Customer customer : vrpVisit.getCustomers()) {
            LocalTime time = startTime.plusMinutes(visitTimeInMinutes);
            customer.setVisitTime(
                time.plusMinutes(Math.round(activity.getArrTime() / 1000 / 60 / 5) * 5));
            customer.setLastModified(new Date());
            customer.setRoute(route);
            visitTimeInMinutes +=
                Constants.getChildrenSeniorCapacity(customer.getChildren(), customer.getSeniors());
            route.getCustomers().add(customer);
          }
        }
      }
      LocalTime time = startTime.plusMinutes(visitTimeInMinutes);
      route.setCustomerEnd(
          time.plusMinutes(Math.round(vehicleRoute.getEnd().getArrTime() / 1000 / 60 / 5) * 5));

      tour.getRoutes().add(route);

      routeNumber++;
      startTime = startTime.plusMinutes(10);
    }

    Route route =
        Route.builder()
            .tour(tour)
            .group(Group.Z)
            .customerStart(LocalTime.of(0, 0))
            .user(user)
            .lastModified(new Date())
            .build();
    for (Job job : bestSolution.getUnassignedJobs()) {
      VRPVisit vrpVisit = visits.get(Integer.parseInt(job.getId()));
      for (Customer customer : vrpVisit.getCustomers()) {
        customer.setRoute(route);
        route.getCustomers().add(customer);
      }
    }

    tour.getRoutes().add(route);

    tour = tourRepository.save(tour);

    // SolutionPrinter.print(problem, bestSolution, SolutionPrinter.Print.CONCISE);
    // SolutionPrinter.print(problem, bestSolution, SolutionPrinter.Print.VERBOSE);

    return new ResponseEntity<>(tour, HttpStatus.OK);
  }

  @GetMapping("excel/{year}")
  public ResponseEntity<byte[]> downloadExcel(@PathVariable Integer year) throws Exception {
    ByteArrayOutputStream outputStream = xlsService.getExcel(year);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    headers.setContentDispositionFormData("attachment", "Laufliste_%d.xlsm".formatted(year));
    byte[] byteArray = outputStream.toByteArray();
    outputStream.close();
    return ResponseEntity.ok().headers(headers).body(byteArray);
  }

  @GetMapping("revert/{year}/{rayon}")
  public ResponseEntity<Tour> revertChangesForYearAndRayon(
      @PathVariable Integer year, @PathVariable Integer rayon) {

    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }

  @GetMapping("commit/{year}/{rayon}")
  public ResponseEntity<Tour> commitChangesForYearAndRayon(
      @PathVariable Integer year, @PathVariable Integer rayon) {

    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }
}
