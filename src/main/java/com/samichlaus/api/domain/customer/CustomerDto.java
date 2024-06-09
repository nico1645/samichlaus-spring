package com.samichlaus.api.domain.customer;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.UUID;

import com.samichlaus.api.domain.constants.Group;
import com.samichlaus.api.domain.constants.Rayon;
import com.samichlaus.api.domain.mail.MailStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class CustomerDto {
    
    @NotNull(message = "The address id is required.")
    private UUID addressId;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required") 
    private String lastName;

    @Min(value = 0, message = "The minimum value is 0")
    @Max(value = 100, message = "The maximum value is 100")
    @NotNull(message = "Children count is required")
    private Integer children;

    @Min(value = 0, message = "The minimum value is 0")
    @Max(value = 100, message = "The maximum value is 100")
    @NotNull(message = "Seniors count is required")
    private Integer seniors;
    @Min(value = 1700, message = "The minimum value is 0")
    @Max(value = 10000, message = "The maximum value is 100")
    @NotNull
    private Integer year;

    @NotNull(message = "The visit date is required.")
    private LocalTime visitTime = LocalTime.of(0, 0);
    @NotNull(message = "Rayon is required")
    @Enumerated(EnumType.ORDINAL)
    private Rayon visitRayon;
    private UUID routeId;

    private String email;
    private String phone;
    @Enumerated(EnumType.ORDINAL)
    private MailStatus mailStatus;

    public Customer toCustomer() {
        return Customer.builder()
            .firstName(firstName)
            .lastName(lastName)
            .children(children)
            .seniors(seniors)
                .year(year)
            .visitTime(visitTime)
            .visitRayon(visitRayon)
            .build();
    }
}
