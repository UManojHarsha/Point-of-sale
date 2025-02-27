package com.pos.invoice.service;

import com.pos.commons.OrdersData;
import com.pos.invoice.model.InvoiceDetails;
import com.pos.invoice.util.InvoiceUtil;
import com.pos.commons.OrderItemsData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import com.pos.commons.api.ApiException;

@Service
public class InvoiceService {
    
    @Autowired
    private InvoiceUtil invoiceUtil;

    public byte[] generateInvoiceBytes(InvoiceDetails details) throws ApiException {
        try {
            OrdersData order = new OrdersData();
            order.setId(details.getOrderId());
            order.setUserEmail(details.getUserEmail());
            order.setTotalPrice(details.getTotalPrice());
            order.setUpdatedDate(details.getOrderDate());

            List<OrderItemsData> items = details.getItems().stream().map(item -> {
                OrderItemsData data = new OrderItemsData();
                data.setProductName(item.getProductName());
                data.setQuantity(item.getQuantity());
                data.setPrice(item.getPrice());
                return data;
            }).collect(Collectors.toList());

            return invoiceUtil.generateInvoice(order, items);
        } catch (Exception e) {
            throw new ApiException("Error generating invoice: " + e.getMessage(), e);
        }
    }
} 