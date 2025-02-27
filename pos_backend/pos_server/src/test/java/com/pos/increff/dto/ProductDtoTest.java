package com.pos.increff.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyString;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.pos.increff.config.AbstractUnitTest;
import com.pos.increff.model.data.ProductData;
import com.pos.increff.model.data.PaginatedData;
import com.pos.increff.model.form.ProductForm;
import com.pos.increff.api.ApiException;
import com.pos.increff.api.ClientApi;
import com.pos.increff.api.ProductApi;
import com.pos.increff.pojo.ClientPojo;
import com.pos.increff.pojo.ProductPojo;
import com.pos.increff.flow.ProductFlow;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.AbstractMap;

public class ProductDtoTest extends AbstractUnitTest {

    @InjectMocks
    private ProductDto dto;

    @Mock
    private ProductApi productApi;

    @Mock
    private ProductFlow productFlow;

    @Mock
    private ClientApi clientApi;

    private String clientName;

    @Test

    //TODO: Write integration tests in dto layer
    public void testAdd() throws ApiException {
        // Create a valid product form
        ProductForm form = createProductForm("test product", 100.0, "test123", clientName);
        
        // Mock the client lookup with proper client setup
        ClientPojo mockClient = new ClientPojo();
        mockClient.setId(1);
        mockClient.setName(clientName);
        when(clientApi.getByName(form.getClientName())).thenReturn(mockClient);
        
        // Mock the behavior for duplicate checks with proper null returns
        when(productApi.getByName(form.getName())).thenReturn(null);
        when(productApi.getByBarcode(form.getBarcode())).thenReturn(null);
        
        // Mock the flow to not throw any exceptions
        doNothing().when(productFlow).add(anyList());
        
        // Execute the test
        dto.add(form);
        
        // Verify that productFlow.add was called with the correct parameters
        verify(productFlow).add(argThat(list -> {
            if (list == null || list.size() != 1) return false;
            ProductPojo product = list.get(0);
            return product != null &&
                   form.getName().equals(product.getName()) &&
                   form.getBarcode().equals(product.getBarcode()) &&
                   Math.abs(form.getPrice() - product.getPrice()) < 0.001 &&
                   product.getClientId() == mockClient.getId();
        }));
    }

    @Test
    public void testBulkAdd() throws ApiException {
        // Create valid product forms
        List<ProductForm> forms = Arrays.asList(
            createProductForm("test product 1", 100.0, "test123", clientName),
            createProductForm("test product 2", 200.0, "test456", clientName)
        );
        
        // Mock the client lookup with proper client setup
        ClientPojo mockClient = new ClientPojo();
        mockClient.setId(1);
        mockClient.setName(clientName);
        when(clientApi.getByName(clientName)).thenReturn(mockClient);
        
        // Mock the duplicate checks for all products
        for(ProductForm form : forms) {
            when(productApi.getByName(form.getName())).thenReturn(null);
            when(productApi.getByBarcode(form.getBarcode())).thenReturn(null);
        }
        
        // Mock the flow to not throw any exceptions
        doNothing().when(productFlow).add(anyList());
        
        // Execute the test
        dto.add(forms);
        
        // Verify that productFlow.add was called with the correct parameters
        verify(productFlow).add(argThat(list -> {
            if (list == null || list.size() != forms.size()) return false;
            
            for (int i = 0; i < forms.size(); i++) {
                ProductForm form = forms.get(i);
                ProductPojo product = list.get(i);
                
                if (product == null ||
                    !form.getName().equals(product.getName()) ||
                    !form.getBarcode().equals(product.getBarcode()) ||
                    Math.abs(form.getPrice() - product.getPrice()) >= 0.001 ||
                    product.getClientId() != mockClient.getId()) {
                    return false;
                }
            }
            return true;
        }));
    }

    @Test(expected = ApiException.class)
    public void testBulkAddWithDuplicateBarcodes() throws ApiException {
        List<ProductForm> forms = Arrays.asList(
            createProductForm("product 1", 100.0, "same-barcode", clientName),
            createProductForm("product 2", 200.0, "same-barcode", clientName)
        );
        
        // Mock the client lookup
        when(clientApi.getByName(anyString())).thenReturn(new ClientPojo());
        
        // Mock to simulate duplicate barcode
        when(productApi.getByBarcode("same-barcode")).thenReturn(new ProductPojo());
        
        dto.add(forms);
    }

