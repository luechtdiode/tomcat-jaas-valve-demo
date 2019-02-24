package ch.seidel.tomcattest;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * handles the JWT (verify a jwt and issue a new jwt if userprincipal is given)
 */
public class SecurityValve extends ValveBase {
    private static final String AUTH_HEADER_KEY = "Authorization";
    private static final String AUTH_HEADER_VALUE_PREFIX = "Bearer "; // with trailing space to separate token
    private static Users users = new Users();

    public void invoke(Request request, Response response) throws IOException, ServletException {
        System.out.println("Security Valve: invoke");
        verifyToken(request);
        // Invoke next valve
        getNext().invoke(request, response);
        if (request.getUserPrincipal() != null) {
            String token = users.createJwt(request.getUserPrincipal());
            response.addHeader(AUTH_HEADER_KEY, AUTH_HEADER_VALUE_PREFIX + token);
        }
        System.out.println("Security Valve: exit invoke");
    }

    private void verifyToken(HttpServletRequest request) {
        String jwt = getBearerToken(request);

        if (jwt != null && !jwt.isEmpty()) {
            try {
                request.login(jwt, "");
                System.out.println("Logged in using JWT");
            } catch (ServletException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No JWT provided, go on unauthenticated");
        }
    }

    private String getBearerToken(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTH_HEADER_KEY);
        if (authHeader != null && authHeader.startsWith(AUTH_HEADER_VALUE_PREFIX)) {
            return authHeader.substring(AUTH_HEADER_VALUE_PREFIX.length());
        }
        return null;
    }
}
