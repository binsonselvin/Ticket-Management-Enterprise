package com.sk.workitem.app.service.impl;

import java.util.Date;
import java.util.UUID;

import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;

import com.sk.workitem.app.service.helper.HttpHelper;

public class TokenGenerator {

    public static PersistentRememberMeToken createToken(String username) {
        String seriesId = generateSeriesId();
        String tokenValue = generateTokenValue();
        Date dateTime = new Date();  // Current date-time

        return new PersistentRememberMeToken(username, seriesId, tokenValue, dateTime);
    }

    private static String generateSeriesId() {
        return HttpHelper.generateSecureRandomString(24);
    }

    private static String generateTokenValue() {
        return HttpHelper.generateSecureRandomString(24);
    }
}
