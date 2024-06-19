package com.samichlaus.api.domain.address;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.samichlaus.api.domain.constants.Rayon;
import jakarta.persistence.*;
import java.util.Date;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties({
  "id",
  "hibernateLazyInitializer",
  "handler",
  "dkode",
  "dkodn",
  "gkode",
  "gkodn",
  "createdAt"
})
@Table(name = "addresses")
public class Address {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @Builder.Default()
  @Column(name = "address_id", updatable = false, unique = true, length = 16, nullable = false)
  private UUID addressId = UUID.randomUUID();

  private Float longitude;
  private Float latitude;
  private Float dkode;
  private Float dkodn;
  private Float gkode;
  private Float gkodn;

  @Column(name = "address", unique = true, nullable = false)
  private String address;

  @Column(name = "zip_name")
  private String zipName;

  @Builder.Default
  @Column(name = "created_at", updatable = false, nullable = false)
  private Date createdAt = new Date();

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "rayon")
  private Rayon rayon;

  @Column(name = "zip_code")
  private Integer zipCode;

  @Transient
  @JsonProperty("rayon")
  public int getRayonValue() {
    return rayon.getValue();
  }
}
