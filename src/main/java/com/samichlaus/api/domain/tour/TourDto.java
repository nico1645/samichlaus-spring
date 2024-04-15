package com.samichlaus.api.domain.tour;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TourDto {
    @NotNull(message = "Date must not be empty.")
    private LocalDate date;
}
