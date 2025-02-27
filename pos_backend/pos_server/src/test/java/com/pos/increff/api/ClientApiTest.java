package com.pos.increff.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.After;
import org.springframework.beans.factory.annotation.Autowired;

import com.pos.increff.config.AbstractUnitTest;
import com.pos.increff.pojo.ClientPojo;
import java.util.List;
public class ClientApiTest extends AbstractUnitTest {

    @Autowired
    private ClientApi api;

    @Test
    public void testAdd() throws ApiException {
        ClientPojo client = new ClientPojo();
        client.setName("test client");
        client.setEmail("test@example.com");
        client.setContactNo("1234567895");
        api.add(client);
        
        ClientPojo retrieved = api.getByName("test client");
        assertNotNull(retrieved);
        assertEquals("test client", retrieved.getName());
    }

    @Test(expected = ApiException.class)
    public void testAddDuplicate() throws ApiException {
        ClientPojo client1 = new ClientPojo();
        client1.setName("test client");
        client1.setEmail("test@example.com");
        client1.setContactNo("1234567890");
        api.add(client1);

        ClientPojo client2 = new ClientPojo();
        client2.setName("test client");
        client2.setEmail("test@example.com");
        client2.setContactNo("1234567890");
        api.add(client2); // Should throw ApiException
    }

    @Test
    public void testUpdate() throws ApiException {
        // First add a client
        ClientPojo client = new ClientPojo();
        client.setName("test client");
        client.setEmail("test@example.com");
        client.setContactNo("1234567890");
        api.add(client);

        // Get the client and update
        int prevId = api.getByName("test client").getId();

        ClientPojo toUpdate = api.getByName("test client");
        toUpdate.setEmail("updated@example.com");
        toUpdate.setContactNo("1234567891");
        api.update(toUpdate.getId(), toUpdate);

        // Verify update
        ClientPojo updated = api.get(toUpdate.getId());
        assertEquals(prevId, updated.getId().intValue());
        assertEquals("test client", updated.getName());
        assertEquals("updated@example.com", updated.getEmail());
        assertEquals("1234567891", updated.getContactNo());
    }

    @Test(expected = ApiException.class)
    public void testGetNonExistent() throws ApiException {
        api.get(9999); // Should throw ApiException
    }

    @Test
    public void testGetAll() throws ApiException {
        // Add two clients
        List<ClientPojo> clients = api.getAll();
        System.out.println(clients);
        ClientPojo client1 = new ClientPojo();
        client1.setName("clientTest1");
        client1.setEmail("clientTest1@example.com");
        client1.setContactNo("1234567888");
        api.add(client1);

        ClientPojo client2 = new ClientPojo();
        client2.setName("clientTest2");
        client2.setEmail("clientTest2@example.com");
        client2.setContactNo("1234567999");
        api.add(client2);

        assertEquals(2, api.getAll().size());
    }

    @After
    public void setUp() {
        api.deleteAll();
    }
} 