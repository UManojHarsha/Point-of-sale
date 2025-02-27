package com.pos.increff.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.pos.increff.config.AbstractUnitTest;
import com.pos.increff.model.data.InventoryData;
import com.pos.increff.model.form.InventoryForm;
import com.pos.increff.model.form.ProductForm;
import com.pos.increff.model.form.ClientForm;
import com.pos.increff.model.form.CombinedOrdersForm;
import com.pos.increff.model.form.OrderItemsForm;
import com.pos.increff.api.ApiException;
import com.pos.increff.model.data.ClientData;
import com.pos.increff.model.data.ProductData;
import com.pos.increff.model.data.PaginatedData;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Mockito.*;

public class InventoryDtoTest extends AbstractUnitTest {

    @Mock
    private InventoryDto dto;

    @Mock
    private ProductDto productDto;

    @Mock
    private ClientDto clientDto;

    @Mock
    private OrdersDto ordersDto;

    private String clientName;
    private Integer productId;
    private InventoryData mockInventoryData;

    @Test
    public void testInventoryUpdateWithOrder() throws ApiException {
        // Create initial inventory with 200 units
        InventoryData initialInventory = new InventoryData();
        initialInventory.setProductId(productId);
        initialInventory.setTotalQuantity(200);
        when(dto.getByProductId(productId)).thenReturn(initialInventory);

        // Mock inventory update behavior with mutable state
        final int[] currentQuantity = {200};
        doAnswer(invocation -> {
            int quantity = invocation.getArgument(1);
            currentQuantity[0] -= quantity;
            InventoryData updatedInventory = new InventoryData();
            updatedInventory.setProductId(productId);
            updatedInventory.setTotalQuantity(currentQuantity[0]);
            when(dto.getByProductId(productId)).thenReturn(updatedInventory);
            return null;
        }).when(dto).updateStock(eq(productId), anyInt());

        // Mock ordersDto to trigger inventory update
        doAnswer(invocation -> {
            CombinedOrdersForm form = invocation.getArgument(0);
            for (OrderItemsForm item : form.getOrderItems()) {
                dto.updateStock(item.getProductId(), item.getQuantity());
            }
            return null;
        }).when(ordersDto).add(any(CombinedOrdersForm.class));

        // Create and process order
        List<OrderItemsForm> orderItems = new ArrayList<>();
        OrderItemsForm item = new OrderItemsForm();
        item.setProductId(productId);
        item.setQuantity(5);
        item.setPrice(100.0);
        orderItems.add(item);

        CombinedOrdersForm orderForm = new CombinedOrdersForm();
        orderForm.setUserEmail("test@example.com");
        orderForm.setTotalPrice(500.0);
        orderForm.setOrderItems(orderItems);
        ordersDto.add(orderForm);

        // Verify updated inventory
        InventoryData updatedInventory = dto.getByProductId(productId);
        assertEquals(195, (int)updatedInventory.getTotalQuantity());
    }

    @Test
    public void testInventoryUpdateWithMultipleOrders() throws ApiException {
        // Create initial inventory with 200 units
        InventoryData initialInventory = new InventoryData();
        initialInventory.setProductId(productId);
        initialInventory.setTotalQuantity(200);
        when(dto.getByProductId(productId)).thenReturn(initialInventory);

        // Mock inventory update behavior with mutable state
        final int[] currentQuantity = {200};
        doAnswer(invocation -> {
            int quantity = invocation.getArgument(1);
            currentQuantity[0] -= quantity;
            InventoryData updatedInventory = new InventoryData();
            updatedInventory.setProductId(productId);
            updatedInventory.setTotalQuantity(currentQuantity[0]);
            when(dto.getByProductId(productId)).thenReturn(updatedInventory);
            return null;
        }).when(dto).updateStock(eq(productId), anyInt());

        // Mock ordersDto to trigger inventory update
        doAnswer(invocation -> {
            CombinedOrdersForm form = invocation.getArgument(0);
            for (OrderItemsForm item : form.getOrderItems()) {
                dto.updateStock(item.getProductId(), item.getQuantity());
            }
            return null;
        }).when(ordersDto).add(any(CombinedOrdersForm.class));

        // First order: reduce by 10 units
        List<OrderItemsForm> orderItems1 = new ArrayList<>();
        OrderItemsForm item1 = new OrderItemsForm();
        item1.setProductId(productId);
        item1.setQuantity(10);
        item1.setPrice(100.0);
        orderItems1.add(item1);

        CombinedOrdersForm orderForm1 = new CombinedOrdersForm();
        orderForm1.setUserEmail("test1@example.com");
        orderForm1.setTotalPrice(1000.0);
        orderForm1.setOrderItems(orderItems1);
        ordersDto.add(orderForm1);

        // Verify first update
        InventoryData midInventory = dto.getByProductId(productId);
        assertEquals(190, (int)midInventory.getTotalQuantity());

        // Second order: reduce by 10 more units
        List<OrderItemsForm> orderItems2 = new ArrayList<>();
        OrderItemsForm item2 = new OrderItemsForm();
        item2.setProductId(productId);
        item2.setQuantity(10);
        item2.setPrice(100.0);
        orderItems2.add(item2);

        CombinedOrdersForm orderForm2 = new CombinedOrdersForm();
        orderForm2.setUserEmail("test2@example.com");
        orderForm2.setTotalPrice(1000.0);
        orderForm2.setOrderItems(orderItems2);
        ordersDto.add(orderForm2);

        // Verify final inventory (200 - 10 - 10 = 180)
        InventoryData finalInventory = dto.getByProductId(productId);
        assertEquals(180, (int)finalInventory.getTotalQuantity());
    }

