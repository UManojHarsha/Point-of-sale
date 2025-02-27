package com.pos.increff.dto;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.MockedStatic;

import com.pos.increff.config.AbstractUnitTest;
import com.pos.commons.OrdersData;
import com.pos.commons.OrderItemsData;
import com.pos.commons.OrderStatus;
import com.pos.increff.model.form.CombinedOrdersForm;
import com.pos.increff.model.form.OrderItemsForm;
import com.pos.increff.api.ApiException;
import com.pos.increff.model.data.PaginatedData;
import com.pos.increff.flow.OrderFlow;
import com.pos.increff.api.OrdersApi;
import com.pos.increff.api.OrderItemsApi;
import com.pos.increff.pojo.OrdersPojo;
import com.pos.increff.pojo.OrderItemsPojo;
import com.pos.increff.util.ConversionUtil;

public class OrdersDtoTest extends AbstractUnitTest {

    @InjectMocks
    private OrdersDto dto;

    @Mock
    private OrderFlow orderFlow;

    @Mock
    private OrdersApi ordersApi;

    @Mock
    private OrderItemsApi orderItemsApi;

    @Mock
    private ConversionUtil conversionUtil;

    private MockedStatic<ConversionUtil> mockedStatic;
    
    @Test
    public void testAdd_Success() throws ApiException {
        // Setup
        CombinedOrdersForm form = createValidOrderForm();
        OrdersPojo orderPojo = createOrderPojo();
        List<OrderItemsPojo> orderItems = Arrays.asList(createOrderItemPojo());

        // Mock behaviors
        when(conversionUtil.convert(form)).thenReturn(orderPojo);
        when(ordersApi.add(any(OrdersPojo.class))).thenReturn(1);
        when(conversionUtil.convertItems(any(), anyInt())).thenReturn(orderItems);
        doNothing().when(orderFlow).add(anyInt(), anyList());

        // Execute
        dto.add(form);

        // Verify
        verify(ordersApi).add(any(OrdersPojo.class));
        verify(orderFlow).add(anyInt(), anyList());
    }

    @Test(expected = NullPointerException.class)
    public void testAdd_NullForm() throws ApiException {
        dto.add(null);
    }

    //@Test
    // public void testGetAll_Success() throws ApiException {
    //     // Setup
    //     List<OrdersPojo> pojoList = Arrays.asList(createOrderPojo());

        
    //     // Mock behaviors
    //     when(ordersApi.getAll(anyInt(), anyInt())).thenReturn(pojoList);

    //     // Execute
    //     PaginatedData<OrdersData> result = dto.getAll(1, 10);
        
    //     // Verify
    //     assertNotNull("Result should not be null", result);
    //     assertNotNull("Result data should not be null", result.getData());
    //     assertEquals("Should have one order", 1, result.getData().size());
    //     assertEquals("User email should match", "test@example.com", result.getData().get(0).getUserEmail());
    //     assertEquals("Order status should match", OrderStatus.PENDING_INVOICE, result.getData().get(0).getStatus());
    //     assertEquals("Total price should match", 100.0, result.getData().get(0).getTotalPrice(), 0.001);
    //     verify(ordersApi).getAll(1, 10);
    // }

    // @Test
    // public void testGet_Success() throws ApiException {
    //     // Setup
    //     OrdersPojo pojo = createOrderPojo();
        
    //     // Mock behaviors
    //     when(ordersApi.get(anyInt())).thenReturn(pojo);

    //     // Execute
    //     OrdersData result = dto.get(1);
        
    //     // Verify
    //     assertNotNull(result);
    //     assertEquals(1, result.getId().intValue());
    //     assertEquals("test@example.com", result.getUserEmail());
    //     verify(ordersApi).get(1);
    // }

