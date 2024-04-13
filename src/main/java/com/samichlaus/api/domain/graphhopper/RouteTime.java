package com.samichlaus.api.domain.graphhopper;

import java.time.LocalTime;
import java.util.List;


import com.samichlaus.api.domain.customer.Customer;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;

@Data
public class RouteTime {
    @NotNull
    private List<Double> depot;
    @NotNull
    private LocalTime startTime;
    @NotNull
    private List<Customer> customers;
    @Null
    private LocalTime endTime;
}
