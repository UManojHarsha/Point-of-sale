package com.pos.increff.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.After;
import org.springframework.beans.factory.annotation.Autowired;

import com.pos.increff.config.AbstractUnitTest;
import com.pos.increff.pojo.ProductPojo;
import java.util.List;
import java.util.ArrayList;

public class ProductApiTest extends AbstractUnitTest {

    @Autowired
    private ProductApi api;

    @After
    public void setUp() {
        api.deleteAll();
    }

    @Test
    public void testAdd() throws ApiException {
        ProductPojo product = new ProductPojo();
        product.setName("test product");
        product.setBarcode("123456789");
        product.setPrice(200.0);
        product.setClientId(1);
        api.add(product);
        
        ProductPojo retrieved = api.getByName("test product");
        assertNotNull(retrieved);
        assertEquals("test product", retrieved.getName());
        assertEquals("123456789", retrieved.getBarcode());
        assertEquals(200L, retrieved.getPrice() , 0.01);
        assertEquals(1, retrieved.getClientId().intValue());
    }

    @Test(expected = ApiException.class)
    public void testAddDuplicate() throws ApiException {
        ProductPojo product1 = new ProductPojo();
        product1.setName("test product");
        product1.setBarcode("123456789");
        product1.setPrice(100.0);
        product1.setClientId(1);
        api.add(product1);

        ProductPojo product2 = new ProductPojo();
        product2.setName("test product");  // Same name as product1
        product2.setBarcode("987654321");  // Different barcode
        product2.setPrice(199.0);
        product2.setClientId(1);
        api.add(product2); // Should throw ApiException for duplicate name
    }

    @Test
    public void testUpdate() throws ApiException {
        // First add a product
        ProductPojo product = new ProductPojo();
        product.setName("test product");
        product.setBarcode("123456789");
        product.setPrice(99.0);
        product.setClientId(1);
        api.add(product);

        // Get the product and update
        int prevId = api.getByName("test product").getId();

        ProductPojo toUpdate = api.getByName("test product");
        toUpdate.setPrice(199.0);
        toUpdate.setBarcode("123456789");
        toUpdate.setClientId(1);
        toUpdate.setName("test product updated");
        api.update(toUpdate.getId(), toUpdate);

        // Verify update
        ProductPojo updated = api.get(toUpdate.getId());
        assertEquals(prevId, updated.getId().intValue());
        assertEquals("test product updated", updated.getName());
        assertEquals("123456789", updated.getBarcode());
        assertEquals(199L, updated.getPrice(), 0.01);
        assertEquals(1, updated.getClientId().intValue());
    }

    @Test(expected = ApiException.class)
    public void testGetNonExistent() throws ApiException {
        api.get(9999); // Should throw ApiException
    }

    @Test
    public void testGetAll() throws ApiException {
        // Add two products
        ProductPojo product1 = new ProductPojo();
        product1.setName("product1");
        product1.setBarcode("123456789");
        product1.setPrice(111.0);
        product1.setClientId(1);
        api.add(product1);

        ProductPojo product2 = new ProductPojo();
        product2.setName("product2");
        product2.setBarcode("987654321");
        product2.setPrice(112.0);
        product2.setClientId(1);
        api.add(product2);

        List<ProductPojo> products = api.getAll();
        assertEquals(2, products.size());
    }

    @Test
    public void testBulkAdd() throws ApiException {
        List<ProductPojo> products = new ArrayList<>();
        
        ProductPojo product1 = new ProductPojo();
        product1.setName("product1");
        product1.setBarcode("123456789");
        product1.setPrice(90.0);
        product1.setClientId(1);
        products.add(product1);

        ProductPojo product2 = new ProductPojo();
        product2.setName("product2");
        product2.setBarcode("987654321");
        product2.setPrice(150.0);
        product2.setClientId(1);
        products.add(product2);

        api.add(products);
        assertEquals(2, api.getAll().size());
    }
}
