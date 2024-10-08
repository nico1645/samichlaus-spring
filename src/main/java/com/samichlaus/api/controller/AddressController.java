package com.samichlaus.api.controller;

import com.samichlaus.api.domain.address.Address;
import com.samichlaus.api.domain.address.AddressDto;
import com.samichlaus.api.domain.address.AddressRepository;
import com.samichlaus.api.domain.constants.Rayon;
import com.samichlaus.api.exception.IllegalCSVFileException;
import com.samichlaus.api.helpers.CSVHelper;
import com.samichlaus.api.services.CSVService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.opengis.referencing.operation.TransformException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Limit;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/address")
@Validated
@RequiredArgsConstructor
public class AddressController {

  private AddressRepository addressRepository;

  private CSVService csvService;

  @Autowired
  public AddressController(AddressRepository addressRepository, CSVService csvService) {
    this.addressRepository = addressRepository;
    this.csvService = csvService;
  }

  @PostMapping("")
  public ResponseEntity<Address> createAddress(@RequestBody @Valid AddressDto addressDto) {

    Address address = addressRepository.save(addressDto.toAddress());
    return new ResponseEntity<>(address, HttpStatus.CREATED);
  }

  @GetMapping("{id}")
  public ResponseEntity<Address> getAddressByUUID(@PathVariable("id") UUID uuid) {
    Optional<Address> address = addressRepository.findByUUID(uuid);
    return address
        .map(value -> new ResponseEntity<>(value, HttpStatus.OK))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @GetMapping("")
  public ResponseEntity<List<Address>> getAllAddresses(
      @RequestParam(required = false) @Size(max = 25, message = "Size must be between 0 and 25")
          String address,
      @RequestParam(required = false) Integer limit) {
    List<Address> addresses = new ArrayList<>();

    if (address == null) addresses.addAll(addressRepository.findAll());
    else if (limit == null)
      addresses.addAll(
          addressRepository.findByAddressContainingIgnoreCaseOrderByAddress(
              address, Limit.unlimited()));
    else
      addresses.addAll(
          addressRepository.findByAddressContainingIgnoreCaseOrderByAddress(
              address, Limit.of(limit)));

    if (addresses.isEmpty()) {
      return new ResponseEntity<>(addresses, HttpStatus.NO_CONTENT);
    }
    addresses.sort(
        (a1, a2) -> {
          // Split addresses into street and house number
          String[] parts1 = a1.getAddress().split("\\s+", 2); // Split by first whitespace
          String[] parts2 = a2.getAddress().split("\\s+", 2);

          // Compare street names
          int streetComparison = parts1[0].compareTo(parts2[0]);
          if (streetComparison != 0) {
            return streetComparison;
          }

          // Extract house numbers and optional letters
          int houseNumber2 = 0;
          Integer houseNumber1 = 0;
          try {
            try {
              houseNumber1 = parts1.length > 1 ? Integer.parseInt(parts1[1]) : 0;
            } catch (Exception ex) {
              houseNumber1 = Integer.parseInt(parts1[1].substring(0, parts1[1].length() - 1));
            }
            try {
              houseNumber2 = parts2.length > 1 ? Integer.parseInt(parts2[1]) : 0;
            } catch (Exception ex) {
              houseNumber2 = Integer.parseInt(parts2[1].substring(0, parts2[1].length() - 1));
            }
          } catch (Exception ee) {
            return 0;
          }

          // Compare house numbers
          return houseNumber1.compareTo(houseNumber2);
        });

    return new ResponseEntity<>(addresses, HttpStatus.OK);
  }

  @PutMapping("{id}")
  public ResponseEntity<Address> updateAddress(
      @PathVariable("id") UUID uuid, @Valid @RequestBody AddressDto addressDto) {
    Optional<Address> address = addressRepository.findByUUID(uuid);

    if (address.isPresent()) {
      Address _addresses = address.get();
      _addresses.setAddress(addressDto.getAddress());
      _addresses.setLongitude(addressDto.getLongitude());
      _addresses.setLatitude(addressDto.getLatitude());
      _addresses.setDkode(addressDto.getDkode());
      _addresses.setDkodn(addressDto.getDkodn());
      _addresses.setGkode(addressDto.getGkode());
      _addresses.setGkodn(addressDto.getGkodn());
      _addresses.setZipName(addressDto.getZipName());
      _addresses.setZipCode(addressDto.getZipCode());
      _addresses.setRayon(Rayon.fromValue(addressDto.getRayon()));
      return new ResponseEntity<>(addressRepository.save(_addresses), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @DeleteMapping("{id}")
  public ResponseEntity<HttpStatus> deleteAddress(@PathVariable("id") UUID uuid) {
    Optional<Address> address = addressRepository.findByUUID(uuid);
    if (address.isPresent()) {
      addressRepository.deleteById(address.get().getId());
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @PostMapping("upload")
  public ResponseEntity<List<Address>> uploadAddresses(@RequestParam MultipartFile file)
      throws IllegalCSVFileException, TransformException {

    if (CSVHelper.hasCSVFormat(file)) {
      csvService.saveAddresses(file);

      return new ResponseEntity<>(csvService.getAllAddresses(), HttpStatus.CREATED);
    }

    throw new IllegalCSVFileException("Upload a valid csv file.");
  }
}
