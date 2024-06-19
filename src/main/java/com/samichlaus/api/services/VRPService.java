package com.samichlaus.api.services;

import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem.FleetSize;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.samichlaus.api.domain.vrp.VRPVisit;
import com.samichlaus.api.helpers.VRPTransportCost;
import java.util.List;

@org.springframework.stereotype.Service
public class VRPService {

  private final GraphhopperService graphhopperService;
  final int WEIGHT_INDEX = 0;
  final int WEIGHT_VISIT_INDEX = 1;

  public VRPService(GraphhopperService graphhopperService) {
    this.graphhopperService = graphhopperService;
  }

  public VehicleRoutingProblem createVehicleRoutingProblem(
      List<VRPVisit> vrpVisits,
      Integer maxVehiclesPerGroup,
      Integer maxGroups,
      Integer maxVisitTime) {
    VRPTransportCost vrpTransportCost =
        new VRPTransportCost(graphhopperService.calculateTimeMatrix(vrpVisits));
    VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
    vrpBuilder.setFleetSize(FleetSize.FINITE);
    vrpBuilder.setRoutingCost(vrpTransportCost);
    addVisitJobs(vrpBuilder, vrpVisits);

    for (int i = 0; i < maxGroups; i++) {

      VehicleTypeImpl.Builder vehicleTypeBuilder =
          VehicleTypeImpl.Builder.newInstance("groupTransportType")
              .addCapacityDimension(WEIGHT_INDEX, maxVehiclesPerGroup)
              .addCapacityDimension(WEIGHT_VISIT_INDEX, maxVisitTime)
              .setProfile("foot");
      VehicleType vehicleType = vehicleTypeBuilder.build();

      VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder.newInstance("group-" + i);
      vehicleBuilder.setStartLocation(Location.newInstance(0));
      vehicleBuilder.setType(vehicleType);
      VehicleImpl vehicle = vehicleBuilder.build();
      vrpBuilder.addVehicle(vehicle);
    }

    return vrpBuilder.build();
  }

  private void addVisitJobs(VehicleRoutingProblem.Builder vrpBuilder, List<VRPVisit> customers) {
    for (int i = 0; i < customers.size(); i++) {
      VRPVisit vrpVisit = customers.get(i);
      vrpBuilder.addJob(
          Service.Builder.newInstance(i + "")
              .addSizeDimension(WEIGHT_INDEX, 1)
              .addSizeDimension(WEIGHT_VISIT_INDEX, vrpVisit.getCapacity())
              .setLocation(Location.newInstance(i + 1))
              .build());
    }
  }
}
