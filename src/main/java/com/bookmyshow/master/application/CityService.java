package com.bookmyshow.master.application;

import com.bookmyshow.master.api.dto.CityResponse;
import com.bookmyshow.master.api.dto.CreateCityRequest;
import com.bookmyshow.master.api.dto.UpdateCityRequest;
import com.bookmyshow.master.domain.model.City;
import com.bookmyshow.master.domain.repository.CityRepository;
import com.bookmyshow.shared.configuration.RedisCacheConfig;
import com.bookmyshow.shared.exception.ConflictException;
import com.bookmyshow.shared.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Application service for City operations with multi-level Redis caching.
 */
@Service
public class CityService {

    private static final Logger log = LoggerFactory.getLogger(CityService.class);

    private final CityRepository cityRepository;

    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = RedisCacheConfig.CACHE_CITIES, key = "'all'")
    public List<CityResponse> getAllCities() {
        log.debug("Fetching all active cities from database (cache miss)");
        return cityRepository.findAllActive().stream()
                .map(CityResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = RedisCacheConfig.CACHE_POPULAR_CITIES, key = "'popular'")
    public List<CityResponse> getPopularCities() {
        log.debug("Fetching popular cities from database (cache miss)");
        return cityRepository.findPopularCities().stream()
                .map(CityResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = RedisCacheConfig.CACHE_CITY_DETAIL, key = "#id")
    public CityResponse getCityById(UUID id) {
        log.debug("Fetching city with ID {} from database (cache miss)", id);
        return cityRepository.findById(id)
                .map(CityResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("City", id));
    }

    @Transactional(readOnly = true)
    public List<CityResponse> searchCities(String query) {
        return cityRepository.searchCities(query).stream()
                .map(CityResponse::from)
                .toList();
    }

    @Transactional
    @CacheEvict(value = {RedisCacheConfig.CACHE_CITIES, RedisCacheConfig.CACHE_POPULAR_CITIES, RedisCacheConfig.CACHE_CITY_DETAIL}, allEntries = true)
    public CityResponse createCity(CreateCityRequest request) {
        if (cityRepository.existsByNameAndState(request.name().trim(), request.state().trim())) {
            throw new ConflictException("City '" + request.name() + "' already exists in state '" + request.state() + "'");
        }

        City city = new City(
                request.name().trim(),
                request.state().trim(),
                request.country() != null ? request.country().trim() : "India",
                request.pincode() != null ? request.pincode().trim() : null,
                request.isPopular(),
                request.latitude(),
                request.longitude()
        );

        City saved = cityRepository.save(city);
        log.info("Created new city: {} ({}) with ID: {}", saved.getName(), saved.getState(), saved.getId());
        return CityResponse.from(saved);
    }

    @Transactional
    @CacheEvict(value = {RedisCacheConfig.CACHE_CITIES, RedisCacheConfig.CACHE_POPULAR_CITIES, RedisCacheConfig.CACHE_CITY_DETAIL}, allEntries = true)
    public CityResponse updateCity(UUID id, UpdateCityRequest request) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("City", id));

        // Check if updating name and state creates a conflict with another city
        if (!city.getName().equalsIgnoreCase(request.name().trim()) || !city.getState().equalsIgnoreCase(request.state().trim())) {
            if (cityRepository.existsByNameAndState(request.name().trim(), request.state().trim())) {
                throw new ConflictException("City '" + request.name() + "' already exists in state '" + request.state() + "'");
            }
        }

        city.setName(request.name().trim());
        city.setState(request.state().trim());
        if (request.country() != null) {
            city.setCountry(request.country().trim());
        }
        city.setPincode(request.pincode() != null ? request.pincode().trim() : null);
        if (request.isPopular() != null) {
            city.setPopular(request.isPopular());
        }
        city.setLatitude(request.latitude());
        city.setLongitude(request.longitude());

        City updated = cityRepository.save(city);
        log.info("Updated city ID: {} - {} ({})", updated.getId(), updated.getName(), updated.getState());
        return CityResponse.from(updated);
    }

    @Transactional
    @CacheEvict(value = {RedisCacheConfig.CACHE_CITIES, RedisCacheConfig.CACHE_POPULAR_CITIES, RedisCacheConfig.CACHE_CITY_DETAIL}, allEntries = true)
    public void deleteCity(UUID id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("City", id));

        city.markDeleted();
        city.deactivate();
        cityRepository.save(city);
        log.info("Soft deleted city ID: {} - {}", city.getId(), city.getName());
    }

    @Transactional
    @CacheEvict(value = {RedisCacheConfig.CACHE_CITIES, RedisCacheConfig.CACHE_POPULAR_CITIES, RedisCacheConfig.CACHE_CITY_DETAIL}, allEntries = true)
    public CityResponse togglePopular(UUID id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("City", id));

        if (city.isPopular()) {
            city.unmarkAsPopular();
        } else {
            city.markAsPopular();
        }

        City updated = cityRepository.save(city);
        log.info("Toggled popular status for city ID {}: {}", id, updated.isPopular());
        return CityResponse.from(updated);
    }
}
