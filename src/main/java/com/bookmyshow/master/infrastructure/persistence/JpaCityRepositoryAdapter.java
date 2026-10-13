package com.bookmyshow.master.infrastructure.persistence;

import com.bookmyshow.master.domain.model.City;
import com.bookmyshow.master.domain.repository.CityRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaCityRepositoryAdapter implements CityRepository {

    private final SpringDataCityRepository springDataCityRepository;

    public JpaCityRepositoryAdapter(SpringDataCityRepository springDataCityRepository) {
        this.springDataCityRepository = springDataCityRepository;
    }

    @Override
    public City save(City city) {
        return springDataCityRepository.save(city);
    }

    @Override
    public Optional<City> findById(UUID id) {
        return springDataCityRepository.findById(id).filter(c -> !c.isDeleted());
    }

    @Override
    public Optional<City> findByNameAndState(String name, String state) {
        return springDataCityRepository.findByNameIgnoreCaseAndStateIgnoreCaseAndDeletedAtIsNull(name, state);
    }

    @Override
    public boolean existsByNameAndState(String name, String state) {
        return springDataCityRepository.existsByNameIgnoreCaseAndStateIgnoreCaseAndDeletedAtIsNull(name, state);
    }

    @Override
    public List<City> findAllActive() {
        return springDataCityRepository.findAllActive();
    }

    @Override
    public List<City> findPopularCities() {
        return springDataCityRepository.findPopularCities();
    }

    @Override
    public List<City> searchCities(String query) {
        if (query == null || query.isBlank()) {
            return findAllActive();
        }
        return springDataCityRepository.searchCities(query.trim());
    }
}