    @Test(expected = ApiException.class)
    public void testBulkAddWithInvalidPrice() throws ApiException {
        List<ProductForm> forms = Arrays.asList(
            createProductForm("product 1", 100.0, "test123", clientName),
            createProductForm("product 2", -200.0, "test456", clientName)
        );
        
        dto.add(forms);
    }

    @Test(expected = ApiException.class)
    public void testBulkAddWithEmptyList() throws ApiException {
        dto.add(new ArrayList<>());
    }

    @Test(expected = ApiException.class)
    public void testBulkAddWithNullList() throws ApiException {
        // Mock the behavior to throw ApiException for null list
        doThrow(new ApiException("Product list cannot be null"))
            .when(productFlow).add(null);
        
        dto.add((List<ProductForm>) null);
    }

    @Test(expected = ApiException.class)
    public void testAddInvalidPrice() throws ApiException {
        ProductForm form = createProductForm("Test Product", -100.0, "BARCODE123", clientName);
        dto.add(form); // Should throw ApiException due to negative price
    }

    @Test(expected = ApiException.class)
    public void testAddEmptyName() throws ApiException {
        ProductForm form = createProductForm("", 100.0, "BARCODE123", clientName);
        dto.add(form); // Should throw ApiException due to empty name
    }

    @Test
    public void testGet() throws ApiException {
        // Mock the product data
        ProductPojo mockProduct = new ProductPojo();
        mockProduct.setId(1);
        mockProduct.setName("test product");
        mockProduct.setBarcode("test123");
        mockProduct.setPrice(100.0);
        mockProduct.setClientId(1);
        when(productApi.get(1)).thenReturn(mockProduct);

        ProductData product = dto.get(1);
        assertNotNull(product);
        assertEquals("test product", product.getName());
    }

    @Test(expected = ApiException.class)
    public void testGetNonExistent() throws ApiException {
        dto.get(9999); // Should throw ApiException
    }

    @Test
    public void testGetAll() throws ApiException {
        // Mock behavior for multiple products
        ProductPojo mockProduct1 = new ProductPojo();
        mockProduct1.setId(1);
        mockProduct1.setName("test product 1");
        ProductPojo mockProduct2 = new ProductPojo();
        mockProduct2.setId(2);
        mockProduct2.setName("test product 2");
        List<ProductPojo> products = Arrays.asList(mockProduct1, mockProduct2);
        
        ClientPojo mockClient = new ClientPojo();
        mockClient.setId(1);
        mockClient.setName(clientName);
        List<ClientPojo> clients = Arrays.asList(mockClient, mockClient);
        
        when(productFlow.getAll(anyInt(), anyInt()))
            .thenReturn(new AbstractMap.SimpleEntry<>(products, clients));

        PaginatedData<ProductData> paginatedData = dto.getAll(1, 10);
        List<ProductData> resultProducts = paginatedData.getData();
        assertEquals(2, resultProducts.size());
    }

    @Test
    public void testUpdate() throws ApiException {
        // Mock the existing product
        ProductPojo existingProduct = new ProductPojo();
        existingProduct.setId(1);
        existingProduct.setName("test product");
        existingProduct.setBarcode("test123");
        existingProduct.setPrice(100.0);
        existingProduct.setClientId(1);
        when(productApi.get(1)).thenReturn(existingProduct);
        
        // Create valid update form
        ProductForm updateForm = createProductForm("updated product", 200.0, "test456", clientName);
        
        // Mock the client lookup
        ClientPojo mockClient = new ClientPojo();
        mockClient.setId(1);
        mockClient.setName(clientName);
        when(clientApi.getByName(updateForm.getClientName())).thenReturn(mockClient);
        
        // Mock the duplicate checks
        when(productApi.getByName(updateForm.getName())).thenReturn(null);
        when(productApi.getByBarcode(updateForm.getBarcode())).thenReturn(null);
        
        // Mock the update to not throw any exceptions
        doNothing().when(productApi).update(anyInt(), any(ProductPojo.class));
        
        // Execute the update
        dto.update(1, updateForm);
        
        // Verify the update
        verify(productApi).update(eq(1), argThat(product ->
            product.getName().equals(updateForm.getName()) &&
            product.getBarcode().equals(updateForm.getBarcode()) &&
            Math.abs(product.getPrice() - updateForm.getPrice()) < 0.001 &&
            product.getClientId() == mockClient.getId()
        ));
    }

