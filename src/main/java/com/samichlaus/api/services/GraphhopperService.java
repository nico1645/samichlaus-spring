package com.samichlaus.api.services;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import com.samichlaus.api.domain.address.Address;
import com.samichlaus.api.domain.constants.Constants;
import com.samichlaus.api.domain.customer.Customer;
import com.samichlaus.api.domain.vrp.VRPVisit;
import org.springframework.stereotype.Service;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.config.Profile;
import com.samichlaus.api.config.YAMLConfig;


@Service
public class GraphhopperService {

    private final GraphHopper hopper = new GraphHopper();

                      
    private GraphhopperService(YAMLConfig config) {
        this.hopper
            .setOSMFile(config.getPathToOsmFile())
            .setGraphHopperLocation(config.getPathToGraphhopperData())
            .setProfiles(
                    List.of(new Profile("foot").setVehicle("foot").setWeighting("custom").setTurnCosts(false),
                    new Profile("car").setVehicle("car").setWeighting("custom").setTurnCosts(true))
                )
                .setElevation(true)
                .importOrLoad();
    }
                                    

    public double calculateDistance(double fromLat, double fromLon, double toLat, double toLon, String profile) {
        GHRequest request = new GHRequest(fromLat, fromLon, toLat, toLon)
                .setProfile(profile)
                .setAlgorithm("dijkstra")
                .setLocale(Locale.GERMAN);
        GHResponse response = hopper.route(request);
        if (response.hasErrors()) {
            throw new RuntimeException("GraphHopper routing error: " + response.getErrors());
        }
        return response.getBest().getDistance();
    }

    public long calculateTime(double fromLat, double fromLon, double toLat, double toLon, String profile) {
        GHRequest request = new GHRequest(fromLat, fromLon, toLat, toLon)
                .setProfile(profile)
                .setAlgorithm("dijkstra")
                .setLocale(Locale.GERMAN);
        GHResponse response = hopper.route(request);
        if (response.hasErrors()) {
            throw new RuntimeException("GraphHopper routing error: " + response.getErrors());
        }
        return response.getBest().getTime();
    }

    public double[][] calculateDistanceMatrix(List<Customer> customers) {
        int n = customers.size()+1;

        double[][] distanceMatrix = new double[n][n];

        // Calculate distances for all pairs of locations
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // Compute distance between locations i and j
                if (i == 0 && j == 0) {
                    distanceMatrix[i][j] = 0;
                } else if (i == 0){
                    Address c2 = customers.get(j - 1).getAddress();
                    distanceMatrix[i][j] = calculateDistance(Constants.DEPOT_LAT_LNG.get(0), Constants.DEPOT_LAT_LNG.get(1), c2.getLatitude(), c2.getLongitude(), "foot");
                } else if (j == 0){
                    Address c1 = customers.get(i - 1).getAddress();
                    distanceMatrix[i][j] = calculateDistance(c1.getLatitude(), c1.getLongitude(), Constants.DEPOT_LAT_LNG.get(0), Constants.DEPOT_LAT_LNG.get(1), "foot");
                } else {
                    Address c2 = customers.get(j - 1).getAddress();
                    Address c1 = customers.get(i - 1).getAddress();
                    distanceMatrix[i][j] = calculateDistance(c1.getLatitude(), c1.getLongitude(), c2.getLatitude(), c2.getLongitude(), "foot");
                }
            }
        }

        return distanceMatrix;
    }

    public long[][] calculateTimeMatrix(List<VRPVisit> customers) {
        int n = customers.size() + 1;
        long[][] distanceMatrix = new long[n][n];

        // Calculate distances for all pairs of locations
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // Compute distance between locations i and j
                if (i == 0 && j == 0) {
                    distanceMatrix[i][j] = 0;
                } else if (i == 0){
                    Address c2 = customers.get(j - 1).getAddress();
                    distanceMatrix[i][j] = calculateTime(Constants.DEPOT_LAT_LNG.get(0), Constants.DEPOT_LAT_LNG.get(1), c2.getLatitude(), c2.getLongitude(), "foot");
                } else if (j == 0){
                    Address c1 = customers.get(i - 1).getAddress();
                    distanceMatrix[i][j] = calculateTime(c1.getLatitude(), c1.getLongitude(), Constants.DEPOT_LAT_LNG.get(0), Constants.DEPOT_LAT_LNG.get(1), "foot");
                } else {
                    Address c2 = customers.get(j - 1).getAddress();
                    Address c1 = customers.get(i - 1).getAddress();
                    distanceMatrix[i][j] = calculateTime(c1.getLatitude(), c1.getLongitude(), c2.getLatitude(), c2.getLongitude(), "foot");
                }
            }
        }

        return distanceMatrix;
    }
}
