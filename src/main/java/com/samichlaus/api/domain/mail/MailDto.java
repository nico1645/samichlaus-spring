package com.samichlaus.api.domain.mail;

import com.samichlaus.api.domain.customer.Customer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;


@Data
public class MailDto {
    @NotNull
    @NotBlank
    private String email;
    @NotNull
    private LocalTime visitTime;
    @NotNull
    private LocalDate visitDate;
    @NotNull
    private UUID customerId;
}
