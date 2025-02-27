package com.pos.increff.flow;
import com.pos.increff.model.form.CombinedOrdersForm;
import com.pos.increff.model.form.OrderItemsForm;
import com.pos.increff.pojo.OrdersPojo;
import com.pos.increff.pojo.OrderItemsPojo;
import com.pos.increff.api.ApiException;
import com.pos.increff.api.InventoryApi;
import com.pos.increff.api.OrderItemsApi;
import com.pos.increff.api.OrdersApi;
import com.pos.commons.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Component
public class OrderFlow {

    @Autowired
    private OrdersApi ordersApi;

    @Autowired
    private OrderItemsApi orderItemsApi;

    @Autowired
    private InventoryApi inventoryApi;
   //TODO: Clean and split it into multiple methods
    @Transactional(rollbackFor = ApiException.class)
    public void add(int orderId , List<OrderItemsPojo> orderItems) throws ApiException {
        
         List<String> inventoryErrors = new ArrayList<>();
        
        for (OrderItemsPojo item : orderItems) {
            int productId = item.getProductId();
            int currentStock = inventoryApi.getStock(productId);
            String productName = inventoryApi.getProduct(productId).getName();
            if (currentStock < item.getQuantity()) {
                inventoryErrors.add("Insufficient stock for product '" + productName);
            }
        }
        if (!inventoryErrors.isEmpty()) {
            throw new ApiException("Error in inventory: " + String.join("; ", inventoryErrors));
        }
        
        for (OrderItemsPojo item : orderItems) {
            inventoryApi.updateStock(item.getProductId(), item.getQuantity());
        }
        orderItemsApi.add(orderItems);
    }
}