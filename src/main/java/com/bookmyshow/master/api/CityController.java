package com.bookmyshow.master.api;

import com.bookmyshow.master.api.dto.CityResponse;
import com.bookmyshow.master.api.dto.CreateCityRequest;
import com.bookmyshow.master.api.dto.UpdateCityRequest;
import com.bookmyshow.master.application.CityService;
import com.bookmyshow.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping({"/api/v1/master/cities", "/api/v1/cities"})
@Tag(name = "Master Data - Cities", description = "Endpoints for discovering service cities and location management")
public class CityController {

    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping
    @Operation(summary = "List or search cities", description = "Retrieves active cities, with optional filtering by popular cities or text search")
    public ResponseEntity<ApiResponse<List<CityResponse>>> getCities(
            @RequestParam(required = false, defaultValue = "false") boolean popular,
            @RequestParam(required = false) String search
    ) {
        List<CityResponse> cities;
        if (popular) {
            cities = cityService.getPopularCities();
        } else if (search != null && !search.isBlank()) {
            cities = cityService.searchCities(search);
        } else {
            cities = cityService.getAllCities();
        }
        return ResponseEntity.ok(ApiResponse.success(cities, "Cities retrieved successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get city by ID", description = "Retrieves detailed information for a specific city")
    public ResponseEntity<ApiResponse<CityResponse>> getCityById(@PathVariable UUID id) {
        CityResponse city = cityService.getCityById(id);
        return ResponseEntity.ok(ApiResponse.success(city, "City retrieved successfully"));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Create city", description = "Creates a new operational city (Admin only)")
    public ResponseEntity<ApiResponse<CityResponse>> createCity(@Valid @RequestBody CreateCityRequest request) {
        CityResponse created = cityService.createCity(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(created, "City created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Update city", description = "Updates details of an existing city (Admin only)")
    public ResponseEntity<ApiResponse<CityResponse>> updateCity(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCityRequest request
    ) {
        CityResponse updated = cityService.updateCity(id, request);
        return ResponseEntity.ok(ApiResponse.success(updated, "City updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Delete city", description = "Soft deletes a city (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteCity(@PathVariable UUID id) {
        cityService.deleteCity(id);
        return ResponseEntity.ok(ApiResponse.success(null, "City deleted successfully"));
    }

    @PatchMapping("/{id}/toggle-popular")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Toggle popular flag", description = "Toggles whether a city appears in the popular cities list (Admin only)")
    public ResponseEntity<ApiResponse<CityResponse>> togglePopular(@PathVariable UUID id) {
        CityResponse updated = cityService.togglePopular(id);
        return ResponseEntity.ok(ApiResponse.success(updated, "City popularity updated successfully"));
    }
}
