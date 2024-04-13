package com.samichlaus.api.domain.customer;

import java.util.Collection;
import java.util.UUID;
import java.util.List;
import java.util.Optional;

import com.samichlaus.api.domain.constants.Rayon;
import com.samichlaus.api.domain.constants.Version;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    @Query(value = "Select * FROM customers where customers.customer_id = :customer_id", nativeQuery = true)
    Optional<Customer> findByUUID(@Param("customer_id") UUID customer_id);
    @NotNull
    Optional<Customer> findById(@NotNull Integer id);
    @Query(value = "SELECT DISTINCT c.year FROM Customer c ORDER BY c.year DESC")
    List<Integer> getAvailableYears();

    List<Customer> findByYearAndVisitRayonAndVersion(int year, Rayon visitRayon, Version version);

    @Query(value = "SELECT * FROM customers WHERE customers.id IN :customerIds", nativeQuery = true)
    List<Customer> findByIdIn(List<Integer> customerIds);
}