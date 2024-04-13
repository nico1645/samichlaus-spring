package com.samichlaus.api.domain.route;

import com.samichlaus.api.domain.constants.Group;
import com.samichlaus.api.domain.constants.Transportation;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class RouteDto {
    @NotNull(message = "The route id is required.")
    private UUID routeId;
    @NotNull(message = "The group is required.")
    @Enumerated(EnumType.STRING)
    private Group group;
    @NotNull(message = "The transport is required.")
    @Enumerated(EnumType.STRING)
    private Transportation transport;
    @NotNull(message = "The visitDate is required.")
    private Date visitStart;
    @NotNull(message = "The visitIds array is required.")
    private List<UUID> visitIds;

}
