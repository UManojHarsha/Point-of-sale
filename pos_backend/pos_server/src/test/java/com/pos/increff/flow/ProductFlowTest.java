package com.pos.increff.flow;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.AbstractMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import com.pos.increff.api.ApiException;
import com.pos.increff.api.ProductApi;
import com.pos.increff.api.InventoryApi;
import com.pos.increff.api.ClientApi;
import com.pos.increff.pojo.ProductPojo;
import com.pos.increff.pojo.InventoryPojo;
import com.pos.increff.pojo.ClientPojo;

public class ProductFlowTest {

    @InjectMocks
    private ProductFlow productFlow;

    @Mock
    private ProductApi productApi;

    @Mock
    private InventoryApi inventoryApi;

    @Mock
    private ClientApi clientApi;

    private static final String DEFAULT_CLIENT_NAME = "Client1";
    private static final int DEFAULT_CLIENT_ID = 1;
    // Test add method
    @Test
    public void testAdd_Success() throws ApiException {
        // Prepare test data
        List<ProductPojo> products = new ArrayList<>();
        products.add(createTestProduct(1, "Test Product", DEFAULT_CLIENT_ID));

        // Mock behavior
        doNothing().when(productApi).add(products);
        doNothing().when(inventoryApi).add(anyList());

        // Execute test
        productFlow.add(products);

        // Verify interactions
        verify(productApi).add(products);
        verify(inventoryApi).add(argThat((List<InventoryPojo> list) -> {
            if (list == null || list.size() != 1) return false;
            InventoryPojo inventory = list.get(0);
            return inventory.getProductId() == 1 && inventory.getTotalQuantity() == 0;
        }));
    }

    @Test(expected = ApiException.class)
    public void testAdd_ProductApiFailure() throws ApiException {
        // Prepare test data
        List<ProductPojo> products = new ArrayList<>();
        products.add(createTestProduct(1, "Test Product", DEFAULT_CLIENT_ID));

        // Mock behavior
        doThrow(new ApiException("Failed to add products")).when(productApi).add(products);

        // Execute test
        productFlow.add(products);
    }

    // Test getAll method
    @Test
    public void testGetAll_Success() throws ApiException {
        // Prepare test data
        ProductPojo product = createTestProduct(1, "Test Product", DEFAULT_CLIENT_ID);
        ClientPojo client = createTestClient(DEFAULT_CLIENT_ID, "Test Client");
        List<ProductPojo> products = new ArrayList<>();
        products.add(product);

        // Mock behavior
        when(productApi.getAll(1, 10)).thenReturn(products);
        when(clientApi.get(DEFAULT_CLIENT_ID)).thenReturn(client);

        // Execute test
        Map.Entry<List<ProductPojo>, List<ClientPojo>> result = productFlow.getAll(1, 10);

        // Verify result
        assertNotNull(result);
        assertEquals(1, result.getKey().size());
        assertEquals(1, result.getValue().size());
        assertEquals(product, result.getKey().get(0));
        assertEquals(client, result.getValue().get(0));
    }

    @Test(expected = ApiException.class)
    public void testGetAll_ClientNotFound() throws ApiException {
        // Prepare test data
        List<ProductPojo> products = new ArrayList<>();
        products.add(createTestProduct(1, "Test Product", DEFAULT_CLIENT_ID));

        // Mock behavior
        when(productApi.getAll(1, 10)).thenReturn(products);
        when(clientApi.get(DEFAULT_CLIENT_ID)).thenThrow(new ApiException("Client not found"));

        // Execute test
        productFlow.getAll(1, 10);
    }

    // Test TSV file processing
    @Test
    public void testProcessTSVFile_Success() throws ApiException, IOException {
        // Prepare test data
        String tsvContent = createTsvContent(
            "Product1\tBAR1\t100.0\t" + DEFAULT_CLIENT_NAME,
            "Product2\tBAR2\t200.0\t" + DEFAULT_CLIENT_NAME
        );
        MockMultipartFile file = createTsvFile(tsvContent);

        // Mock behavior
        mockDefaultClientLookup();
        when(productApi.getByName(anyString())).thenReturn(null);
        when(productApi.getByBarcode(anyString())).thenReturn(null);

        // Execute test
        productFlow.processTSVFile(file);

        // Verify product addition
        verify(productApi).add(argThat((List<ProductPojo> list) -> {
            if (list == null || list.size() != 2) return false;
            ProductPojo p1 = list.get(0);
            ProductPojo p2 = list.get(1);
            return p1.getName().equals("Product1") && p2.getName().equals("Product2");
        }));
    }

