package com.samichlaus.api.domain.tour;

import com.samichlaus.api.domain.constants.Rayon;
import com.samichlaus.api.domain.constants.Version;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface TourRepository extends JpaRepository<Tour, Integer> {

    @EntityGraph(attributePaths = {"routes"})
    Optional<Tour> findTourByYearAndRayonAndVersion(int year, Rayon rayon, Version version);

    @Query(value = "Select * FROM tour where tour.tour_id = :tour_id", nativeQuery = true)
    Optional<Tour> findTourByUUID(@Param("tour_id") UUID tour_id);

}