    @Test(expected = ApiException.class)
    public void testUpdateNonExistent() throws ApiException {
        ProductForm form = createProductForm("Test Product", 100.0, "BARCODE123", clientName);
        dto.update(9999, form); // Should throw ApiException
    }

    @Test(expected = ApiException.class)
    public void testUpdateInvalidData() throws ApiException {
        // Mock existing product
        ProductPojo existingProduct = new ProductPojo();
        existingProduct.setId(1);
        when(productApi.get(1)).thenReturn(existingProduct);
        
        // Try to update with invalid data
        ProductForm updateForm = createProductForm("", -100.0, "", clientName);
        dto.update(1, updateForm); // Should throw ApiException
    }

    @After
    public void tearDown() {
        reset(productApi, productFlow, clientApi);
    }

    // File Upload Tests
    // @Test
    // public void testUploadProductMastersSuccess() throws ApiException, IOException {
    //     // Create a mock MultipartFile
    //     MultipartFile mockFile = mock(MultipartFile.class);
    //     when(mockFile.isEmpty()).thenReturn(false);
    //     when(mockFile.getOriginalFilename()).thenReturn("test.tsv");
        
    //     // Create a mock input stream with valid TSV content
    //     String tsvContent = "name\tbarcode\tprice\tclient_name\n" +
    //                        "product1\tbar123\t100.0\tTest Client";
    //     InputStream inputStream = new ByteArrayInputStream(tsvContent.getBytes());
    //     when(mockFile.getInputStream()).thenReturn(inputStream);
        
    //     // Mock the flow behavior
    //     doNothing().when(productFlow).processTSVFile(mockFile);
        
    //     // Execute test
    //     dto.uploadProductMasters(mockFile);
        
    //     // Verify
    //     verify(productFlow).processTSVFile(mockFile);
    // }


    // @Test(expected = ApiException.class)
    // public void testUploadInvalidFileFormat() throws ApiException, IOException {
    //     MultipartFile mockFile = mock(MultipartFile.class);
    //     when(mockFile.isEmpty()).thenReturn(false);
    //     when(mockFile.getOriginalFilename()).thenReturn("test.txt");
        
    //     doThrow(new ApiException("Invalid file format"))
    //         .when(productFlow).processTSVFile(mockFile);
        
    //     dto.uploadProductMasters(mockFile);
    // }

    // Delete Tests
    @Test
    public void testDeleteSuccess() throws ApiException {
        doNothing().when(productApi).delete(1);
        dto.delete(1);
        verify(productApi).delete(1);
    }

    @Test(expected = ApiException.class)
    public void testDeleteNullId() throws ApiException {
        dto.delete(null);
    }

    @Test
    public void testDeleteAllSuccess() throws ApiException {
        doNothing().when(productApi).deleteAll();
        dto.deleteAll();
        verify(productApi).deleteAll();
    }

    // Search Tests
    @Test
    public void testSearchByNameSuccess() throws ApiException {
        String searchName = "test";
        List<ProductPojo> mockProducts = new ArrayList<>();
        ProductPojo product = new ProductPojo();
        product.setName("test product");
        mockProducts.add(product);
        
        when(productApi.searchByName(eq(searchName), anyInt(), anyInt()))
            .thenReturn(mockProducts);
        
        PaginatedData<ProductData> result = dto.searchByName(searchName, 1, 10);
        
        assertNotNull(result);
        assertEquals(1, result.getData().size());
        assertEquals("test product", result.getData().get(0).getName());
    }

    @Test
    public void testSearchByNameEmptyResult() throws ApiException {
        when(productApi.searchByName(anyString(), anyInt(), anyInt()))
            .thenReturn(new ArrayList<>());
        
        PaginatedData<ProductData> result = dto.searchByName("nonexistent", 1, 10);
        
        assertNotNull(result);
        assertEquals(0, result.getData().size());
    }

    // Template Download Test
    @Test
    public void testDownloadTemplateTsv() {
        ResponseEntity<Resource> response = dto.downloadTemplateTsv();
        
        assertNotNull(response);
        assertEquals("attachment; filename=product_template.tsv", 
                     response.getHeaders().getFirst("Content-Disposition"));
        assertEquals("text/tab-separated-values", 
                     response.getHeaders().getFirst("Content-Type"));
    }

