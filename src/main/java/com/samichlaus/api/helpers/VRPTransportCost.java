package com.samichlaus.api.helpers;

import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.cost.VehicleRoutingTransportCosts;
import com.graphhopper.jsprit.core.problem.driver.Driver;
import com.graphhopper.jsprit.core.problem.vehicle.Vehicle;

public class VRPTransportCost implements VehicleRoutingTransportCosts {

    private final long[][] timeMatrix;

    public VRPTransportCost(long[][] timeMatrix) {
        this.timeMatrix = timeMatrix;
    }

    @Override
    public double getTransportTime(Location from, Location to, double departureTime, Driver driver, Vehicle vehicle) {
        return timeMatrix[from.getIndex()][to.getIndex()];
    }

    @Override
    public double getBackwardTransportTime(Location from, Location to, double arrivalTime, Driver driver, Vehicle vehicle) {
        return timeMatrix[to.getIndex()][from.getIndex()];
    }

    @Override
    public double getTransportCost(Location from, Location to, double departureTime, Driver driver, Vehicle vehicle) {
        return timeMatrix[from.getIndex()][to.getIndex()];
    }

    @Override
    public double getBackwardTransportCost(Location from, Location to, double arrivalTime, Driver driver, Vehicle vehicle) {
        return timeMatrix[to.getIndex()][from.getIndex()];
    }

    @Override
    public double getDistance(Location from, Location to, double departureTime, Vehicle vehicle) {
        return timeMatrix[to.getIndex()][from.getIndex()];
    }

}
