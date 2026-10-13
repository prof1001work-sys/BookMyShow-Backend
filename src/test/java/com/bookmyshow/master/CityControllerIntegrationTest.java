package com.bookmyshow.master;

import com.bookmyshow.master.api.dto.CreateCityRequest;
import com.bookmyshow.master.api.dto.UpdateCityRequest;
import com.bookmyshow.master.domain.model.City;
import com.bookmyshow.master.domain.repository.CityRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CityControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CityRepository cityRepository;

    @Test
    @DisplayName("Public user can fetch all active cities")
    void shouldGetAllCitiesPublicly() throws Exception {
        City c = new City("CityPublic1", "State1", "India", "111111", false, null, null);
        cityRepository.save(c);

        mockMvc.perform(get("/api/v1/master/cities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("Public user can fetch popular cities")
    void shouldGetPopularCitiesPublicly() throws Exception {
        City popular = new City("CityPopular1", "StatePopular1", "India", "222222", true, null, null);
        cityRepository.save(popular);

        mockMvc.perform(get("/api/v1/master/cities?popular=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("Public user can search cities by prefix")
    void shouldSearchCitiesPublicly() throws Exception {
        City searchTarget = new City("SearchTargetCity", "TargetState", "India", "333333", false, null, null);
        cityRepository.save(searchTarget);

        mockMvc.perform(get("/api/v1/master/cities?search=SearchTarget"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").value("SearchTargetCity"));
    }

    @Test
    @DisplayName("Unauthenticated request to create city should return 401 Unauthorized")
    void shouldRejectCreateCityWhenUnauthenticated() throws Exception {
        CreateCityRequest req = new CreateCityRequest("UnauthorizedCity", "StateX", "India", "444444", false, null, null);

        mockMvc.perform(post("/api/v1/master/cities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    @DisplayName("Customer role should be forbidden from creating city (403)")
    void shouldForbidCustomerFromCreatingCity() throws Exception {
        CreateCityRequest req = new CreateCityRequest("ForbiddenCity", "StateY", "India", "555555", false, null, null);

        mockMvc.perform(post("/api/v1/master/cities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin can perform complete CRUD lifecycle on city")
    void shouldPerformAdminCityCrudLifecycle() throws Exception {
        // 1. Create
        CreateCityRequest createReq = new CreateCityRequest("AdminTestCity", "AdminState", "India", "666666", false, BigDecimal.valueOf(12.34), BigDecimal.valueOf(56.78));

        String responseContent = mockMvc.perform(post("/api/v1/master/cities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("AdminTestCity"))
                .andReturn().getResponse().getContentAsString();

        String cityId = objectMapper.readTree(responseContent).path("data").path("id").asText();

        // 2. Update
        UpdateCityRequest updateReq = new UpdateCityRequest("AdminTestCityUpdated", "AdminState", "India", "666667", true, BigDecimal.valueOf(12.34), BigDecimal.valueOf(56.78));

        mockMvc.perform(put("/api/v1/master/cities/" + cityId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("AdminTestCityUpdated"))
                .andExpect(jsonPath("$.data.isPopular").value(true));

        // 3. Toggle popular
        mockMvc.perform(patch("/api/v1/master/cities/" + cityId + "/toggle-popular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.isPopular").value(false));

        // 4. Delete
        mockMvc.perform(delete("/api/v1/master/cities/" + cityId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
