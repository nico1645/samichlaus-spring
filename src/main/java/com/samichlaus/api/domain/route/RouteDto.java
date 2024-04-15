package com.samichlaus.api.domain.route;

import com.samichlaus.api.domain.constants.Group;
import com.samichlaus.api.domain.constants.Transportation;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class RouteDto {
    @NotNull(message = "The group is required.")
    @Enumerated(EnumType.STRING)
    private Group group;
    @NotNull(message = "The transport is required.")
    @Enumerated(EnumType.STRING)
    private Transportation transport;
    @NotNull(message = "The customer start time is required.")
    private LocalTime customerStart;
    @NotNull(message = "The customer end time is required.")
    private LocalTime customerEnd;
    @NotNull(message = "Tour id is required.")
    private UUID tourId;
    @NotNull(message = "Samichlaus is required. Can be empty string.")
    private String samichlaus = "";
    @NotNull(message = "Ruprecht is required. Can be empty string.")
    private String ruprecht = "";
    @NotNull(message = "Schmutzli is required. Can be empty string.")
    private String schmutzli = "";
    @NotNull(message = "Engel1 is required. Can be empty string.")
    private String engel1 = "";
    @NotNull(message = "Engel2 is required. Can be empty string.")
    private String engel2 = "";

    public Route toRoute() {
        return Route.builder()
                .group(group)
                .customerStart(customerStart)
                .customerEnd(customerEnd)
                .transport(transport)
                .samichlaus(samichlaus)
                .ruprecht(ruprecht)
                .schmutzli(schmutzli)
                .engel1(engel1)
                .engel2(engel2)
                .build();
    }
}
