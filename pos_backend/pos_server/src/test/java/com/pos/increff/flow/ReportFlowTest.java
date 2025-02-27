package com.pos.increff.flow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import org.springframework.beans.factory.annotation.Autowired;

import com.pos.increff.api.ApiException;
import com.pos.increff.api.ClientApi;
import com.pos.increff.api.OrderItemsApi;
import com.pos.increff.api.OrdersApi;
import com.pos.increff.api.ProductApi;
import com.pos.increff.model.data.ReportData;
import com.pos.increff.model.form.ReportForm;
import com.pos.increff.pojo.ClientPojo;
import com.pos.increff.pojo.OrderItemsPojo;
import com.pos.increff.pojo.OrdersPojo;
import com.pos.increff.pojo.ProductPojo;
import com.pos.increff.config.AbstractUnitTest;
import com.pos.commons.OrderStatus;

public class ReportFlowTest extends AbstractUnitTest {

    @Autowired
    private ReportFlow reportFlow;
    @Autowired
    private OrdersApi ordersApi;
    @Autowired
    private OrderItemsApi orderItemsApi;
    @Autowired
    private ProductApi productApi;
    @Autowired
    private ClientApi clientApi;

    private ClientPojo client;
    private ProductPojo product;
    private OrdersPojo order;
    private OrderItemsPojo orderItem;

    @Test
    public void testGetReportNoFilters() throws ApiException {
        List<ReportData> reports = reportFlow.getReport(new ReportForm());
        assertEquals(1, reports.size());
        assertReportData(reports.get(0), "test product", "test client", "TEST123", 5);
    }

    @Test
    public void testGetReportWithDateFilter() throws ApiException {
        ReportForm form = new ReportForm();
        form.setFromDate(getDateWithOffset(-1));
        form.setToDate(getDateWithOffset(1));
        assertEquals(1, reportFlow.getReport(form).size());
    }

    @Test
    public void testGetReportWithProductNameFilter() throws ApiException {
        testReportFilter("Test Product", "Non Existent Product", true , false);
    }

    @Test
    public void testGetReportWithClientNameFilter() throws ApiException {
        testReportFilter("Test Client", "Non Existent Client", false , false);
    }

    @Test
    public void testGetReportWithBarcodeFilter() throws ApiException {
        testReportFilter("TEST123", "NONEXISTENT123", false , true);
    }

    @Test
    public void testGetReportWithDateRangeOutsideOrder() throws ApiException {
        ReportForm form = new ReportForm();
        form.setFromDate(getDateWithOffset(-365));
        form.setToDate(getDateWithOffset(-358));
        assertTrue(reportFlow.getReport(form).isEmpty());
    }

    @Test
    public void testGetReportWithMultipleFilters() throws ApiException {
        ReportForm form = new ReportForm();
        form.setProductName("Test Product");
        form.setClientName("Test Client");
        form.setBarcode("TEST123");
        form.setFromDate(getDateWithOffset(-1));
        form.setToDate(getDateWithOffset(1));
        assertEquals(1, reportFlow.getReport(form).size());

        form.setBarcode("NONEXISTENT123");
        assertTrue(reportFlow.getReport(form).isEmpty());
    }

    @After
    public void tearDown() throws ApiException {
        // Clean up all test data
        reportFlow.deleteAll();
        orderItemsApi.deleteAll();
        ordersApi.deleteAll();
        productApi.deleteAll();
        clientApi.deleteAll();
    }

    @Before
    public void setUp() throws ApiException {
        // Clean up all test data first
        orderItemsApi.deleteAll();
        ordersApi.deleteAll();
        productApi.deleteAll();
        clientApi.deleteAll();
        
        // Now create fresh test data
        createTestData();
    }

    private void testReportFilter(String validValue, String invalidValue, boolean isProduct , boolean isBarcode) throws ApiException {
        ReportForm form = new ReportForm();
        if (isProduct) {
            form.setProductName(validValue);
        } 
        else if (isBarcode) {
            form.setBarcode(validValue);
        } 
        else {
            form.setClientName(validValue);
        }
        assertEquals(1, reportFlow.getReport(form).size());

        if (isProduct) {
            form.setProductName(invalidValue);
        } 
        else if(isBarcode){
            form.setBarcode(invalidValue);
        }
        else {
            form.setClientName(invalidValue);
        }
        assertTrue(reportFlow.getReport(form).isEmpty());
    }

    private void assertReportData(ReportData report, String productName, String clientName, String barcode, int quantity) {
        assertEquals(productName, report.getProductName());
        assertEquals(clientName, report.getClientName());
        assertEquals(barcode, report.getBarcode());
        assertEquals(quantity, report.getQuantity());
    }

    private Date getDateWithOffset(int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }


    private void createTestData() throws ApiException {
        client = createClient("Test Client" , "clientTest@test.com" , "1234567890");
        product = createProduct("Test Product", "TEST123", client.getId(), 100.0);
        order = createOrder();
        orderItem = createOrderItem(order.getId(), product.getId(), 5, 80.0);
        addOrderItems(Arrays.asList(orderItem));
    }

    private ClientPojo createClient(String name, String email, String contactNo) throws ApiException {
        ClientPojo client = new ClientPojo();
        client.setName(name);
        client.setEmail(email);
        client.setContactNo(contactNo);
        clientApi.add(client);
        return client;
    }

    private ProductPojo createProduct(String name, String barcode, int clientId, Double price) throws ApiException {
        ProductPojo product = new ProductPojo();
        product.setName(name);
        product.setBarcode(barcode);
        product.setClientId(clientId);
        product.setPrice(price);
        productApi.add(product);
        return product;
    }

    private OrdersPojo createOrder() throws ApiException {
        OrdersPojo order = new OrdersPojo();
        order.setUserEmail("test@test.com");
        order.setTotalPrice(100.0);
        order.setStatus(OrderStatus.PENDING_INVOICE);
        order.setInvoicePath(null);
        ordersApi.add(order);
        return order;
    }

    private OrderItemsPojo createOrderItem(int orderId, int productId, int quantity, double price) {
        OrderItemsPojo orderItem = new OrderItemsPojo();
        orderItem.setOrderId(orderId);
        orderItem.setProductId(productId);
        orderItem.setQuantity(quantity);
        orderItem.setTotalPrice(price);
        return orderItem;
    }

    private void addOrderItems(List<OrderItemsPojo> orderItems) throws ApiException {
        orderItemsApi.add(orderItems);
    }
}
