package com.pos.increff.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.After;
import org.springframework.beans.factory.annotation.Autowired;

import com.pos.increff.config.AbstractUnitTest;
import com.pos.increff.pojo.InventoryPojo;
import java.util.List;
import java.util.ArrayList;

public class InventoryApiTest extends AbstractUnitTest {

    @Autowired
    private InventoryApi api;

    @After
    public void setUp() {
        api.deleteAll();
    }

    @Test
    public void testAdd() throws ApiException {
        InventoryPojo inventory = new InventoryPojo();
        inventory.setProductId(1);
        inventory.setTotalQuantity(100);
        api.add(inventory);
        
        InventoryPojo retrieved = api.get(inventory.getId());
        assertNotNull(retrieved);
        assertEquals(1, retrieved.getProductId().intValue());
        assertEquals(100, retrieved.getTotalQuantity().intValue());
    }

    @Test
    public void testBulkAdd() throws ApiException {
        List<InventoryPojo> inventoryList = new ArrayList<>();
        
        InventoryPojo inventory1 = new InventoryPojo();
        inventory1.setProductId(1);
        inventory1.setTotalQuantity(100);
        inventoryList.add(inventory1);

        InventoryPojo inventory2 = new InventoryPojo();
        inventory2.setProductId(2);
        inventory2.setTotalQuantity(200);
        inventoryList.add(inventory2);

        api.add(inventoryList);
        assertEquals(2, api.getAll().size());
    }

    @Test
    public void testUpdate() throws ApiException {
        // First add inventory
        InventoryPojo inventory = new InventoryPojo();
        inventory.setProductId(1);
        inventory.setTotalQuantity(100);
        api.add(inventory);

        // Get and update
        InventoryPojo toUpdate = api.get(inventory.getId());
        toUpdate.setTotalQuantity(150);
        api.update(toUpdate.getProductId(), toUpdate);

        // Verify update
        InventoryPojo updated = api.get(toUpdate.getId());
        assertEquals(150, updated.getTotalQuantity().intValue());
    }

    @Test
    public void testUpdateStock() throws ApiException {
        // First add inventory
        InventoryPojo inventory = new InventoryPojo();
        inventory.setProductId(1);
        inventory.setTotalQuantity(100);
        api.add(inventory);

        // Update stock
        api.updateStock(1, 75);

        // Verify stock update
        assertEquals(25, api.getStock(1));
    }


    @Test
    public void updateStock() throws ApiException {
        InventoryPojo inventory = new InventoryPojo() ;
        inventory.setProductId(1) ;
        inventory.setTotalQuantity(100);
        api.add(inventory);

        InventoryPojo updatedPojo = new InventoryPojo();
        updatedPojo.setProductId(1);
        updatedPojo.setTotalQuantity(75);
        api.update(1 , updatedPojo);
        assertEquals(75, api.getStock(1));
    }

    @Test(expected = ApiException.class)
    public void testGetNonExistent() throws ApiException {
        api.get(9999); // Should throw ApiException
    }

    @Test
    public void testGetAll() throws ApiException {
        List<InventoryPojo> inventory = api.getAll(1, 10);
        assertEquals(0, inventory.size());
    }
}
