package org.rishi.profiling.Profile;

import java.lang.reflect.Method;
import java.util.Arrays;
//import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import com.sun.jersey.core.util.Base64;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
@Provider
public class BasicAuth implements ContainerRequestFilter {
	private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
    private static final String BASIC_AUTH_PREFIX = "Basic ";

    private static final String VALID_USERNAME = "your_valid_username";
    private static final String VALID_PASSWORD = "your_valid_password";

    @Override
    public ContainerRequest filter(ContainerRequest request) {
        String authHeader = request.getHeaderValue(AUTHORIZATION_HEADER_KEY);

        if (authHeader != null && authHeader.startsWith(BASIC_AUTH_PREFIX)) {
            String encodedCredentials = authHeader.substring(BASIC_AUTH_PREFIX.length()).trim();
            String credentials = Base64.base64Decode(encodedCredentials);

            String[] userPass = credentials.split(":");
            String username = userPass[0];
            String password = userPass[1];

            if (isValidCredentials(username, password)) {
                return request;
            }
        }

        // If credentials are invalid or not provided, return a 401 Unauthorized response
        throw new WebApplicationException(Response.Status.UNAUTHORIZED);
    }

    private boolean isValidCredentials(String username, String password) {
        // You can implement your own logic to validate the username and password
        return VALID_USERNAME.equals(username) && VALID_PASSWORD.equals(password);
    }
}
