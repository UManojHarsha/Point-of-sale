package com.pos.increff.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pos.increff.config.AbstractIntegrationTest;
import com.pos.increff.model.data.ClientData;
import com.pos.increff.model.form.ClientForm;
import com.pos.increff.api.ApiException;
import com.pos.increff.api.ClientApi;
import com.pos.increff.model.data.PaginatedData;
import com.pos.increff.model.data.FieldErrorData;

public class ClientDtoTest extends AbstractIntegrationTest {

    @Autowired
    private ClientDto dto;

    @Autowired
    private ClientApi api;

    private ClientForm createClientForm(String name, String email, String contactNo) {
        ClientForm form = new ClientForm();
        form.setName(name);
        form.setEmail(email);
        form.setContactNo(contactNo);
        return form;
    }

    @Before
    public void setUp() {
        // Clean up before each test
        api.deleteAll();
    }

    @Test
    public void testAdd() throws ApiException {
        ClientForm form = createClientForm("test client", "test@example.com", "1234567890");
        dto.add(form);
        
        PaginatedData<ClientData> paginatedData = dto.getAll(1, 10);
        List<ClientData> clients = paginatedData.getData();
        assertEquals(1, clients.size());
        assertEquals("test client", clients.get(0).getName());
        assertEquals("test@example.com", clients.get(0).getEmail());
    }

    @Test(expected = ApiException.class)
    public void testAddDuplicateName() throws ApiException {
        ClientForm form1 = createClientForm("Test Client", "test1@example.com", "1234567890");
        ClientForm form2 = createClientForm("Test Client", "test2@example.com", "0987654321");
        
        dto.add(form1);
        dto.add(form2); // Should throw ApiException
    }

    @Test(expected = ApiException.class)
    public void testAddInvalidEmail() throws ApiException {
        ClientForm form = createClientForm("Test Client", "invalid-email", "1234567890");
        dto.add(form); // Should throw ApiException due to invalid email
    }

    @Test
    public void testAddInvalidPhone() throws ApiException {
        ClientForm form = createClientForm("Test Client", "test@example.com", "123"); // Invalid phone number
        try {
            dto.add(form); // Should throw ApiException due to invalid phone number
            fail("Expected ApiException was not thrown");
        } catch (ApiException e) {
            List<FieldErrorData> fieldErrors = e.getFieldErrors();
            assertNotNull("Field errors should not be null", fieldErrors);
            assertFalse("Field errors should not be empty", fieldErrors.isEmpty());
            boolean foundPhoneError = false;
            for (FieldErrorData error : fieldErrors) {
                if (error.getField().equals("contactNo") && 
                    error.getMessage().contains("Phone number must be 10 digits")) {
                    foundPhoneError = true;
                    break;
                }
            }
            assertTrue("Should find phone number validation error", foundPhoneError);
        }
    }

    @Test
    public void testGet() throws ApiException {
        // Add a test client
        ClientForm form = createClientForm("test client", "test@example.com", "1234567890");
        dto.add(form);
        
        // Get all clients and get the ID of the first one
        List<ClientData> clients = dto.getAll(1, 10).getData();
        int id = clients.get(0).getId();
        
        // Test get by ID
        ClientData client = dto.get(id);
        assertNotNull(client);
        assertEquals("test client", client.getName());
        assertEquals("test@example.com", client.getEmail());
    }

    @Test(expected = ApiException.class)
    public void testGetNonExistent() throws ApiException {
        dto.get(9999); // Should throw ApiException
    }

    @Test
    public void testGetAll() throws ApiException {
        // Add two test clients
        ClientForm form1 = createClientForm("test client 1", "test1@example.com", "1234567890");
        ClientForm form2 = createClientForm("test client 2", "test2@example.com", "0987654321");
        dto.add(form1);
        dto.add(form2);
        
        PaginatedData<ClientData> paginatedData = dto.getAll(1, 10);
        List<ClientData> clients = paginatedData.getData();
        assertEquals(2, clients.size());
    }

    @Test
    public void testUpdate() throws ApiException {
        // Add a test client
        ClientForm form = createClientForm("test client", "test@example.com", "1234567890");
        dto.add(form);
        
        // Get the client ID
        List<ClientData> clients = dto.getAll(1, 10).getData();
        int id = clients.get(0).getId();
        
        // Update the client
        ClientForm updateForm = createClientForm("updated client", "updated@example.com", "9876543210");
        dto.update(id, updateForm);
        
        // Verify the update
        ClientData updated = dto.get(id);
        assertEquals("updated client", updated.getName());
        assertEquals("updated@example.com", updated.getEmail());
        assertEquals("9876543210", updated.getContactNo());
    }

    @Test(expected = ApiException.class)
    public void testUpdateNonExistent() throws ApiException {
        System.out.println(api.getAll());
        ClientForm form = createClientForm("Test Client", "test@example.com", "1234567890");
        dto.update(9999, form); // Should throw ApiException
    }

    @Test(expected = ApiException.class)
    public void testUpdateDuplicateName() throws ApiException {
        // Add two test clients
        ClientForm form1 = createClientForm("test client 1", "test1@example.com", "1234567890");
        ClientForm form2 = createClientForm("test client 2", "test2@example.com", "0987654321");
        dto.add(form1);
        dto.add(form2);
        
        // Get the second client's ID
        List<ClientData> clients = dto.getAll(1, 10).getData();
        int id2 = clients.get(1).getId();
        
        // Try to update second client with first client's name
        ClientForm updateForm = createClientForm("test client 1", "test2@example.com", "0987654321");
        dto.update(id2, updateForm);
    }

    @Test
    public void testDelete() throws ApiException {
        // Add a test client
        ClientForm form = createClientForm("test client", "test@example.com", "1234567890");
        dto.add(form);
        
        // Get the client ID
        List<ClientData> clients = dto.getAll(1, 10).getData();
        int id = clients.get(0).getId();
        
        // Delete the client
        dto.delete(id);
        
        // Verify deletion
        PaginatedData<ClientData> paginatedData = dto.getAll(1, 10);
        assertEquals(0, paginatedData.getData().size());
    }

    @Test(expected = ApiException.class)
    public void testDeleteNonExistent() throws ApiException {
        dto.delete(9999); // Should throw ApiException
    }

    @Test
    public void testValidation() throws ApiException {
        // Test valid data
        ClientForm validForm = createClientForm("Valid Name", "valid@example.com", "1234567890");
        dto.add(validForm); // Should not throw exception
        
        // Verify the client was added
        List<ClientData> clients = dto.getAll(1, 10).getData();
        assertEquals(1, clients.size());
        
        // Verify all fields were saved correctly
        ClientData client = clients.get(0);
        assertEquals("valid name", client.getName());
        assertEquals("valid@example.com", client.getEmail());
        assertEquals("1234567890", client.getContactNo());
        assertNotNull(client.getCreatedDate());
        assertNotNull(client.getUpdatedDate());
    }

    @After
    public void tearDown() {
        api.deleteAll();
    }
}
