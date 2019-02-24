package ch.seidel.tomcattest;

import java.security.Principal;

public class SampleRolePrincipal implements Principal {
    private final String principal;

    public SampleRolePrincipal(String principal) {
        this.principal = principal;
    }

    @Override
    public String getName() {
        return principal;
    }
}
