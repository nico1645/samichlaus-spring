package com.samichlaus.api.domain.graphhopper;

import com.samichlaus.api.domain.constants.Transportation;
import com.samichlaus.api.domain.customer.Customer;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;
import lombok.Data;

@Data
public class RouteTime {
  @NotNull private List<Double> depot;
  @NotNull private LocalTime startTime;
  @NotNull private List<Customer> customers;
  @NotNull private LocalTime endTime;

  @NotNull(message = "The transport is required.")
  @Enumerated(EnumType.STRING)
  private Transportation transport;
}
