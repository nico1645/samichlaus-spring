package com.samichlaus.api.domain.tour;

import com.samichlaus.api.domain.constants.Rayon;
import com.samichlaus.api.domain.constants.Version;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TourRepository extends JpaRepository<Tour, Integer> {

    @EntityGraph(attributePaths = {"routes"})
    Optional<Tour> findTourByYearAndRayonAndVersion(int year, Rayon rayon, Version version);

}