    @Test(expected = ApiException.class)
    public void testProcessTSVFile_DuplicateProductName() throws ApiException, IOException {
        // Prepare test data
        String tsvContent = createTsvContent(
            "Product1\tBAR1\t100.0\t" + DEFAULT_CLIENT_NAME,
            "Product1\tBAR2\t200.0\t" + DEFAULT_CLIENT_NAME
        );
        MockMultipartFile file = createTsvFile(tsvContent);

        // Mock behavior
        mockDefaultClientLookup();

        // Execute test
        productFlow.processTSVFile(file);
    }

    @Test(expected = ApiException.class)
    public void testProcessTSVFile_InvalidPrice() throws ApiException, IOException {
        // Prepare test data
        String tsvContent = createTsvContent(
            "Product1\tBAR1\t-100.0\t" + DEFAULT_CLIENT_NAME
        );
        MockMultipartFile file = createTsvFile(tsvContent);

        // Execute test
        productFlow.processTSVFile(file);
    }

    @Test(expected = ApiException.class)
    public void testProcessTSVFile_EmptyFile() throws ApiException, IOException {
        // Execute test with just headers
        productFlow.processTSVFile(createTsvFile("name\tbarcode\tprice\tclientName"));
    }

    @Test(expected = NullPointerException.class)
    public void testProcessTSVFile_TooManyRows() throws ApiException, IOException {
        // Create content with more than MAX_ROWS
        StringBuilder content = new StringBuilder("name\tbarcode\tprice\tclientName\n");
        for (int i = 0; i < 5001; i++) {
            content.append(String.format("Product%d\tBAR%d\t100.0\t%s\n", i, i, DEFAULT_CLIENT_NAME));
        }
        MockMultipartFile file = createTsvFile(content.toString());

        // Execute test
        productFlow.processTSVFile(file);
    }

    @Test(expected = ApiException.class)
    public void testProcessTSVFile_MissingColumns() throws ApiException, IOException {
        // Prepare test data with missing columns
        String tsvContent = createTsvContent("Product1\tBAR1\t100.0");
        MockMultipartFile file = createTsvFile(tsvContent);

        // Execute test
        productFlow.processTSVFile(file);
    }

    @Test(expected = ApiException.class)
    public void testProcessTSVFile_DuplicateBarcode() throws ApiException, IOException {
        // Prepare test data
        String tsvContent = createTsvContent(
            "Product1\tBAR1\t100.0\t" + DEFAULT_CLIENT_NAME,
            "Product2\tBAR1\t200.0\t" + DEFAULT_CLIENT_NAME
        );
        MockMultipartFile file = createTsvFile(tsvContent);

        // Mock behavior
        mockDefaultClientLookup();

        // Execute test
        productFlow.processTSVFile(file);
    }

    @Test(expected = ApiException.class)
    public void testProcessTSVFile_NonexistentClient() throws ApiException, IOException {
        // Prepare test data
        String tsvContent = createTsvContent(
            "Product1\tBAR1\t100.0\tNonexistentClient"
        );
        MockMultipartFile file = createTsvFile(tsvContent);

        // Mock client lookup to throw exception
        when(clientApi.getByName("NonexistentClient"))
            .thenThrow(new ApiException("Client not found"));

        // Execute test
        productFlow.processTSVFile(file);
    }

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Helper methods for creating test data
    private ProductPojo createTestProduct(int id, String name, int clientId) {
        ProductPojo product = new ProductPojo();
        product.setId(id);
        product.setName(name);
        product.setClientId(clientId);
        return product;
    }

    private ClientPojo createTestClient(int id, String name) {
        ClientPojo client = new ClientPojo();
        client.setId(id);
        client.setName(name);
        return client;
    }

    private MockMultipartFile createTsvFile(String content) {
        return new MockMultipartFile(
            "file", 
            "test.tsv",
            "text/tab-separated-values", 
            content.getBytes(StandardCharsets.UTF_8)
        );
    }

    private String createTsvContent(String... rows) {
        StringBuilder content = new StringBuilder("name\tbarcode\tprice\tclientName\n");
        for (String row : rows) {
            content.append(row).append("\n");
        }
        return content.toString().trim();
    }

    private void mockDefaultClientLookup() throws ApiException {
        ClientPojo client = createTestClient(DEFAULT_CLIENT_ID, DEFAULT_CLIENT_NAME);
        when(clientApi.getByName(DEFAULT_CLIENT_NAME)).thenReturn(client);
    }

}
