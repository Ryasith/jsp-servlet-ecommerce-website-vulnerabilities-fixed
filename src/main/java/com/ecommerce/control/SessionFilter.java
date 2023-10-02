package com.ecommerce.control;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/*") // Define the URL pattern for which the filter should apply (all URLs in this case)
public class SessionFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Get the current session, but do not create a new one if it doesn't exist
        HttpSession session = httpRequest.getSession(false);

        // Define the URL of your login page
        String loginPage = "/login.jsp";  // Replace with your actual login page URL

        // Check if the user is trying to access a protected resource
        boolean isProtectedResource = isProtectedResource(httpRequest.getRequestURI());

        if (isProtectedResource && session.getAttribute("account") == null) {
            // Redirect to the login page if not authenticated
            httpResponse.sendRedirect(httpRequest.getContextPath() + loginPage);
        } else {
            // Continue processing the request
            chain.doFilter(request, response);
        }
    }

    private boolean isProtectedResource(String uri) {
        // Define the logic to determine if a URL is a protected resource
        // For example, you can check the URL path or use annotations on servlets or JSPs.
        // Return true for protected resources, false for public resources.
        return uri.startsWith("/order-history") || uri.equals("/cart.jsp") || uri.equals("/checkout.jsp") || uri.equals("/checkout");
    }
    // Other filter methods (init and destroy) can be implemented as needed
}
