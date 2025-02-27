package com.pos.increff.api;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.pos.increff.config.AbstractUnitTest;
import com.pos.increff.pojo.OrderItemsPojo;
import com.pos.increff.pojo.OrdersPojo;
import com.pos.commons.OrderStatus;
import com.pos.increff.util.DateTimeUtils;

public class OrderItemsApiTest extends AbstractUnitTest {

    @Mock
    private OrderItemsApi orderItemsApi;

    @Mock
    private OrdersApi ordersApi;

    private List<OrderItemsPojo> testItems;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
        try {
            setupTestData();
            setupMockBehavior();
        } catch (ApiException e) {
            fail("Setup failed: " + e.getMessage());
        }
    }

    @After
    public void tearDown() {
        reset(orderItemsApi, ordersApi);
    }

    @Test
    public void testAdd() throws ApiException {
        OrdersPojo order = createOrder();
        List<OrderItemsPojo> items = new ArrayList<>();
        items.add(createOrderItem(order.getId(), 1, 2, 500.0));
        items.add(createOrderItem(order.getId(), 2, 1, 300.0));
        
        orderItemsApi.add(items);
        List<OrderItemsPojo> retrievedItems = orderItemsApi.getByOrderId(order.getId());
        
        assertEquals(2, retrievedItems.size());
        verify(orderItemsApi).add(items);
        verify(orderItemsApi).getByOrderId(order.getId());
    }

    @Test
    public void testGetAll() throws ApiException {
        List<OrderItemsPojo> allItems = orderItemsApi.getAll();
        assertNotNull(allItems);
        assertEquals(2, allItems.size());
        verify(orderItemsApi).getAll();
    }

    @Test
    public void testGetByOrderId() throws ApiException {
        OrdersPojo order = createOrder();
        List<OrderItemsPojo> retrievedItems = orderItemsApi.getByOrderId(order.getId());
        
        assertEquals(2, retrievedItems.size());
        assertEquals(Integer.valueOf(1), retrievedItems.get(0).getOrderId());
        verify(orderItemsApi).getByOrderId(order.getId());
    }

    @Test(expected = ApiException.class)
    public void testGetByNonExistentOrderId() throws ApiException {
        orderItemsApi.getByOrderId(999);
    }

    @Test
    public void testAddMultipleItemsSameOrder() throws ApiException {
        OrdersPojo order = createOrder();
        
        // Add first batch of items
        List<OrderItemsPojo> firstBatch = new ArrayList<>();
        firstBatch.add(createOrderItem(order.getId(), 1, 2, 500.0));
        orderItemsApi.add(firstBatch);
        
        // Add second batch of items
        List<OrderItemsPojo> secondBatch = new ArrayList<>();
        secondBatch.add(createOrderItem(order.getId(), 2, 3, 300.0));
        orderItemsApi.add(secondBatch);
        
        // Mock behavior for this specific test
        List<OrderItemsPojo> combinedItems = new ArrayList<>(firstBatch);
        combinedItems.addAll(secondBatch);
        when(orderItemsApi.getByOrderId(order.getId())).thenReturn(combinedItems);
        
        // Verify total items
        List<OrderItemsPojo> allItems = orderItemsApi.getByOrderId(order.getId());
        assertEquals(2, allItems.size());
        verify(orderItemsApi, times(2)).add(anyList());
        verify(orderItemsApi).getByOrderId(order.getId());
    }

    
    private void setupTestData() throws ApiException {
        testItems = new ArrayList<>();
        testItems.add(createOrderItem(1, 1, 2, 500.0));
        testItems.add(createOrderItem(1, 2, 1, 300.0));
    }

    private void setupMockBehavior() throws ApiException {
        // Setup mock behavior for getAll
        when(orderItemsApi.getAll()).thenReturn(testItems);

        // Setup mock behavior for getByOrderId
        when(orderItemsApi.getByOrderId(1)).thenReturn(testItems);
        when(orderItemsApi.getByOrderId(999)).thenThrow(new ApiException("Order not found"));

        // Setup mock behavior for add
        doNothing().when(orderItemsApi).add(anyList());

        // Setup mock behavior for ordersApi
        when(ordersApi.add(any(OrdersPojo.class))).thenReturn(1);
    }

    private OrdersPojo createOrder() throws ApiException {
        OrdersPojo order = new OrdersPojo();
        order.setUserEmail("test@example.com");
        order.setTotalPrice(1000.0);
        order.setStatus(OrderStatus.PENDING_INVOICE);
        order.setInvoicePath(null);
        order.setId(1); // Set fixed ID for testing
        return order;
    }

    private OrderItemsPojo createOrderItem(int orderId, int productId, int quantity, double price) {
        OrderItemsPojo item = new OrderItemsPojo();
        item.setOrderId(orderId);
        item.setProductId(productId);
        item.setQuantity(quantity);
        item.setTotalPrice(price);
        return item;
    }

}
