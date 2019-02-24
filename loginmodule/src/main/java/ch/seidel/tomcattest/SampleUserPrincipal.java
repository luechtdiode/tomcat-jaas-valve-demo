package ch.seidel.tomcattest;

import java.security.Principal;

public class SampleUserPrincipal implements Principal {
    private final String username;

    public SampleUserPrincipal(String username) {
        this.username = username;
    }

    @Override
    public String getName() {
        return username;
    }
}