    @Test
    public void testGetAllItems_Success() throws ApiException {
        // Setup
        List<OrderItemsPojo> pojoList = Arrays.asList(createOrderItemPojo());
        OrderItemsData itemData = new OrderItemsData();
        itemData.setId(1);
        itemData.setOrderId(1);
        
        // Mock behaviors
        when(orderItemsApi.getByOrderId(anyInt())).thenReturn(pojoList);
        when(conversionUtil.convert(any(OrderItemsPojo.class))).thenReturn(itemData);

        // Execute
        List<OrderItemsData> result = dto.getAllItems(1);
        
        // Verify
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderItemsApi).getByOrderId(1);
    }

    // @Test
    // public void testUpdate_Success() throws ApiException {
    //     // Setup
    //     OrdersPojo existingPojo = createOrderPojo();
    //     OrdersData updateData = new OrdersData();
    //     updateData.setStatus(OrderStatus.INVOICE_GENERATED);
    //     updateData.setInvoicePath("/path/to/invoice.pdf");

    //     // Mock behaviors
    //     when(ordersApi.get(anyInt())).thenReturn(existingPojo);
    //     doNothing().when(ordersApi).update(any(OrdersPojo.class));

    //     // Execute
    //     dto.update(1, updateData);

    //     // Verify
    //     verify(ordersApi).get(1);
    //     verify(ordersApi).update(any(OrdersPojo.class));
    // }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();
        mockedStatic = mockStatic(ConversionUtil.class);
        
        // Create default test data
        OrdersData defaultOrderData = new OrdersData();
        defaultOrderData.setId(1);
        defaultOrderData.setStatus(OrderStatus.PENDING_INVOICE);
        defaultOrderData.setUserEmail("test@example.com");
        defaultOrderData.setTotalPrice(100.0);

        OrdersPojo defaultOrderPojo = createOrderPojo();
        OrderItemsPojo defaultOrderItemPojo = createOrderItemPojo();
        OrderItemsData defaultOrderItemData = createOrderItemData();
        List<OrderItemsPojo> defaultOrderItems = Arrays.asList(defaultOrderItemPojo);
        
        // Setup all necessary mock behaviors
        mockedStatic.when(() -> ConversionUtil.convert(any(OrdersPojo.class)))
                   .thenReturn(defaultOrderData);
        when(conversionUtil.convert(any(CombinedOrdersForm.class)))
                   .thenReturn(defaultOrderPojo);
        when(conversionUtil.convert(any(OrderItemsPojo.class)))
                   .thenReturn(defaultOrderItemData);
        when(conversionUtil.convertItems(any(), anyInt()))
                   .thenReturn(defaultOrderItems);
        
        // Setup default API behaviors
        when(ordersApi.get(anyInt())).thenReturn(defaultOrderPojo);
        when(ordersApi.getAll(anyInt(), anyInt())).thenReturn(Arrays.asList(defaultOrderPojo));
        when(orderItemsApi.getByOrderId(anyInt())).thenReturn(defaultOrderItems);
        when(ordersApi.add(any(OrdersPojo.class))).thenReturn(1);
        doNothing().when(orderFlow).add(anyInt(), anyList());
        doNothing().when(ordersApi).update(any(OrdersPojo.class));
    }

    @After
    public void tearDown() {
        if (mockedStatic != null) {
            mockedStatic.close();
        }
    }

    // Helper methods
    private CombinedOrdersForm createValidOrderForm() {
        CombinedOrdersForm form = new CombinedOrdersForm();
        form.setUserEmail("test@example.com");
        form.setTotalPrice(100.0);
        
        List<OrderItemsForm> items = new ArrayList<>();
        OrderItemsForm item = new OrderItemsForm();
        item.setProductId(1);
        item.setQuantity(2);
        item.setPrice(50.0);
        items.add(item);
        
        form.setOrderItems(items);
        return form;
    }

    private OrdersPojo createOrderPojo() {
        OrdersPojo pojo = new OrdersPojo();
        pojo.setId(1);
        pojo.setUserEmail("test@example.com");
        pojo.setTotalPrice(100.0);
        pojo.setStatus(OrderStatus.PENDING_INVOICE);
        return pojo;
    }

    private OrderItemsPojo createOrderItemPojo() {
        OrderItemsPojo pojo = new OrderItemsPojo();
        pojo.setId(1);
        pojo.setOrderId(1);
        pojo.setProductId(1);
        pojo.setQuantity(2);
        pojo.setTotalPrice(100.0);
        return pojo;
    }

    private OrderItemsData createOrderItemData() {
        OrderItemsData itemData = new OrderItemsData();
        itemData.setId(1);
        itemData.setOrderId(1);
        itemData.setQuantity(2);
        itemData.setPrice(50.0);
        itemData.setProductName("Test Product");
        return itemData;
    }

}
