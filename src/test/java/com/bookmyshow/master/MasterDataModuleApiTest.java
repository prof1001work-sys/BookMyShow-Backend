package com.bookmyshow.master;

import com.bookmyshow.master.api.publicapi.CityPublicDto;
import com.bookmyshow.master.api.publicapi.GenrePublicDto;
import com.bookmyshow.master.api.publicapi.LanguagePublicDto;
import com.bookmyshow.master.application.MasterDataModuleApiImpl;
import com.bookmyshow.master.domain.model.City;
import com.bookmyshow.master.domain.model.Genre;
import com.bookmyshow.master.domain.model.Language;
import com.bookmyshow.master.domain.repository.CertificationRepository;
import com.bookmyshow.master.domain.repository.CityRepository;
import com.bookmyshow.master.domain.repository.GenreRepository;
import com.bookmyshow.master.domain.repository.LanguageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MasterDataModuleApiTest {

    @Mock
    private CityRepository cityRepository;
    @Mock
    private LanguageRepository languageRepository;
    @Mock
    private GenreRepository genreRepository;
    @Mock
    private CertificationRepository certificationRepository;

    private MasterDataModuleApiImpl masterDataModuleApi;

    @BeforeEach
    void setUp() {
        masterDataModuleApi = new MasterDataModuleApiImpl(
                cityRepository,
                languageRepository,
                genreRepository,
                certificationRepository
        );
    }

    @Test
    @DisplayName("findCityById should return CityPublicDto when city exists")
    void shouldFindCityById() {
        UUID cityId = UUID.randomUUID();
        City city = new City("Bengaluru", "Karnataka", "India", "560001", true, null, null);
        city.setId(cityId);

        when(cityRepository.findById(cityId)).thenReturn(Optional.of(city));

        Optional<CityPublicDto> result = masterDataModuleApi.findCityById(cityId);

        assertThat(result).isPresent();
        assertThat(result.get().name()).isEqualTo("Bengaluru");
        assertThat(result.get().isPopular()).isTrue();
    }

    @Test
    @DisplayName("isValidCity should return true for active non-deleted city")
    void shouldValidateActiveCity() {
        UUID cityId = UUID.randomUUID();
        City city = new City("Mumbai", "Maharashtra", "India", "400001", true, null, null);
        city.setId(cityId);

        when(cityRepository.findById(cityId)).thenReturn(Optional.of(city));

        boolean valid = masterDataModuleApi.isValidCity(cityId);

        assertThat(valid).isTrue();
    }

    @Test
    @DisplayName("findLanguageByCode should return LanguagePublicDto")
    void shouldFindLanguageByCode() {
        Language lang = new Language("Hindi", "hi");
        lang.setId(UUID.randomUUID());

        when(languageRepository.findByCode("hi")).thenReturn(Optional.of(lang));

        Optional<LanguagePublicDto> result = masterDataModuleApi.findLanguageByCode("hi");

        assertThat(result).isPresent();
        assertThat(result.get().name()).isEqualTo("Hindi");
    }

    @Test
    @DisplayName("findGenreById should return GenrePublicDto")
    void shouldFindGenreById() {
        UUID genreId = UUID.randomUUID();
        Genre genre = new Genre("Action", "ACTION", "Action description");
        genre.setId(genreId);

        when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));

        Optional<GenrePublicDto> result = masterDataModuleApi.findGenreById(genreId);

        assertThat(result).isPresent();
        assertThat(result.get().code()).isEqualTo("ACTION");
    }
}
