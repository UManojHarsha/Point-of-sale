package com.pos.increff.flow;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.pos.increff.api.ApiException;
import com.pos.increff.model.data.ReportData;
import com.pos.increff.model.form.ReportForm;
import com.pos.increff.pojo.ClientPojo;
import com.pos.increff.pojo.OrderItemsPojo;
import com.pos.increff.pojo.OrdersPojo;
import com.pos.increff.pojo.ProductPojo;
import com.pos.increff.api.OrdersApi;
import com.pos.increff.api.OrderItemsApi;
import com.pos.increff.api.ProductApi;
import com.pos.increff.api.ClientApi;
import com.pos.increff.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReportFlow {

    @Autowired
    private OrdersApi ordersApi;

    @Autowired
    private OrderItemsApi orderItemsApi;

    @Autowired
    private ProductApi productApi;

    @Autowired
    private ClientApi clientApi;

    //TODO: Clean up this function and split them into smaller functions
    //Find a way to filter all the orders acc to report form
    public List<ReportData> getReport(ReportForm form) throws ApiException {
        List<OrdersPojo> orders = ordersApi.getAll(1, 10);  // Get all orders first
        List<ReportData> reportList = new ArrayList<>();
        
        for (OrdersPojo order : orders) {
            // Skip if order date is outside the requested range
            if (!isDateInRange(DateTimeUtils.toDate(order.getUpdatedDate()), form.getFromDate(), form.getToDate())) {
                continue;
            }
            
            // Get order items for this order
            List<OrderItemsPojo> orderItems = orderItemsApi.getByOrderId(order.getId());
            
            for (OrderItemsPojo item : orderItems) {
                ReportData report = new ReportData();
                
                // Get product details
                ProductPojo product = productApi.get(item.getProductId());
                // Get client details
                ClientPojo client = clientApi.get(product.getClientId());
                
                // Set report data
                report.setProductName(product.getName());
                report.setClientName(client.getName());
                report.setBarcode(product.getBarcode());
                report.setQuantity(item.getQuantity());
                report.setPrice(item.getTotalPrice()*item.getQuantity());
                
                // Apply other filters
                if (shouldIncludeInReport(report, form)) {
                    reportList.add(report);
                }
            }
        }
        
        return reportList;
    }

    public void deleteAll() throws ApiException {
        ordersApi.deleteAll();
        orderItemsApi.deleteAll();
        productApi.deleteAll();
        clientApi.deleteAll();
    }

    private boolean isDateInRange(Date orderDate, Date fromDate, Date toDate) {
        // If no dates specified, include all orders
        if (fromDate == null && toDate == null) {
            return true;
        }

        // Create calendar instances
        Calendar orderCal = Calendar.getInstance();
        Calendar fromCal = Calendar.getInstance();
        Calendar toCal = Calendar.getInstance();

        // Set the dates and strip time components
        orderCal.setTime(orderDate);
        stripTime(orderCal);

        // Convert to comparable values (days since epoch)
        long orderDays = orderCal.getTimeInMillis() / (24 * 60 * 60 * 1000);
        
        long fromDays = Long.MIN_VALUE;
        if (fromDate != null) {
            fromCal.setTime(fromDate);
            stripTime(fromCal);
            fromDays = fromCal.getTimeInMillis() / (24 * 60 * 60 * 1000);
        }
        
        long toDays = Long.MAX_VALUE;
        if (toDate != null) {
            toCal.setTime(toDate);
            stripTime(toCal);
            toDays = toCal.getTimeInMillis() / (24 * 60 * 60 * 1000);
        }

        return orderDays >= fromDays && orderDays <= toDays;
    }

    private void stripTime(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

    //Instead of this have a join query in dao layer to filter the orders
    private boolean shouldIncludeInReport(ReportData report, ReportForm form) {
        // Product name filter
        if (form.getProductName() != null && !form.getProductName().isEmpty() && 
            !report.getProductName().toLowerCase().contains(form.getProductName().toLowerCase())) {
            return false;
        }
        
        // Client name filter
        if (form.getClientName() != null && !form.getClientName().isEmpty() && 
            !report.getClientName().toLowerCase().contains(form.getClientName().toLowerCase())) {
            return false;
        }
        
        // Barcode filter
        if (form.getBarcode() != null && !form.getBarcode().isEmpty() && 
            !report.getBarcode().equals(form.getBarcode())) {
            return false;
        }
        
        return true;
    }

}
