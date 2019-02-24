package ch.seidel.tomcattest;

import io.jsonwebtoken.*;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.security.auth.Subject;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Users {
    // should be a database-connection...
    private static final Map<String, Subject> users = new HashMap<>();

    private static final SecureRandom random = new SecureRandom();
    private static final JwtManager JWT_MANAGER = new JwtManager();

    static {
        addUser("testuser", "resutset",
                "testrole", "tomcat", "manager-gui", "manager-script", "admin-gui", "admin-script");
    }

    public static void clear() {
        users.clear();
    }

    public static void addUser(String username, String password, String... roles) {
        Subject s = new Subject();
        s.getPrincipals().add(new SampleUserPrincipal(username));
        String key = createKey(username);
        s.getPublicCredentials().add(key);
        s.getPrivateCredentials().add(hashed(password));
        for (String role : roles) {
            s.getPrincipals().add(new SampleRolePrincipal(role.trim()));
        }
        if (users.containsKey(key)) {
            throw new RuntimeException("user exists already!");
        }
        users.put(key, s);
    }

    public Subject find(String userid, String password) {
        if (userid == null) {
            userid = "somefancyhackername";
        }
        if (password == null) {
            password = "somefancyhackerpassword";
        }

        Subject subject = users.get(createKey(userid));
        if (subject != null) {
            for (Object pcred : subject.getPrivateCredentials()) {
                String hashedCreds = pcred.toString();
                String[] split = hashedCreds.split(":");
                byte[] salt = Base64.getDecoder().decode(split[0]);
                if (hashedWithSalt(password, salt).equals(hashedCreds)) {
                    return subject;
                }
            }
        }
        return null;
    }

    public Subject find(String jwt) {
        try {
            Jws<Claims> claimsJws = JWT_MANAGER.parseToken(jwt);
            //        String username = claimsJws.getBody().getSubject();
            String key = claimsJws.getBody().get(JwtManager.CLAIM_USER_HASH).toString();
            //        String roles = claimsJws.getBody().get(JwtManager.CLAIM_ROLE).toString();
            //        for(String role : roles.split(",")) {
            //            subject.getPrincipals().add(new SampleRolePrincipal(role.trim()));
            //        })
            return users.get(key);
        } catch (ExpiredJwtException
                | UnsupportedJwtException
                | MalformedJwtException
                | SignatureException
                | IllegalArgumentException e) {
            return null;
        }
    }

    public String createJwt(Principal userPrincipal) {
        Subject s = users.get(userPrincipal.getName());
        String key = s.getPublicCredentials().iterator().next().toString();
        return JWT_MANAGER.createToken(userPrincipal.getName(), key,
                s.getPrincipals(SampleRolePrincipal.class).stream()
                        .map(SampleRolePrincipal::getName)
                        .collect(Collectors.joining(",")));
    }

    private static String hashed(String password) {
        byte[] saltb = new byte[16];
        random.nextBytes(saltb);
        return hashedWithSalt(password, saltb);
    }

    static String hashedWithSalt(String password, byte[] saltb) {
        int iterationCount = 65536;
        KeySpec spec = new PBEKeySpec(password.toCharArray(), saltb, iterationCount, 256);
        String salt = Base64.getEncoder().encodeToString(saltb);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return salt + ":" + Base64.getEncoder().encodeToString(factory.generateSecret(spec).getEncoded());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            for (int i = 0; i < iterationCount; i++) {
                // huh!
            }
            return salt + ":" + (salt + password).hashCode();
        }
    }

    private static String createKey(String userid) {
        return userid;
    }
}
