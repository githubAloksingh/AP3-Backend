package com.banking.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@AllArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    public static final String AUTHENTICATED_USER_ATTRIBUTE = "authenticatedUser";

    private final AuthTokenService authTokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Allow CORS preflight OPTIONS requests to pass through
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Login required");
            return false;
        }

        String token = authorizationHeader.substring(7);
        AuthTokenService.AuthenticatedUser authenticatedUser = authTokenService.findByToken(token)
                .orElse(null);

        if (authenticatedUser == null) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid or expired token");
            return false;
        }

        request.setAttribute(AUTHENTICATED_USER_ATTRIBUTE, authenticatedUser);

        String uri = request.getRequestURI();
        String method = request.getMethod();
        String role = authenticatedUser.role();

        if (uri.startsWith("/api/customers")) {
            if (uri.equals("/api/customers") || uri.equals("/api/customers/")) {
                if ("GET".equalsIgnoreCase(method) && !"ROLE_ADMIN".equals(role)) {
                    response.sendError(HttpStatus.FORBIDDEN.value(), "Access denied");
                    return false;
                }
            } else {
                String suffix = uri.substring("/api/customers/".length()).split("/")[0];
                try {
                    Long targetCustomerId = Long.parseLong(suffix);
                    if (!"ROLE_ADMIN".equals(role) && !targetCustomerId.equals(authenticatedUser.customerId())) {
                        response.sendError(HttpStatus.FORBIDDEN.value(), "Access denied");
                        return false;
                    }
                } catch (NumberFormatException e) {
                    // Let controller handle path variables formatting
                }
            }
        }

        if (uri.startsWith("/api/accounts")) {
            if (uri.equals("/api/accounts") || uri.equals("/api/accounts/")) {
                if ("GET".equalsIgnoreCase(method)) {
                    String customerIdParam = request.getParameter("customerId");
                    if (customerIdParam == null) {
                        if (!"ROLE_ADMIN".equals(role)) {
                            response.sendError(HttpStatus.FORBIDDEN.value(), "Access denied");
                            return false;
                        }
                    } else {
                        try {
                            Long targetCustomerId = Long.parseLong(customerIdParam);
                            if (!"ROLE_ADMIN".equals(role) && !targetCustomerId.equals(authenticatedUser.customerId())) {
                                response.sendError(HttpStatus.FORBIDDEN.value(), "Access denied");
                                return false;
                            }
                        } catch (NumberFormatException e) {
                            // Let controller handle parameter formatting
                        }
                    }
                }
            }
        }

        return true;
    }
}
