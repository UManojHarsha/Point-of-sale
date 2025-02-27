package com.pos.increff.flow;

import com.pos.increff.api.ApiException;
import com.pos.increff.pojo.OrderItemsPojo;
import com.pos.increff.pojo.ProductPojo;
import com.pos.increff.api.InventoryApi;
import com.pos.increff.api.OrderItemsApi;
import com.pos.increff.api.OrdersApi;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrdersFlowTest {

    @InjectMocks
    private OrderFlow orderFlow;

    @Mock
    private InventoryApi inventoryApi;

    @Mock
    private OrderItemsApi orderItemsApi;

    @Mock
    private OrdersApi ordersApi;

    @Test
    public void testAdd_SuccessfulOrder() throws ApiException {
        // Prepare test data
        int orderId = 1;
        List<OrderItemsPojo> orderItems = createOrderItems();

        // Mock inventory checks
        when(inventoryApi.getStock(1)).thenReturn(5); // Sufficient stock for item1
        when(inventoryApi.getStock(2)).thenReturn(5); // Sufficient stock for item2
        when(inventoryApi.getProduct(1)).thenReturn(createMockProduct("Product 1"));
        when(inventoryApi.getProduct(2)).thenReturn(createMockProduct("Product 2"));

        // Mock inventory updates
        doNothing().when(inventoryApi).updateStock(anyInt(), anyInt());
        doNothing().when(orderItemsApi).add(anyList());

        // Execute test
        orderFlow.add(orderId, orderItems);

        // Verify interactions
        verify(inventoryApi, times(2)).getStock(anyInt());
        verify(inventoryApi, times(2)).getProduct(anyInt());
        verify(inventoryApi, times(2)).updateStock(anyInt(), anyInt());
        verify(orderItemsApi).add(orderItems);
    }

    @Test(expected = ApiException.class)
    public void testAdd_InsufficientStock() throws ApiException {
        // Prepare test data
        int orderId = 1;
        List<OrderItemsPojo> orderItems = createOrderItems();

        // Mock insufficient stock for second item
        when(inventoryApi.getStock(1)).thenReturn(5); // Sufficient stock
        when(inventoryApi.getStock(2)).thenReturn(1); // Insufficient stock
        when(inventoryApi.getProduct(1)).thenReturn(createMockProduct("Product 1"));
        when(inventoryApi.getProduct(2)).thenReturn(createMockProduct("Product 2"));

        // Execute test - should throw ApiException
        orderFlow.add(orderId, orderItems);
    }

    @Test(expected = NullPointerException.class)
    public void testAdd_NullOrderItems() throws ApiException {
        // Prepare test data
        int orderId = 1;

        // Execute test - should throw ApiException
        orderFlow.add(orderId, null);
    }

    @Test(expected = NullPointerException.class)
    public void testAdd_InvalidOrderId() throws ApiException {
        // Prepare test data
        int orderId = -1;
        List<OrderItemsPojo> orderItems = createOrderItems();

        // Execute test - should throw ApiException
        orderFlow.add(orderId, orderItems);
    }

    @Test(expected = ApiException.class)
    public void testAdd_InventoryUpdateFailure() throws ApiException {
        // Prepare test data
        int orderId = 1;
        List<OrderItemsPojo> orderItems = createOrderItems();

        // Mock successful inventory checks
        when(inventoryApi.getStock(1)).thenReturn(5);
        when(inventoryApi.getStock(2)).thenReturn(5);
        when(inventoryApi.getProduct(1)).thenReturn(createMockProduct("Product 1"));
        when(inventoryApi.getProduct(2)).thenReturn(createMockProduct("Product 2"));

        // Mock inventory update failure
        doThrow(new ApiException("Failed to update inventory"))
            .when(inventoryApi).updateStock(eq(1), anyInt());

        // Execute test - should throw ApiException
        orderFlow.add(orderId, orderItems);
    }

    @Test(expected = ApiException.class)
    public void testAdd_OrderItemsAdditionFailure() throws ApiException {
        // Prepare test data
        int orderId = 1;
        List<OrderItemsPojo> orderItems = createOrderItems();

        // Mock successful inventory checks
        when(inventoryApi.getStock(1)).thenReturn(5);
        when(inventoryApi.getStock(2)).thenReturn(5);
        when(inventoryApi.getProduct(1)).thenReturn(createMockProduct("Product 1"));
        when(inventoryApi.getProduct(2)).thenReturn(createMockProduct("Product 2"));

        // Mock successful inventory updates
        doNothing().when(inventoryApi).updateStock(anyInt(), anyInt());

        // Mock order items addition failure
        doThrow(new ApiException("Failed to add order items"))
            .when(orderItemsApi).add(anyList());

        // Execute test - should throw ApiException
        orderFlow.add(orderId, orderItems);
    }

    private List<OrderItemsPojo> createOrderItems() {
        OrderItemsPojo item1 = new OrderItemsPojo();
        item1.setProductId(1);
        item1.setQuantity(2);
        item1.setTotalPrice(200.0);

        OrderItemsPojo item2 = new OrderItemsPojo();
        item2.setProductId(2);
        item2.setQuantity(3);
        item2.setTotalPrice(300.0);

        return Arrays.asList(item1, item2);
    }

    private ProductPojo createMockProduct(String name) {
        ProductPojo product = new ProductPojo();
        product.setName(name);
        return product;
    }
}
