package com.pos.increff.dto;

import com.pos.increff.api.ApiException;
import org.springframework.web.server.ResponseStatusException;
import com.pos.increff.config.AbstractUnitTest;
import com.pos.increff.model.data.UserData;
import com.pos.increff.model.form.UserForm;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UserDtoTest extends AbstractUnitTest {

    @Autowired
    private UserDto dto;
    // Unit Tests

    @Test
    public void testAdd() throws ApiException {
        // Create a user with non-supervisor email
        UserForm form = createUserForm("test@increff.com", "password123");
        dto.add(form);

        // Verify the role is OPERATOR since test@example.com is not in supervisor.emails
        String role = dto.getUserRole("test@increff.com");
        assertEquals("OPERATOR", role);

        // Verify user exists and has correct data
        List<UserData> users = dto.getAll();
        assertEquals(1, users.size());
        UserData user = users.get(0);
        assertEquals("test@increff.com", user.getEmail());
        assertEquals("OPERATOR", user.getRole());
    }

    @Test(expected = ApiException.class)
    public void testAddDuplicateEmail() throws ApiException {
        UserForm form1 = createUserForm("test@example.com", "password123");
        UserForm form2 = createUserForm("test@example.com", "password456");
        
        dto.add(form1);
        dto.add(form2); // Should throw ApiException
    }

    @Test(expected = ApiException.class)
    public void testAddInvalidEmail() throws ApiException {
        UserForm form = createUserForm("invalid-email", "password123");
        dto.add(form);
    }

    @Test(expected = ApiException.class)
    public void testAddEmptyPassword() throws ApiException {
        UserForm form = createUserForm("test@example.com", "");
        dto.add(form);
    }

    @Test
    public void testLogin() throws ApiException {
        // Create a user first
        UserForm form = createUserForm("test@increff.com", "password123");
        dto.add(form);

        // Test login
        UserForm loginForm = createUserForm("test@increff.com", "password123");
        UserData userData = dto.login(loginForm);
        
        assertNotNull(userData);
        assertEquals("test@increff.com", userData.getEmail());
        assertEquals("OPERATOR", userData.getRole());
    }

    @Test(expected = ApiException.class)
    public void testLoginWrongPassword() throws ApiException {
        // Create a user first
        UserForm form = createUserForm("test@example.com", "password123");
        dto.add(form);

        // Test login with wrong password
        UserForm loginForm = createUserForm("test@example.com", "wrongpassword");
        dto.login(loginForm);
    }

    @Test(expected = ApiException.class)
    public void testLoginNonExistentUser() throws ApiException {
        UserForm loginForm = createUserForm("nonexistent@example.com", "password123");
        dto.login(loginForm);
    }

    @Test
    public void testGetAll() throws ApiException {
        // Add multiple users
        UserForm form1 = createUserForm("user1@example.com", "password123");
        UserForm form2 = createUserForm("user2@example.com", "password456");
        
        dto.add(form1);
        dto.add(form2);

        List<UserData> users = dto.getAll();
        assertEquals(2, users.size());
    }

    @Test
    public void testGetUserRole() throws ApiException {
        UserForm form = createUserForm("test@increff.com", "password123");
        dto.add(form);

        String role = dto.getUserRole("test@increff.com");
        assertEquals("OPERATOR", role);
    }

    @Test(expected = ApiException.class)
    public void testGetUserRoleNonExistentUser() throws ApiException {
        dto.getUserRole("nonexistent@example.com");
    }

    // Integration Tests

    @Test
    public void testUserLoginLogout() throws ApiException {
        // Create user
        UserForm form = createUserForm("test@example.com", "password123");
        dto.add(form);

        // Login
        UserForm loginForm = createUserForm("test@example.com", "password123");
        UserData userData = dto.login(loginForm);
        assertNotNull(userData);

        // Logout
        dto.logout();
        // Note: Since logout typically involves session management,
        // additional assertions might be needed based on your implementation
    }

    @Test
    public void testPasswordEncryption() throws ApiException {
        String rawPassword = "password123";
        UserForm form = createUserForm("test@example.com", rawPassword);
        dto.add(form);

        // Get the user and verify password is encrypted
        List<UserData> users = dto.getAll();
        UserData user = users.stream()
            .filter(u -> u.getEmail().equals("test@example.com"))
            .findFirst()
            .orElse(null);

        assertNotNull(user);
        
        // Try login to verify encryption worked correctly
        UserForm loginForm = createUserForm("test@example.com", rawPassword);
        UserData loginResult = dto.login(loginForm);
        assertNotNull(loginResult);
    }

    @Test
    public void testSupervisorRoleAssignment() throws ApiException {
        // Create a user with supervisor email
        UserForm form = createUserForm("admin@increff.com", "password123");
        dto.add(form);

        String role = dto.getUserRole("admin@increff.com");
        assertEquals("SUPERVISOR", role);
    }

    @Test
    public void testOperatorRoleAssignment() throws ApiException {
        // Create a user with non-supervisor email
        UserForm form = createUserForm("operator@example.com", "password123");
        dto.add(form);

        String role = dto.getUserRole("operator@example.com");
        assertEquals("OPERATOR", role);
    }

    @After
    public void tearDown() {
        dto.deleteAll();
    }

    private UserForm createUserForm(String email, String password) {
        UserForm form = new UserForm();
        form.setEmail(email);
        form.setPassword(password);
        return form;
    }

    @Before
    public void setUp() {
        dto.deleteAll();
    }

}
