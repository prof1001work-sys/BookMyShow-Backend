package com.bookmyshow.iam.infrastructure.social;

public interface SocialAuthProvider {

    String getProviderName();

    SocialUserProfile verifyToken(String idToken);

    record SocialUserProfile(String providerId, String email, String firstName, String lastName) {}
}
