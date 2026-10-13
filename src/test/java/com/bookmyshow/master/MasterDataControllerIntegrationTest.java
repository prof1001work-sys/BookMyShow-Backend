package com.bookmyshow.master;

import com.bookmyshow.master.api.dto.CreateCertificationRequest;
import com.bookmyshow.master.api.dto.CreateGenreRequest;
import com.bookmyshow.master.api.dto.CreateLanguageRequest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MasterDataControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Public user can list languages, genres, and certifications")
    void shouldFetchReferenceDataPublicly() throws Exception {
        mockMvc.perform(get("/api/v1/master/languages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(get("/api/v1/master/genres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(get("/api/v1/master/certifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin can create new language, genre, and certification")
    void shouldAllowAdminToCreateMasterData() throws Exception {
        // 1. Language
        CreateLanguageRequest langReq = new CreateLanguageRequest("Spanish", "es");
        mockMvc.perform(post("/api/v1/master/languages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(langReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Spanish"))
                .andExpect(jsonPath("$.data.code").value("es"));

        // 2. Genre
        CreateGenreRequest genreReq = new CreateGenreRequest("Documentary", "DOCU", "Real events");
        mockMvc.perform(post("/api/v1/master/genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(genreReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Documentary"))
                .andExpect(jsonPath("$.data.code").value("DOCU"));

        // 3. Certification
        CreateCertificationRequest certReq = new CreateCertificationRequest("PG-13", "Parents Strongly Cautioned", "Under 13 caution", 13);
        mockMvc.perform(post("/api/v1/master/certifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(certReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.code").value("PG-13"))
                .andExpect(jsonPath("$.data.minimumAge").value(13));
    }

    @Test
    @DisplayName("Unauthenticated user cannot create master data")
    void shouldRejectUnauthenticatedMasterDataCreation() throws Exception {
        CreateLanguageRequest langReq = new CreateLanguageRequest("German", "de");
        mockMvc.perform(post("/api/v1/master/languages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(langReq)))
                .andExpect(status().isUnauthorized());
    }
}
