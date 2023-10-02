package com.ecommerce.control;

import com.ecommerce.control.OAuthServlet;
import com.ecommerce.dao.AccountDao;
import com.ecommerce.entity.Account;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

@WebServlet("/oauth2callback/")
public class OAuthCallbackServlet extends HttpServlet {

    AccountDao accountDao = new AccountDao();

    private void executeGoogleLogin(HttpServletRequest request, HttpServletResponse response, Account account) throws IOException {
        // Get the status of remember me checkbox.
        HttpSession session = request.getSession();
        session.setAttribute("account", account);
        response.sendRedirect("/");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String authorizationCode = req.getParameter("code");

        GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                new NetHttpTransport(),
                new JacksonFactory(),
                "https://accounts.google.com/o/oauth2/token",
                "296800126007-9u42ob4mfd6vb3bmno6c16nusu44jiaa.apps.googleusercontent.com",
                "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",
                authorizationCode,
                "http://localhost:8080/oauth2callback/"
        ).execute();

        String accessToken = tokenResponse.getAccessToken();
        String refreshToken = tokenResponse.getRefreshToken(); // If available

        HttpRequestInitializer initializer = new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) throws IOException {
                request.getHeaders().setAuthorization("Bearer " + accessToken);
            }
        };

        Oauth2 oauth2 = new Oauth2(new NetHttpTransport(), new JacksonFactory(), initializer);
        Userinfo userInfo = oauth2.userinfo().get().execute();

        // Get the Google login user details
        String name = userInfo.getName();
        String email = userInfo.getEmail();
        String uId = userInfo.getId();
        String profileImage = userInfo.getPicture();

        // Save the user details in the session
        req.getSession().setAttribute("name", name);
        req.getSession().setAttribute("email", email);
        req.getSession().setAttribute("profileImage", profileImage);

        URL url = new URL(profileImage);
        // Open a connection to the URL
        URLConnection connection = url.openConnection();

        // Get the input stream from the connection
        InputStream inputStream = connection.getInputStream();

        if (accountDao.checkUsernameExists(email)) {
            Account account = accountDao.checkGoogleLoginAccount(email, uId);
            executeGoogleLogin(req, resp, account);
        }else{
            accountDao.createAccount(email, null, inputStream, uId);
            Account account = accountDao.checkGoogleLoginAccount(email, uId);

            if (account != null){
                executeGoogleLogin(req, resp, account);
            }else{
                resp.sendRedirect("/");
            }
        }
    }
}
