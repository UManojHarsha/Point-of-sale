package com.pos.increff.flow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.pos.increff.api.InventoryApi;
import com.pos.increff.pojo.InventoryPojo;
import com.pos.increff.api.ApiException;
import com.pos.increff.api.ProductApi;
import com.pos.increff.pojo.ProductPojo;

@Component
public class InventoryFlow {
    @Autowired 
    private InventoryApi inventoryApi;

    @Autowired
    private ProductApi productApi;

    public void add(InventoryPojo p) throws ApiException {
        ProductPojo product = productApi.get(p.getProductId());
        if (product == null) {
            throw new ApiException("Product with given ID does not exist");
        }
        inventoryApi.add(p);
    }

    public void create(InventoryPojo p) throws ApiException {
        inventoryApi.add(p);
    }
}

