package com.bookmyshow.iam;

import com.bookmyshow.iam.infrastructure.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        String secret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
        String issuer = "bookmyshow-test";
        jwtTokenProvider = new JwtTokenProvider(secret, issuer, 15, 7);
    }

    @Test
    @DisplayName("Successfully generates and validates a valid JWT access token")
    void shouldGenerateAndValidateAccessToken() {
        UUID userId = UUID.randomUUID();
        String email = "test.customer@example.com";
        List<String> roles = List.of("ROLE_CUSTOMER");

        String token = jwtTokenProvider.generateAccessToken(userId, email, roles);

        assertThat(token).isNotBlank();
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
        assertThat(jwtTokenProvider.extractUserId(token)).isEqualTo(userId);
        assertThat(jwtTokenProvider.extractRoles(token)).containsExactly("ROLE_CUSTOMER");
        assertThat(jwtTokenProvider.extractJti(token)).isNotBlank();
    }

    @Test
    @DisplayName("Rejects malformed or tampered token")
    void shouldRejectMalformedToken() {
        String invalidToken = "eyAiYWxnIjogIkhTMjU2IiB9.invalidPayload.invalidSignature";
        assertThat(jwtTokenProvider.validateToken(invalidToken)).isFalse();
    }
}
