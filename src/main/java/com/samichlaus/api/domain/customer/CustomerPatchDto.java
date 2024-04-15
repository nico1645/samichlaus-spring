package com.samichlaus.api.domain.customer;

import com.samichlaus.api.domain.constants.Rayon;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;
import java.util.UUID;

@Data
public class CustomerPatchDto {
    @NotNull
    private UUID customerId;
    private UUID addressId;
    private String firstName;
    private String lastName;
    @Min(value = 0, message = "The minimum value is 0")
    @Max(value = 100, message = "The maximum value is 100")
    private Integer children;
    @Min(value = 0, message = "The minimum value is 0")
    @Max(value = 100, message = "The maximum value is 100")
    private Integer seniors;
    @Min(value = 1700, message = "The minimum value is 0")
    @Max(value = 10000, message = "The maximum value is 100")
    private Integer year;
    private LocalTime visitTime = LocalTime.of(0, 0);
    @Enumerated(EnumType.STRING)
    private Rayon visitRayon;
    private UUID routeId;
}
