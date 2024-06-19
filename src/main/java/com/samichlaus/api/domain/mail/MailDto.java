package com.samichlaus.api.domain.mail;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
import lombok.Data;

@Data
public class MailDto {
  @NotNull @NotBlank private String email;
  @NotNull private LocalTime visitTime;
  @NotNull private LocalDate visitDate;
  @NotNull private UUID customerId;
}
