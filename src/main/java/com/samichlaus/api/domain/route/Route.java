package com.samichlaus.api.domain.route;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.samichlaus.api.domain.constants.Group;
import com.samichlaus.api.domain.constants.Transportation;
import com.samichlaus.api.domain.customer.Customer;
import com.samichlaus.api.domain.tour.Tour;
import com.samichlaus.api.domain.user.User;
import jakarta.persistence.*;


import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties({"id", "user", "lastModified", "createdAt", "route"})
@Table(name = "route")
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Builder.Default()
    @Column(name="route_id", updatable = false, unique=true, length = 16, nullable=false)
    private UUID routeId = UUID.randomUUID();

    @NotNull
    @Builder.Default()
    @Column(name = "samichlaus")
    private String samichlaus = "";
    @NotNull
    @Builder.Default()
    @Column(name = "ruprecht")
    private String ruprecht = "";
    @NotNull
    @Builder.Default()
    @Column(name = "schmutzli")
    private String schmutzli = "";
    @NotNull
    @Builder.Default()
    @Column(name = "engel1")
    private String engel1 = "";
    @NotNull
    @Builder.Default()
    @Column(name = "engel2")
    private String engel2 = "";

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "visit_group")
    private Group group;

    @Column(name = "other_route")
    private Integer route;

    @NotNull
    @Builder.Default()
    @Enumerated(EnumType.STRING)
    @Column(name = "transport_type")
    private Transportation transport = Transportation.foot;

    @NotNull
    @Column(name = "customer_start")
    private LocalTime customerStart;

    @JsonManagedReference
    @Builder.Default()
    @Column(name = "customers_ids")
    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL)
    List<Customer> customers = new ArrayList<>();

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id")
    private Tour tour;

    @Builder.Default()
    @Column(name = "created_at", updatable = false, nullable = false)
    private Date createdAt = new Date();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;
    @Column(name = "last_modified")
    private Date lastModified;

}
