package com.samichlaus.api.domain.customer;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.samichlaus.api.domain.address.Address;
import com.samichlaus.api.domain.constants.Rayon;
import com.samichlaus.api.domain.constants.Transportation;
import com.samichlaus.api.domain.constants.Version;
import com.samichlaus.api.domain.mail.MailStatus;
import com.samichlaus.api.domain.route.Route;
import com.samichlaus.api.domain.user.User;
import jakarta.persistence.*;
import java.time.LocalTime;
import java.util.Date;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties({"id", "user", "lastModified", "createdAt", "customer"})
@Table(name = "customers")
public class Customer implements Comparable<Customer> {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @Builder.Default()
  @Column(name = "customer_id", updatable = false, unique = true, length = 16, nullable = false)
  private UUID customerId = UUID.randomUUID();

  @NotNull
  @Column(name = "first_name")
  private String firstName;

  @NotNull
  @Column(name = "last_name")
  private String lastName;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "address_id", referencedColumnName = "id", nullable = false)
  private Address address;

  @NotNull private Integer children;
  @NotNull private Integer seniors;

  @NotNull
  @Column(name = "visit_year")
  private Integer year;

  @NotNull
  @Builder.Default
  @Enumerated(EnumType.ORDINAL)
  @Column(name = "transport_type")
  private Transportation transport = Transportation.foot;

  @NotNull
  @Column(name = "visit_time")
  private LocalTime visitTime;

  @NotNull
  @Enumerated(EnumType.ORDINAL)
  @Column(name = "visit_rayon")
  private Rayon visitRayon;

  @Column(name = "link")
  private String link;

  @Column(name = "email")
  private String email;

  @Column(name = "phone")
  private String phone;

  @NotNull
  @Builder.Default
  @Enumerated(EnumType.ORDINAL)
  @Column(name = "version")
  com.samichlaus.api.domain.constants.Version version = Version.TST;

  @Builder.Default
  @Column(name = "created_at", updatable = false, nullable = false)
  private Date createdAt = new Date();

  @Column(name = "other_customer")
  private Integer customer;

  @JsonBackReference
  @ManyToOne
  @JoinColumn(name = "route_id")
  private Route route;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
  private User user;

  @Column(name = "last_modified")
  private Date lastModified;

  @Builder.Default
  @Enumerated(EnumType.ORDINAL)
  @Column(name = "mail_status", nullable = false)
  private MailStatus mailStatus = MailStatus.NOT_SENT;

  @Transient
  @JsonProperty("visitRayon")
  public int getRayonValue() {
    return visitRayon.getValue();
  }

  @Override
  public int compareTo(@NotNull Customer o) {
    return getVisitTime().compareTo(o.getVisitTime());
  }
}
