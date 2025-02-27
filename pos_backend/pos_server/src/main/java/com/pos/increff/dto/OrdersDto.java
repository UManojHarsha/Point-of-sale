package com.pos.increff.dto;

import com.pos.increff.api.ApiException;
import com.pos.increff.api.OrderItemsApi;
import com.pos.increff.api.OrdersApi;
import com.pos.increff.flow.OrderFlow;
import com.pos.increff.pojo.OrderItemsPojo;
import com.pos.increff.pojo.OrdersPojo;
import com.pos.commons.OrderItemsData;
import com.pos.commons.OrdersData;
import com.pos.commons.OrderStatus;
import com.pos.commons.client.AppClientException;
import com.pos.commons.client.InvoiceClient;
import com.pos.commons.InvoiceGenerationRequest;
import com.pos.increff.model.form.CombinedOrdersForm;
import com.pos.increff.model.form.OrderItemsForm;
import com.pos.increff.model.data.PaginatedData;
import com.pos.increff.util.DateTimeUtils;
import com.pos.increff.util.ConversionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.client.RestTemplate;

@Component
public class OrdersDto extends AbstractDto {

    @Autowired
    private OrderFlow orderFlow;

    @Autowired
    private OrdersApi orderApi;

    @Autowired
    private OrderItemsApi orderItemsApi;

    @Autowired
    private ConversionUtil conversionUtil;

    @Autowired
    private OrdersApi ordersApi;

    @Autowired
    private RestTemplate restTemplate;
    
    private static final String INVOICE_SERVICE_URL = "http://localhost:9002";

    //Autowire this : TODO
    private InvoiceClient getInvoiceClient() {
        return new InvoiceClient(INVOICE_SERVICE_URL, restTemplate);
    }

    //Remove sout statements
    //TODO: Handle orderpojos in the flow layer and send only email
    public void add(CombinedOrdersForm form) throws ApiException {
        System.out.println(form.getOrderItems()+" order items");
        System.out.println(form.getTotalPrice()+" total price");
        System.out.println(form.getUserEmail()+" user email");
        checkValid(form); 
        for (OrderItemsForm item : form.getOrderItems()) {
            checkValid(item);
        }
        OrdersPojo order = conversionUtil.convert(form);
        int orderId = ordersApi.add(order);
        List<OrderItemsPojo> orderItems = conversionUtil.convertItems(form.getOrderItems(), orderId);
        orderFlow.add(orderId , orderItems);
    }

    //TODO: Remove next page and handle it in frontend
    //TODO: Split up the function
    public PaginatedData<OrdersData> getAll(int page, int pageSize) throws ApiException {
        List<OrdersPojo> orders = orderApi.getAll(page, pageSize);
        boolean hasNextPage = orders.size() > pageSize;
        if (hasNextPage) {
            orders = orders.subList(0, pageSize); 
        }
        List<OrdersData> list2 = new ArrayList<>();
        for (OrdersPojo p : orders) {
            list2.add(ConversionUtil.convert(p));
        }
        return new PaginatedData<OrdersData>(list2, page, pageSize, hasNextPage);
    }

    public void deleteAllOrders() throws ApiException {
        orderApi.deleteAll();
    }

    public OrdersData get(Integer orderId) throws ApiException {
        return ConversionUtil.convert(orderApi.get(orderId));
    }   

    //TODO: have a better name
    public List<OrderItemsData> getAllItems(Integer order_id) throws ApiException {
        List<OrderItemsPojo> items = orderItemsApi.getByOrderId(order_id);
        List<OrderItemsData> itemsData = new ArrayList<>();
        for (OrderItemsPojo item : items) { 
            itemsData.add(conversionUtil.convert(item));
        }
        return itemsData;
    }
    //TODO: Null check tyo be done in api layer
    //TODO: This fun is used only once so, shift it to api and have a reasonable name 
    public void update(Integer orderId, OrdersData orderData) throws ApiException {
        OrdersPojo order = orderApi.get(orderId);
        if (order == null) {
            throw new ApiException("Order not found: " + orderId);
        }
        
        order.setStatus(orderData.getStatus());
        order.setInvoicePath(orderData.getInvoicePath());
        
        orderApi.update(order);
    }

    //TODO: USe entities instead of orderdata and orderitemdata
    public String generateInvoice(Integer orderId) throws ApiException {
        OrdersData order = get(orderId);
        
        if (order.getStatus() != OrderStatus.PENDING_INVOICE) {
            throw new ApiException("Invoice already generated for this order");
        }

        List<OrderItemsData> orderItems = getAllItems(orderId);
        
        try {
            InvoiceGenerationRequest requestBody = new InvoiceGenerationRequest(order, orderItems);
            String invoicePath = getInvoiceClient().generateInvoice(orderId, requestBody);
            
            order.setStatus(OrderStatus.INVOICE_GENERATED);
            order.setInvoicePath(invoicePath);
            update(orderId, order);
            
            return invoicePath;
        } 
        catch (AppClientException e) {
            throw new ApiException("Failed to generate invoice: " + e.getMessage());
        }
    }

    public byte[] downloadInvoice(Integer orderId) throws ApiException {
        OrdersData order = get(orderId);
        
        if (order.getStatus() != OrderStatus.INVOICE_GENERATED) {
            throw new ApiException("Invoice not generated for this order");
        }

        try {
            byte[] pdfBytes = getInvoiceClient().downloadInvoice(orderId, order.getInvoicePath());
            order.setStatus(OrderStatus.COMPLETED);
            update(orderId, order);
            return pdfBytes;
        } 
        catch (AppClientException e) {
            throw new ApiException("Failed to download invoice: " + e.getMessage());
        }
    }

}
