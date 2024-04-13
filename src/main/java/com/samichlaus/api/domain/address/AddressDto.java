package com.samichlaus.api.domain.address;

import com.samichlaus.api.domain.constants.Rayon;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class AddressDto {
    private Float longitude;
    private Float latitude;
    private Float dkode;
    private Float dkodn;
    private Float gkode;
    private Float gkodn;
    @NotBlank(message = "Address is required")
    private String address;
    @NotBlank(message = "City is required")
    private String zipName;
    @Min(value = 1, message = "The minimum value is 1")
    @Max(value = 3, message = "The maximum value is 3")
    @NotNull(message = "Rayon is required")
    private Integer rayon;
    @Min(value = 1, message = "The minimum value is 1")
    @Max(value = 9999, message = "The maximum value is 9999")
    @NotNull(message = "Zip code is required")
    private Integer zipCode;

    public Address toAddress() {
        return Address.builder()
            .longitude(longitude)
            .latitude(latitude)
            .dkode(dkode)
            .dkodn(dkodn)
            .gkode(gkode)
            .gkodn(gkodn)
            .address(address)
            .zipName(zipName)
            .rayon(Rayon.fromValue(rayon))
            .zipCode(zipCode)
            .build();
    }
}
