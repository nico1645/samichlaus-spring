package com.samichlaus.api.domain.route;

import com.samichlaus.api.domain.customer.Customer;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RouteRepository extends JpaRepository<Route, Integer> {
    @NotNull
    @EntityGraph(attributePaths = {"customers"})
    Optional<Route> findById(@NotNull Integer id);

    @Query(value = "SELECT * FROM routes WHERE routes.route_id IN :routeIds", nativeQuery = true)
    List<Route> findByRouteIdIn(List<UUID> routeIds);
    @Query(value = "SELECT * FROM routes WHERE routes.id IN :routeIds", nativeQuery = true)
    List<Route> findByIdIn(List<Integer> routeIds);

}