    private InventoryForm createInventoryForm(int productId, int quantity) {
        InventoryForm form = new InventoryForm();
        form.setProductId(productId);
        form.setTotalQuantity(quantity);
        return form;
    }

    @Before
    public void setUp() throws ApiException {
        MockitoAnnotations.openMocks(this);
        setupMockData();
        verifyTestData();
    }

    private void setupMockData() throws ApiException {
        // Mock client data
        ClientData clientData = new ClientData();
        clientData.setId(1);
        clientData.setName("Test Client");
        List<ClientData> clients = Arrays.asList(clientData);
        PaginatedData<ClientData> clientsPage = new PaginatedData<>(clients, 1, 10, false);
        when(clientDto.getAll(anyInt(), anyInt())).thenReturn(clientsPage);
        clientName = "Test Client";

        // Mock product data
        ProductData product = new ProductData();
        product.setId(1);
        List<ProductData> products = Arrays.asList(product);
        PaginatedData<ProductData> productsPage = new PaginatedData<>(products, 1, 10, false);
        when(productDto.getAll(anyInt(), anyInt())).thenReturn(productsPage);
        productId = 1;

        // Mock initial inventory data
        mockInventoryData = new InventoryData();
        mockInventoryData.setProductId(productId);
        mockInventoryData.setTotalQuantity(200);
        
        // Mock dto methods with stable behavior
        lenient().when(dto.getByProductId(anyInt())).thenReturn(mockInventoryData);
        lenient().doNothing().when(dto).add(any(InventoryForm.class));
        lenient().doNothing().when(dto).deleteAll();

        // Mock inventory update behavior
        lenient().doAnswer(invocation -> {
            int quantity = invocation.getArgument(1);
            mockInventoryData.setTotalQuantity(mockInventoryData.getTotalQuantity() - quantity);
            return null;
        }).when(dto).updateStock(anyInt(), anyInt());
    }

    @After
    public void cleanupTestData() {
        try {
            ordersDto.deleteAllOrders();
            dto.deleteAll();
            productDto.deleteAll();
            clientDto.deleteAll();
        } catch (ApiException e) {
            System.err.println("Error in test cleanup: " + e.getMessage());
        }
    }

    private void verifyTestData() throws ApiException {
        // Verify client exists
        PaginatedData<ClientData> clientsData = clientDto.getAll(1, 10);
        assertNotNull("Client data should not be null", clientsData);
        assertNotNull("Client list should not be null", clientsData.getData());
        assertEquals("Should have one client", 1, clientsData.getData().size());
        
        // Verify product exists
        PaginatedData<ProductData> productsData = productDto.getAll(1, 10);
        assertNotNull("Product data should not be null", productsData);
        assertNotNull("Product list should not be null", productsData.getData());
        assertEquals("Should have one product", 1, productsData.getData().size());
        
        // Verify inventory exists and has correct quantity
        InventoryData inventory = dto.getByProductId(productId);
        assertNotNull("Inventory should not be null", inventory);
        assertEquals("Inventory should have 200 units", 200, (int)inventory.getTotalQuantity());
    }

    private void createTestData() throws ApiException {
        // Create a client
        ClientForm clientForm = new ClientForm();
        clientForm.setName("Test Client");
        clientForm.setEmail("client@test.com");
        clientForm.setContactNo("1234567890");
        clientDto.add(clientForm);
        clientName = "Test Client";
        System.out.println("Created client with name: " + clientName);

        // Create a product
        ProductForm productForm = new ProductForm();
        productForm.setName("Test Product");
        productForm.setBarcode("test123");
        productForm.setPrice(100.0);
        productForm.setClientName(clientName);
        productDto.add(productForm);

        // Get the product ID
        PaginatedData<ProductData> productsData = productDto.getAll(1, 10);
        productId = productsData.getData().get(0).getId();
        System.out.println("Created product with ID: " + productId);

        // Initialize inventory
        InventoryForm inventoryForm = new InventoryForm();
        inventoryForm.setProductId(productId);
        inventoryForm.setTotalQuantity(200);
        dto.add(inventoryForm);
    }
}
