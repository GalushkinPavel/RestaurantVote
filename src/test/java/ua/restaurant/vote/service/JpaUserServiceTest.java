package ua.restaurant.vote.service;

import org.junit.Test;
import ua.restaurant.vote.model.Role;
import ua.restaurant.vote.model.User;

import javax.validation.ConstraintViolationException;

/**
 * Created by Galushkin Pavel on 06.03.2017.
 */
public class JpaUserServiceTest extends AbstractUserServiceTest {

    @Test
    public void testValidation() throws Exception {
        // empty name
        validateRootCause(() -> service.save(new User(null, "  ", "invalid@yandex.ru", "password", Role.ROLE_USER)), ConstraintViolationException.class);
        // empty email
        validateRootCause(() -> service.save(new User(null, "User", "  ", "password", Role.ROLE_USER)), ConstraintViolationException.class);
        // empty password
        validateRootCause(() -> service.save(new User(null, "User", "invalid@yandex.ru", "  ", Role.ROLE_USER)), ConstraintViolationException.class);
    }
}
