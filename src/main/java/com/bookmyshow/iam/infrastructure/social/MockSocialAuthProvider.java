package com.bookmyshow.iam.infrastructure.social;

import org.springframework.stereotype.Component;

@Component
public class MockSocialAuthProvider implements SocialAuthProvider {

    @Override
    public String getProviderName() {
        return "GOOGLE";
    }

    @Override
    public SocialUserProfile verifyToken(String idToken) {
        // Stub implementation returning mock user profile
        return new SocialUserProfile("google-12345", "social.user@example.com", "Social", "User");
    }
}
