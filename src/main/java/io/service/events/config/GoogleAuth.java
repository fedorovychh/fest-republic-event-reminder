package io.service.events.config;

import io.service.events.exception.GoogleAuthenticationException;
import com.google.api.services.calendar.CalendarScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class GoogleAuth {
    private static final List<String> SCOPES =
            Collections.singletonList(CalendarScopes.CALENDAR);

    public HttpCredentialsAdapter getHttpCredentialsAdapter() {
        try {
            GoogleCredentials googleCredentials = GoogleCredentials.getApplicationDefault().createScoped(SCOPES);
            return new HttpCredentialsAdapter(googleCredentials);
        } catch (IOException e) {
            throw new GoogleAuthenticationException("Authentication failed!", e);
        }
    };
}
