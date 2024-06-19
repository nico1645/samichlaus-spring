package com.samichlaus.api.domain.tour;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.samichlaus.api.domain.constants.Rayon;
import com.samichlaus.api.domain.constants.Version;
import com.samichlaus.api.domain.route.Route;
import com.samichlaus.api.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties({"id", "user", "lastModified", "created_at", "tour"})
@Table(name = "tour")
public class Tour {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @Builder.Default()
  @Column(name = "tour_id", updatable = false, unique = true, length = 16, nullable = false)
  private final UUID tourId = UUID.randomUUID();

  @JsonManagedReference
  @Builder.Default()
  @Column(name = "route_ids", nullable = false)
  @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL)
  List<Route> routes = new ArrayList<>();

  @NotNull
  @Column(name = "date")
  private LocalDate date;

  @NotNull
  @Column(name = "visit_year")
  private Integer year;

  @NotNull
  @Enumerated(EnumType.ORDINAL)
  @Column(name = "rayon")
  private Rayon rayon;

  @Column(name = "other_tour")
  private Integer tour;

  @NotNull
  @Builder.Default
  @Enumerated(EnumType.ORDINAL)
  @Column(name = "version")
  Version version = Version.TST;

  @NotNull
  @Builder.Default
  @Column(name = "created_at", updatable = false, nullable = false)
  private final Date createdAt = new Date();

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
  private User user;

  @NotNull
  @Column(name = "last_modified")
  private Date lastModified;

  @Transient
  @JsonProperty("rayon")
  public int getRayonValue() {
    return rayon.getValue();
  }
}
