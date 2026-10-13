package com.bookmyshow.master.api;

import com.bookmyshow.master.api.dto.CertificationResponse;
import com.bookmyshow.master.api.dto.CreateCertificationRequest;
import com.bookmyshow.master.api.dto.CreateGenreRequest;
import com.bookmyshow.master.api.dto.CreateLanguageRequest;
import com.bookmyshow.master.api.dto.GenreResponse;
import com.bookmyshow.master.api.dto.LanguageResponse;
import com.bookmyshow.master.application.MasterDataService;
import com.bookmyshow.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/master")
@Tag(name = "Master Data - Cinema Reference", description = "Endpoints for cinema reference data: languages, genres, and certifications")
public class MasterDataController {

    private final MasterDataService masterDataService;

    public MasterDataController(MasterDataService masterDataService) {
        this.masterDataService = masterDataService;
    }

    // --- Languages ---

    @GetMapping("/languages")
    @Operation(summary = "Get all languages", description = "Retrieves all active cinema audio/subtitle languages")
    public ResponseEntity<ApiResponse<List<LanguageResponse>>> getLanguages() {
        List<LanguageResponse> languages = masterDataService.getAllLanguages();
        return ResponseEntity.ok(ApiResponse.success(languages, "Languages retrieved successfully"));
    }

    @PostMapping("/languages")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Create language", description = "Creates a new cinema language (Admin only)")
    public ResponseEntity<ApiResponse<LanguageResponse>> createLanguage(@Valid @RequestBody CreateLanguageRequest request) {
        LanguageResponse response = masterDataService.createLanguage(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "Language created successfully"));
    }

    // --- Genres ---

    @GetMapping("/genres")
    @Operation(summary = "Get all genres", description = "Retrieves all movie genres")
    public ResponseEntity<ApiResponse<List<GenreResponse>>> getGenres() {
        List<GenreResponse> genres = masterDataService.getAllGenres();
        return ResponseEntity.ok(ApiResponse.success(genres, "Genres retrieved successfully"));
    }

    @PostMapping("/genres")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Create genre", description = "Creates a new movie genre (Admin only)")
    public ResponseEntity<ApiResponse<GenreResponse>> createGenre(@Valid @RequestBody CreateGenreRequest request) {
        GenreResponse response = masterDataService.createGenre(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "Genre created successfully"));
    }

    // --- Certifications ---

    @GetMapping("/certifications")
    @Operation(summary = "Get all certifications", description = "Retrieves all cinema censor ratings/certifications")
    public ResponseEntity<ApiResponse<List<CertificationResponse>>> getCertifications() {
        List<CertificationResponse> certifications = masterDataService.getAllCertifications();
        return ResponseEntity.ok(ApiResponse.success(certifications, "Certifications retrieved successfully"));
    }

    @PostMapping("/certifications")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Create certification", description = "Creates a new movie certification rating (Admin only)")
    public ResponseEntity<ApiResponse<CertificationResponse>> createCertification(@Valid @RequestBody CreateCertificationRequest request) {
        CertificationResponse response = masterDataService.createCertification(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "Certification created successfully"));
    }
}
