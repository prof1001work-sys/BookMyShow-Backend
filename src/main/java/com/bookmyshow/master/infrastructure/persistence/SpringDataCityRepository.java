package com.bookmyshow.master.infrastructure.persistence;

import com.bookmyshow.master.domain.model.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataCityRepository extends JpaRepository<City, UUID> {

    Optional<City> findByNameIgnoreCaseAndStateIgnoreCaseAndDeletedAtIsNull(String name, String state);

    boolean existsByNameIgnoreCaseAndStateIgnoreCaseAndDeletedAtIsNull(String name, String state);

    @Query("SELECT c FROM City c WHERE c.status = 'ACTIVE' AND c.deletedAt IS NULL ORDER BY c.name ASC")
    List<City> findAllActive();

    @Query("SELECT c FROM City c WHERE c.isPopular = true AND c.status = 'ACTIVE' AND c.deletedAt IS NULL ORDER BY c.name ASC")
    List<City> findPopularCities();

    @Query("SELECT c FROM City c WHERE (LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(c.state) LIKE LOWER(CONCAT('%', :query, '%'))) AND c.status = 'ACTIVE' AND c.deletedAt IS NULL ORDER BY c.name ASC")
    List<City> searchCities(@Param("query") String query);
}
