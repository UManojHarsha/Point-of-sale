package com.pos.increff.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.After;
import org.springframework.beans.factory.annotation.Autowired;

import com.pos.increff.config.AbstractUnitTest;
import com.pos.increff.pojo.UserPojo;
import java.util.List;

public class UserApiTest extends AbstractUnitTest {

    @Autowired
    private UserApi api;


    private UserPojo createUser(String email, String password, String role) {
        UserPojo user = new UserPojo();
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        return user;
    }

    @After
    public void setUp() {
        api.deleteAll();
    }

    @Test
    public void testAdd() throws ApiException {
        UserPojo user = createUser("test@example.com", "password123", "OPERATOR");
        api.add(user);
        
        UserPojo retrieved = api.getUserByEmail("test@example.com");
        assertNotNull(retrieved);
        assertEquals("test@example.com", retrieved.getEmail());
        assertEquals("OPERATOR", retrieved.getRole());
    }

    @Test(expected = ApiException.class)
    public void testAddDuplicateEmail() throws ApiException {
        UserPojo user1 = createUser("test@example.com", "password123", "OPERATOR");
        api.add(user1);

        UserPojo user2 = createUser("test@example.com", "password456", "SUPERVISOR");
        api.add(user2); // Should throw ApiException
    }

    @Test
    public void testGetUserByEmail() throws ApiException {
        UserPojo user = createUser("test@example.com", "password123", "OPERATOR");
        api.add(user);

        UserPojo retrieved = api.getUserByEmail("test@example.com");
        assertNotNull(retrieved);
        assertEquals("test@example.com", retrieved.getEmail());
        assertEquals("OPERATOR", retrieved.getRole());
    }

    @Test(expected = ApiException.class)
    public void testGetUserByNonExistentEmail() throws ApiException {
        api.getUserByEmail("nonexistent@example.com"); // Should throw ApiException
    }

    @Test
    public void testGetAll() throws ApiException {
        // Add multiple users
        UserPojo user1 = createUser("user1@example.com", "password123", "OPERATOR");
        UserPojo user2 = createUser("user2@example.com", "password456", "SUPERVISOR");
        
        api.add(user1);
        api.add(user2);

        List<UserPojo> users = api.getAll();
        assertEquals(2, users.size());
    }

    @Test
    public void testDelete() throws ApiException {
        // First add a user
        UserPojo user = createUser("test@example.com", "password123", "OPERATOR");
        api.add(user);
        
        // Get the user's ID and delete
        UserPojo added = api.getUserByEmail("test@example.com");
        api.delete(added.getId());
        
        // Verify deletion by trying to get the user (should throw exception)
        try {
            api.getUserByEmail("test@example.com");
        } catch (ApiException e) {
            assertEquals("User with given email does not exist", e.getMessage());
        }
    }

    @Test
    public void testNormalize() throws ApiException {
        UserPojo user = createUser("test@example.com", "password123", "operator");
        api.add(user); // This will normalize the role

        UserPojo retrieved = api.getUserByEmail("test@example.com");
        assertEquals("OPERATOR", retrieved.getRole()); // Role should be uppercase
    }
}
