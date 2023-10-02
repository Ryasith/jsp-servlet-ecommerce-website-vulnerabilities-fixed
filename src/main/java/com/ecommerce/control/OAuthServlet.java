package com.ecommerce.control;

import com.google.api.client.auth.oauth2.*;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow.Builder;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@WebServlet("/google-login")
public class OAuthServlet extends HttpServlet {
    private static final String CLIENT_ID = "296800126007-9u42ob4mfd6vb3bmno6c16nusu44jiaa.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "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";
    private static final String REDIRECT_URI = "http://localhost:8080/oauth2callback/";
    private static final String[] SCOPES = {"openid", "email", "profile"};

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AuthorizationCodeFlow flow = createFlow();
        AuthorizationCodeRequestUrl authorizationUrl = flow.newAuthorizationUrl();
        authorizationUrl.setRedirectUri(REDIRECT_URI);
        String url = authorizationUrl.build();
        resp.sendRedirect(url);
    }

    public AuthorizationCodeFlow createFlow() {
        return new Builder(
                BearerToken.authorizationHeaderAccessMethod(),
                new NetHttpTransport(),
                new JacksonFactory(),
                new GenericUrl("https://accounts.google.com/o/oauth2/token"),
                new ClientParametersAuthentication(CLIENT_ID, CLIENT_SECRET),
                CLIENT_ID,
                "https://accounts.google.com/o/oauth2/auth"
        )
                .setScopes(Arrays.asList(SCOPES))
                .build();
    }
}
