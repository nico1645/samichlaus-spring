package com.samichlaus.api.domain.route;

import com.samichlaus.api.domain.customer.Customer;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RouteRepository extends JpaRepository<Route, Integer> {
    @NotNull
    @EntityGraph(attributePaths = {"customers"})
    Optional<Route> findById(@NotNull Integer id);

    @Query(value = "Select * FROM route where route.route_id = :route_id", nativeQuery = true)
    Optional<Route> findByUUID(@Param("route_id") UUID route_id);

}
