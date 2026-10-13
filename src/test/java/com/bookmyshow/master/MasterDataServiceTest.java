package com.bookmyshow.master;

import com.bookmyshow.master.api.dto.CertificationResponse;
import com.bookmyshow.master.api.dto.CreateCertificationRequest;
import com.bookmyshow.master.api.dto.CreateGenreRequest;
import com.bookmyshow.master.api.dto.CreateLanguageRequest;
import com.bookmyshow.master.api.dto.GenreResponse;
import com.bookmyshow.master.api.dto.LanguageResponse;
import com.bookmyshow.master.application.MasterDataService;
import com.bookmyshow.master.domain.model.Certification;
import com.bookmyshow.master.domain.model.Genre;
import com.bookmyshow.master.domain.model.Language;
import com.bookmyshow.master.domain.repository.CertificationRepository;
import com.bookmyshow.master.domain.repository.GenreRepository;
import com.bookmyshow.master.domain.repository.LanguageRepository;
import com.bookmyshow.shared.exception.ConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MasterDataServiceTest {

    @Mock
    private LanguageRepository languageRepository;
    @Mock
    private GenreRepository genreRepository;
    @Mock
    private CertificationRepository certificationRepository;

    private MasterDataService masterDataService;

    @BeforeEach
    void setUp() {
        masterDataService = new MasterDataService(languageRepository, genreRepository, certificationRepository);
    }

    @Test
    @DisplayName("getAllLanguages should return list of active languages")
    void shouldReturnAllActiveLanguages() {
        Language l1 = new Language("Hindi", "hi");
        l1.setId(UUID.randomUUID());
        Language l2 = new Language("English", "en");
        l2.setId(UUID.randomUUID());

        when(languageRepository.findAllActive()).thenReturn(List.of(l1, l2));

        List<LanguageResponse> result = masterDataService.getAllLanguages();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).code()).isEqualTo("hi");
        assertThat(result.get(1).code()).isEqualTo("en");
    }

    @Test
    @DisplayName("createLanguage should save when code and name are unique")
    void shouldCreateLanguage() {
        CreateLanguageRequest request = new CreateLanguageRequest("Telugu", "te");

        when(languageRepository.existsByCode("te")).thenReturn(false);
        when(languageRepository.existsByName("Telugu")).thenReturn(false);
        when(languageRepository.save(any(Language.class))).thenAnswer(inv -> {
            Language l = inv.getArgument(0);
            l.setId(UUID.randomUUID());
            return l;
        });

        LanguageResponse response = masterDataService.createLanguage(request);

        assertThat(response.name()).isEqualTo("Telugu");
        assertThat(response.code()).isEqualTo("te");
        verify(languageRepository).save(any(Language.class));
    }

    @Test
    @DisplayName("createLanguage should throw ConflictException when code exists")
    void shouldThrowConflictWhenLanguageCodeExists() {
        CreateLanguageRequest request = new CreateLanguageRequest("Hindi", "hi");
        when(languageRepository.existsByCode("hi")).thenReturn(true);

        assertThatThrownBy(() -> masterDataService.createLanguage(request))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    @DisplayName("getAllGenres should return list of active genres")
    void shouldReturnAllActiveGenres() {
        Genre g1 = new Genre("Action", "ACTION", "High-energy");
        g1.setId(UUID.randomUUID());
        Genre g2 = new Genre("Sci-Fi", "SCIFI", "Futuristic");
        g2.setId(UUID.randomUUID());

        when(genreRepository.findAllActive()).thenReturn(List.of(g1, g2));

        List<GenreResponse> result = masterDataService.getAllGenres();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).code()).isEqualTo("ACTION");
    }

    @Test
    @DisplayName("createGenre should save when code is unique")
    void shouldCreateGenre() {
        CreateGenreRequest request = new CreateGenreRequest("Animation", "ANIMATION", "Animated cartoons");

        when(genreRepository.existsByCode("ANIMATION")).thenReturn(false);
        when(genreRepository.existsByName("Animation")).thenReturn(false);
        when(genreRepository.save(any(Genre.class))).thenAnswer(inv -> {
            Genre g = inv.getArgument(0);
            g.setId(UUID.randomUUID());
            return g;
        });

        GenreResponse response = masterDataService.createGenre(request);

        assertThat(response.code()).isEqualTo("ANIMATION");
        verify(genreRepository).save(any(Genre.class));
    }

    @Test
    @DisplayName("getAllCertifications should return all certifications ordered by age")
    void shouldReturnCertifications() {
        Certification c1 = new Certification("U", "Universal", "All ages", 0);
        c1.setId(UUID.randomUUID());
        Certification c2 = new Certification("A", "Adults", "18+", 18);
        c2.setId(UUID.randomUUID());

        when(certificationRepository.findAll()).thenReturn(List.of(c1, c2));

        List<CertificationResponse> result = masterDataService.getAllCertifications();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).code()).isEqualTo("U");
        assertThat(result.get(1).code()).isEqualTo("A");
    }

    @Test
    @DisplayName("createCertification should save when code is unique")
    void shouldCreateCertification() {
        CreateCertificationRequest request = new CreateCertificationRequest("UA", "Parental Guidance", "Under 12 accompanied", 12);

        when(certificationRepository.existsByCode("UA")).thenReturn(false);
        when(certificationRepository.save(any(Certification.class))).thenAnswer(inv -> {
            Certification c = inv.getArgument(0);
            c.setId(UUID.randomUUID());
            return c;
        });

        CertificationResponse response = masterDataService.createCertification(request);

        assertThat(response.code()).isEqualTo("UA");
        assertThat(response.minimumAge()).isEqualTo(12);
        verify(certificationRepository).save(any(Certification.class));
    }
}
