package ch.seidel.tomcattest;

import org.junit.Before;
import org.junit.Test;

import javax.security.auth.Subject;

import java.util.Base64;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class UsersTest {

    @Before
    public void setup() {
        Users.clear();
    }

    @Test
    public void testPasswordHashing() {
        Users users = new Users();
        byte[] saltb = {1, 2, 3};
        String hashedpw1 = Users.hashedWithSalt("123", saltb);
        assertThat(hashedpw1.split(":")[0], is(Base64.getEncoder().encodeToString((saltb))));

        String hashedpw2 = Users.hashedWithSalt("123", Base64.getDecoder().decode(hashedpw1.split(":")[0]));
        assertThat(hashedpw1, is(hashedpw2));
    }

    @Test
    public void testAddUser() {
        Users users = new Users();
        Users.addUser("Roland", "asdfqer.939234asdfe", "admin", "user");
        Subject roland = users.find("Roland", "asdfqer.939234asdfe");
        assertThat(roland.getPrincipals(SampleUserPrincipal.class).size(), is(1));
        assertThat(roland.getPrincipals(SampleUserPrincipal.class).iterator().next().getName(), is("Roland"));
        assertThat(roland.getPrincipals(SampleRolePrincipal.class).size(), is(2));
    }

    @Test
    public void testFindNonexistingUserPW() {
        Users users = new Users();
        Users.addUser("Roland", "asdfqer.939234asdfe", "admin", "user");
        Subject roland = users.find("Roland", "939234asdfe");
        assertNull(roland);
    }
    @Test
    public void testFindNonexistingUserName() {
        Users users = new Users();
        Users.addUser("Roland", "asdfqer.939234asdfe", "admin", "user");
        Subject roland = users.find("Hans", "asdfqer.939234asdfe");
        assertNull(roland);
    }
}