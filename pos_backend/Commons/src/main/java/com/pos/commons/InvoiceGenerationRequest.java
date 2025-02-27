package com.pos.commons;

import java.util.List;  

public class InvoiceGenerationRequest {
    private final OrdersData order;
    private final List<OrderItemsData> orderItems;

    public InvoiceGenerationRequest(OrdersData order, List<OrderItemsData> orderItems) {
        this.order = order;
        this.orderItems = orderItems;
    }

    public OrdersData getOrder() {
        return order;
    }

    public List<OrderItemsData> getOrderItems() {
        return orderItems;
    }

    @Override
    public String toString() {
        return "InvoiceGenerationRequest{" +
               "order=" + order +
               ", orderItems=" + orderItems +
               '}';
    }
}