    // Integration Tests
    @Test
    public void testCompleteProductLifecycle() throws ApiException {
        // Create a product
        ProductForm form = createProductForm("lifecycle test", 150.0, "lifecycle123", clientName);
        
        // Mock client lookup
        ClientPojo mockClient = new ClientPojo();
        mockClient.setId(1);
        mockClient.setName(clientName);
        when(clientApi.getByName(form.getClientName())).thenReturn(mockClient);
        
        // Mock duplicate checks
        when(productApi.getByName(form.getName())).thenReturn(null);
        when(productApi.getByBarcode(form.getBarcode())).thenReturn(null);
        
        // Mock flow behavior
        doNothing().when(productFlow).add(anyList());
        
        // Add the product
        dto.add(form);
        
        // Mock the product retrieval
        ProductPojo addedProduct = new ProductPojo();
        addedProduct.setId(1);
        addedProduct.setName(form.getName());
        addedProduct.setBarcode(form.getBarcode());
        addedProduct.setPrice(form.getPrice());
        addedProduct.setClientId(mockClient.getId());
        when(productApi.get(1)).thenReturn(addedProduct);
        
        // Verify the rest of the lifecycle
        ProductData retrievedProduct = dto.get(1);
        assertEquals(form.getName(), retrievedProduct.getName());
        assertEquals(form.getBarcode(), retrievedProduct.getBarcode());
        assertEquals(form.getPrice(), retrievedProduct.getPrice(), 0.001);
        
        // Update and delete verifications
        doNothing().when(productApi).update(anyInt(), any(ProductPojo.class));
        doNothing().when(productApi).delete(anyInt());
        
        ProductForm updateForm = createProductForm("updated lifecycle", 200.0, "lifecycle456", clientName);
        dto.update(1, updateForm);
        dto.delete(1);
    }

    private ProductForm createProductForm(String name, double price, String barcode, String clientName) {
        ProductForm form = new ProductForm();
        form.setName(name != null ? name.toLowerCase().trim() : null);
        form.setPrice(price);
        form.setBarcode(barcode != null ? barcode.trim() : null);
        form.setClientName(clientName != null ? clientName.trim() : null);
        return form;
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();
        setupMockData();
    }

    private void setupMockData() throws ApiException {
        // Set up client name
        clientName = "Test Client";

        // Mock ClientApi behavior
        ClientPojo mockClient = new ClientPojo();
        mockClient.setId(1);
        mockClient.setName(clientName);
        when(clientApi.getByName(clientName)).thenReturn(mockClient);
        when(clientApi.getByName("")).thenThrow(new ApiException("Client name cannot be empty"));

        // Mock ProductApi behavior for successful case
        ProductPojo mockProduct = new ProductPojo();
        mockProduct.setId(1);
        mockProduct.setName("test product");
        mockProduct.setBarcode("test123");
        mockProduct.setPrice(100.0);
        mockProduct.setClientId(1);

        // Mock ProductApi get behavior
        when(productApi.get(1)).thenReturn(mockProduct);
        when(productApi.get(9999)).thenThrow(new ApiException("Product with ID 9999 does not exist"));

        // Mock ProductApi getByName behavior
        when(productApi.getByName("test product")).thenReturn(mockProduct);
        when(productApi.getByName("Test Product")).thenReturn(null);
        when(productApi.getByName("")).thenThrow(new ApiException("Product name cannot be empty"));

        // Mock ProductFlow behavior for getAll
        List<ProductPojo> products = new ArrayList<>();
        products.add(mockProduct);
        List<ClientPojo> clients = new ArrayList<>();
        clients.add(mockClient);
        when(productFlow.getAll(anyInt(), anyInt()))
            .thenReturn(new AbstractMap.SimpleEntry<>(products, clients));

        // Mock ProductFlow add behavior for single and bulk operations
        doNothing().when(productFlow).add(anyList());

        // Mock ProductApi update behavior
        doNothing().when(productApi).update(eq(1), any(ProductPojo.class));
        doThrow(new ApiException("Product with ID 9999 does not exist"))
            .when(productApi).update(eq(9999), any(ProductPojo.class));
    }
}

