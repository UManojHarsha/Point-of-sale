package com.pos.increff.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.After;
import org.springframework.beans.factory.annotation.Autowired;

import com.pos.increff.config.AbstractUnitTest;
import com.pos.increff.pojo.OrdersPojo;
import com.pos.commons.OrderStatus;
import java.util.List;

public class OrdersApiTest extends AbstractUnitTest {

    @Autowired
    private OrdersApi api;

    @After
    public void setUp() {
        api.deleteAll();
    }

    @Test
    public void testAdd() throws ApiException {
        OrdersPojo order = new OrdersPojo();
        order.setUserEmail("test@example.com");
        order.setTotalPrice(1000.0);
        order.setStatus(OrderStatus.PENDING_INVOICE);
        order.setInvoicePath(null);
        api.add(order);
        
        OrdersPojo retrieved = api.get(order.getId());
        assertNotNull(retrieved);
        assertEquals("test@example.com", retrieved.getUserEmail());
        assertEquals(1000.0, retrieved.getTotalPrice(), 0.01);
        assertEquals(OrderStatus.PENDING_INVOICE, retrieved.getStatus());
    }


    @Test
    public void testGetAll() throws ApiException {
        // Add multiple orders
        OrdersPojo order1 = new OrdersPojo();
        order1.setUserEmail("user1@example.com");
        order1.setTotalPrice(1000.0);
        order1.setStatus(OrderStatus.PENDING_INVOICE);
        order1.setInvoicePath(null);
        api.add(order1);

        OrdersPojo order2 = new OrdersPojo();
        order2.setUserEmail("user2@example.com");
        order2.setTotalPrice(2000.0);
        order2.setStatus(OrderStatus.PENDING_INVOICE);
        order2.setInvoicePath(null);
        api.add(order2);

        List<OrdersPojo> orders = api.getAll(1, 10);
        assertEquals(2, orders.size());
        assertEquals(OrderStatus.PENDING_INVOICE, orders.get(0).getStatus());
        assertEquals(OrderStatus.PENDING_INVOICE, orders.get(1).getStatus());
    }

    @Test(expected = ApiException.class)
    public void testGetNonExistent() throws ApiException {
        api.get(9999); // Should throw ApiException
    }

}
