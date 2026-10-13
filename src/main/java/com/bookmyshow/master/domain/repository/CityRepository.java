package com.bookmyshow.master.domain.repository;

import com.bookmyshow.master.domain.model.City;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain repository contract for City aggregate.
 */
public interface CityRepository {

    City save(City city);

    Optional<City> findById(UUID id);

    Optional<City> findByNameAndState(String name, String state);

    boolean existsByNameAndState(String name, String state);

    List<City> findAllActive();

    List<City> findPopularCities();

    List<City> searchCities(String query);
}
