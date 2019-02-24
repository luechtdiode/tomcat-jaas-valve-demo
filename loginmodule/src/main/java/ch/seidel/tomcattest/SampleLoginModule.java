package ch.seidel.tomcattest;

import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class SampleLoginModule implements LoginModule {
    private CallbackHandler handler = null;
    private Subject matchingUserSubject = null;
    private Subject subject = null;
    private Users users = new Users();

    public void initialize(Subject subject, CallbackHandler handler, Map sharedState, Map options) {
        System.out.println("SampleLoginModule: initialize");
        this.subject = subject;
        this.handler = handler;
        System.out.println("SampleLoginModule - initialize - subject: " + subject);
        System.out.println("SampleLoginModule - initialize - sharedState: " + sharedState);
    }

    public boolean login() throws LoginException {
        System.out.println("SampleLoginModule: login");
        if (handler == null) throw new LoginException("No CallbackHandler");
        Callback[] callbacks = new Callback[2];
        callbacks[0] = new NameCallback("Username: ");
        callbacks[1] = new PasswordCallback("Password: ", false);
        String username;
        String password;
        try {
            handler.handle(callbacks);
            username = ((NameCallback) callbacks[0]).getName();
            password = new String(((PasswordCallback) callbacks[1]).getPassword());
        } catch (IOException | UnsupportedCallbackException e) {
            throw new LoginException(e.toString());
        }

        matchingUserSubject = authenticate(username, password);

        return matchingUserSubject != null;
    }

    public boolean commit() {
        System.out.println("SampleLoginModule: commit");
        if (matchingUserSubject == null) {
            System.out.println("SampleLoginModule: no matchingUserSubject created");
            return false;
        }
        Set<SampleUserPrincipal> userPrincipals = matchingUserSubject.getPrincipals(SampleUserPrincipal.class);
        if (userPrincipals.isEmpty()) {
            System.out.println("SampleLoginModule: no matchingUserSubject created");
            return false;
        }

        subject.getPrincipals().addAll(userPrincipals);
        subject.getPrincipals().addAll(matchingUserSubject.getPrincipals(SampleRolePrincipal.class));
        System.out.println("SampleLoginModule: commit successful");
        return true;
    }

    public boolean logout() {
        System.out.println("SampleLoginModule: logout");
        matchingUserSubject.getPrincipals().forEach(p -> subject.getPrincipals().remove(p));
        return true;
    }

    public boolean abort() {
        System.out.println("SampleLoginModule: abort");
        return true;
    }

    private Subject authenticate(String user, String password) {
        if (user == null || password == null) {
            return null;
        }
        if (!password.isEmpty() && !user.isEmpty()) {
            return users.find(user, password);
        } else if (!user.isEmpty()) {
            // assume it is a jwt. It will return null, if jwt is invalid
            return users.find(user);
        } else {
            return null;
        }
    }
}
