package com.samichlaus.api.domain.tour;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;

@Data
public class TourDto {
  @NotNull(message = "Date must not be empty.")
  private LocalDate date;
}
