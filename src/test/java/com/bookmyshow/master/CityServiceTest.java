package com.bookmyshow.master;

import com.bookmyshow.master.api.dto.CityResponse;
import com.bookmyshow.master.api.dto.CreateCityRequest;
import com.bookmyshow.master.api.dto.UpdateCityRequest;
import com.bookmyshow.master.application.CityService;
import com.bookmyshow.master.domain.model.City;
import com.bookmyshow.master.domain.model.MasterStatus;
import com.bookmyshow.master.domain.repository.CityRepository;
import com.bookmyshow.shared.exception.ConflictException;
import com.bookmyshow.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CityServiceTest {

    @Mock
    private CityRepository cityRepository;

    private CityService cityService;

    @BeforeEach
    void setUp() {
        cityService = new CityService(cityRepository);
    }

    @Test
    @DisplayName("getAllCities should return all active cities")
    void shouldReturnAllActiveCities() {
        City city1 = new City("Mumbai", "Maharashtra", "India", "400001", true, BigDecimal.valueOf(19.0760), BigDecimal.valueOf(72.8777));
        city1.setId(UUID.randomUUID());
        City city2 = new City("Pune", "Maharashtra", "India", "411001", true, BigDecimal.valueOf(18.5204), BigDecimal.valueOf(73.8567));
        city2.setId(UUID.randomUUID());

        when(cityRepository.findAllActive()).thenReturn(List.of(city1, city2));

        List<CityResponse> result = cityService.getAllCities();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("Mumbai");
        assertThat(result.get(1).name()).isEqualTo("Pune");
        verify(cityRepository).findAllActive();
    }

    @Test
    @DisplayName("getPopularCities should return only popular cities")
    void shouldReturnPopularCities() {
        City popular = new City("Bengaluru", "Karnataka", "India", "560001", true, null, null);
        popular.setId(UUID.randomUUID());

        when(cityRepository.findPopularCities()).thenReturn(List.of(popular));

        List<CityResponse> result = cityService.getPopularCities();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().name()).isEqualTo("Bengaluru");
        assertThat(result.getFirst().isPopular()).isTrue();
    }

    @Test
    @DisplayName("getCityById should return city when found")
    void shouldReturnCityByIdWhenFound() {
        UUID id = UUID.randomUUID();
        City city = new City("Delhi-NCR", "Delhi", "India", "110001", true, null, null);
        city.setId(id);

        when(cityRepository.findById(id)).thenReturn(Optional.of(city));

        CityResponse result = cityService.getCityById(id);

        assertThat(result.id()).isEqualTo(id);
        assertThat(result.name()).isEqualTo("Delhi-NCR");
    }

    @Test
    @DisplayName("getCityById should throw ResourceNotFoundException when not found")
    void shouldThrowWhenCityNotFound() {
        UUID id = UUID.randomUUID();
        when(cityRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cityService.getCityById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("createCity should successfully persist new city")
    void shouldCreateNewCity() {
        CreateCityRequest request = new CreateCityRequest("Hyderabad", "Telangana", "India", "500001", true, null, null);

        when(cityRepository.existsByNameAndState("Hyderabad", "Telangana")).thenReturn(false);
        when(cityRepository.save(any(City.class))).thenAnswer(invocation -> {
            City c = invocation.getArgument(0);
            c.setId(UUID.randomUUID());
            return c;
        });

        CityResponse response = cityService.createCity(request);

        assertThat(response.name()).isEqualTo("Hyderabad");
        assertThat(response.state()).isEqualTo("Telangana");
        assertThat(response.isPopular()).isTrue();
        verify(cityRepository).save(any(City.class));
    }

    @Test
    @DisplayName("createCity should throw ConflictException if city already exists in state")
    void shouldThrowConflictWhenCityExists() {
        CreateCityRequest request = new CreateCityRequest("Mumbai", "Maharashtra", "India", "400001", true, null, null);

        when(cityRepository.existsByNameAndState("Mumbai", "Maharashtra")).thenReturn(true);

        assertThatThrownBy(() -> cityService.createCity(request))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    @DisplayName("updateCity should update existing city details")
    void shouldUpdateCity() {
        UUID id = UUID.randomUUID();
        City existing = new City("Ahmedabad", "Gujarat", "India", "380001", false, null, null);
        existing.setId(id);

        UpdateCityRequest updateReq = new UpdateCityRequest("Ahmedabad", "Gujarat", "India", "380002", true, BigDecimal.valueOf(23.0225), BigDecimal.valueOf(72.5714));

        when(cityRepository.findById(id)).thenReturn(Optional.of(existing));
        when(cityRepository.save(existing)).thenReturn(existing);

        CityResponse updated = cityService.updateCity(id, updateReq);

        assertThat(updated.isPopular()).isTrue();
        assertThat(updated.pincode()).isEqualTo("380002");
        verify(cityRepository).save(existing);
    }

    @Test
    @DisplayName("deleteCity should mark city as deleted and inactive")
    void shouldSoftDeleteCity() {
        UUID id = UUID.randomUUID();
        City existing = new City("Jaipur", "Rajasthan", "India", "302001", true, null, null);
        existing.setId(id);

        when(cityRepository.findById(id)).thenReturn(Optional.of(existing));
        when(cityRepository.save(existing)).thenReturn(existing);

        cityService.deleteCity(id);

        assertThat(existing.isDeleted()).isTrue();
        assertThat(existing.getStatus()).isEqualTo(MasterStatus.INACTIVE);
        verify(cityRepository).save(existing);
    }

    @Test
    @DisplayName("togglePopular should flip popular status")
    void shouldTogglePopularStatus() {
        UUID id = UUID.randomUUID();
        City existing = new City("Chandigarh", "Punjab", "India", "160001", false, null, null);
        existing.setId(id);

        when(cityRepository.findById(id)).thenReturn(Optional.of(existing));
        when(cityRepository.save(existing)).thenReturn(existing);

        CityResponse toggled = cityService.togglePopular(id);

        assertThat(toggled.isPopular()).isTrue();
        verify(cityRepository).save(existing);
    }
}
