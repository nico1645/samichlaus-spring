package com.samichlaus.api.domain.address;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AddressRepository extends JpaRepository<Address, Integer> {
  List<Address> findByAddressContainingIgnoreCaseOrderByAddress(String address, Limit limit);

  List<Address> findByAddress(String address);

  @Query(
      value = "Select * FROM addresses where addresses.address_id = :address_id",
      nativeQuery = true)
  Optional<Address> findByUUID(@Param("address_id") UUID addressId);

  @NotNull
  Optional<Address> findById(@NotNull Integer id);
}
