package com.samichlaus.api.domain.route;

import com.samichlaus.api.domain.constants.Group;
import com.samichlaus.api.domain.constants.Transportation;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;
import java.util.UUID;

@Data
public class RoutePatchDto {
    @NotNull
    private UUID routeId;
    @Enumerated(EnumType.STRING)
    private Group group;
    @Enumerated(EnumType.STRING)
    private Transportation transport;
    private LocalTime customerStart;
    private LocalTime customerEnd;
    private UUID tourId;
    private String samichlaus = "";
    private String ruprecht = "";
    private String schmutzli = "";
    private String engel1 = "";
    private String engel2 = "";

}
