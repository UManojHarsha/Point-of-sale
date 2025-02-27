package com.pos.invoice.dto;

import com.pos.invoice.flow.InvoiceFlow;
import com.pos.commons.api.ApiException;
import com.pos.commons.OrdersData;
import com.pos.commons.OrderItemsData;
import com.pos.invoice.model.InvoiceDetails;
import com.pos.invoice.model.InvoiceItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InvoiceDto {
    @Autowired
    private InvoiceFlow flow;

    //TODO: Split this function into smaller functions
    public String generateInvoice(Integer orderId, OrdersData order, List<OrderItemsData> orderItems) throws ApiException {
        if (orderId == null) {
            throw new ApiException("Order ID cannot be null");
        }
        
        InvoiceDetails details = new InvoiceDetails();
        details.setOrderId(orderId);
        details.setUserEmail(order.getUserEmail());
        details.setTotalPrice(order.getTotalPrice());
        details.setOrderDate(order.getUpdatedDate());
        
        // Convert OrderItemsData to InvoiceItem
        List<InvoiceItem> invoiceItems = orderItems.stream()
            .map(item -> {
                InvoiceItem invoiceItem = new InvoiceItem();
                invoiceItem.setProductName(item.getProductName());
                invoiceItem.setQuantity(item.getQuantity());
                invoiceItem.setPrice(item.getPrice());
                invoiceItem.setTotal(item.getQuantity() * item.getPrice());
                return invoiceItem;
            })
            .collect(Collectors.toList());
        
        details.setItems(invoiceItems);
        
        return flow.generateInvoice(details);
    }
}

