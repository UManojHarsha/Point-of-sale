package com.pos.increff.dto;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Calendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.pos.increff.config.AbstractUnitTest;
import com.pos.increff.model.data.ReportData;
import com.pos.increff.model.form.ReportForm;
import com.pos.increff.flow.ReportFlow;
import com.pos.increff.api.ApiException;

public class ReportDtoTest extends AbstractUnitTest {

    @InjectMocks
    private ReportDto reportDto;

    @Mock
    private ReportFlow reportFlow;

    @Test
    public void testGetReportNoFilters() throws ApiException {
        ReportForm form = new ReportForm();
        List<ReportData> report = reportDto.getReport(form);
        
        assertNotNull(report);
        assertEquals(2, report.size());
        verify(reportFlow).getReport(form);
    }

    @Test
    public void testGetReportWithProductNameFilter() throws ApiException {
        ReportForm form = new ReportForm();
        form.setProductName("Test Product 1");
        List<ReportData> report = reportDto.getReport(form);
        
        assertNotNull(report);
        assertEquals(1, report.size());
        assertEquals("test product 1", report.get(0).getProductName());
        verify(reportFlow).getReport(form);
    }

    @Test
    public void testGetReportWithClientNameFilter() throws ApiException {
        ReportForm form = new ReportForm();
        form.setClientName("Test Client");
        List<ReportData> report = reportDto.getReport(form);
        
        assertNotNull(report);
        assertEquals(2, report.size());
        for (ReportData data : report) {
            assertEquals("test client", data.getClientName());
        }
        verify(reportFlow).getReport(form);
    }

    @Test
    public void testGetReportWithBarcodeFilter() throws ApiException {
        ReportForm form = new ReportForm();
        form.setBarcode("BARCODE1");
        List<ReportData> report = reportDto.getReport(form);
        
        assertNotNull(report);
        assertEquals(1, report.size());
        assertEquals("BARCODE1", report.get(0).getBarcode());
        verify(reportFlow).getReport(form);
    }

    @Test
    public void testGetReportWithDateFilter() throws ApiException {
        ReportForm form = new ReportForm();
        form.setFromDate(getDateWithOffset(-1));
        form.setToDate(getDateWithOffset(1));
        
        List<ReportData> report = reportDto.getReport(form);
        assertNotNull(report);
        assertEquals(2, report.size());
        verify(reportFlow).getReport(form);
    }

    @Test
    public void testGetReportWithMultipleFilters() throws ApiException {
        ReportForm form = new ReportForm();
        form.setProductName("Test Product 1");
        form.setClientName("Test Client");
        form.setBarcode("BARCODE1");
        
        List<ReportData> report = reportDto.getReport(form);
        assertNotNull(report);
        assertEquals(1, report.size());
        ReportData data = report.get(0);
        assertEquals("test product 1", data.getProductName());
        assertEquals("test client", data.getClientName());
        assertEquals("BARCODE1", data.getBarcode());
        verify(reportFlow).getReport(form);
    }

    @Test
    public void testGetReportWithDateRangeOutsideOrders() throws ApiException {
        ReportForm form = new ReportForm();
        form.setFromDate(getDateWithOffset(-365));
        form.setToDate(getDateWithOffset(-358));
        
        // Mock behavior for date range outside orders
        when(reportFlow.getReport(argThat(f -> 
            f.getFromDate() != null && 
            f.getToDate() != null && 
            f.getFromDate().before(getDateWithOffset(-300))))).thenReturn(new ArrayList<>());
        
        List<ReportData> report = reportDto.getReport(form);
        assertNotNull(report);
        assertEquals(0, report.size());
        verify(reportFlow).getReport(form);
    }

    @Test
    public void testGetReportWithNonExistentProduct() throws ApiException {
        ReportForm form = new ReportForm();
        form.setProductName("Non-existent Product");
        
        // Mock behavior for non-existent product
        when(reportFlow.getReport(argThat(f -> 
            "Non-existent Product".equals(f.getProductName())))).thenReturn(new ArrayList<>());
        
        List<ReportData> report = reportDto.getReport(form);
        assertNotNull(report);
        assertEquals(0, report.size());
        verify(reportFlow).getReport(form);
    }

    @Test
    public void testGetReportWithNonExistentClient() throws ApiException {
        ReportForm form = new ReportForm();
        form.setClientName("Non-existent Client");
        
        List<ReportData> report = reportDto.getReport(form);
        assertNotNull(report);
        assertEquals(0, report.size());
        verify(reportFlow).getReport(form);
    }

    @Test
    public void testGetReportWithNonExistentBarcode() throws ApiException {
        ReportForm form = new ReportForm();
        form.setBarcode("NON-EXISTENT-BARCODE");
        
        List<ReportData> report = reportDto.getReport(form);
        assertNotNull(report);
        assertEquals(0, report.size());
        verify(reportFlow).getReport(form);
    }

    @Test
    public void testGetReportForSpecificDateRange() throws ApiException {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -5);
        Date fromDate = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 10);
        Date toDate = cal.getTime();

        ReportForm form = new ReportForm();
        form.setFromDate(fromDate);
        form.setToDate(toDate);
        
        List<ReportData> report = reportDto.getReport(form);
        assertNotNull(report);
        assertEquals(2, report.size());
        verify(reportFlow).getReport(form);
    }

    private Date getDateWithOffset(int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    @After
    public void tearDown() {
        reset(reportFlow);
    }

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        setupMockData();
    }

    private void setupMockData() {
        try {
            // Create mock report data
            List<ReportData> mockReportData = new ArrayList<>();
            mockReportData.add(createMockReportData("test product 1", "test client", "BARCODE1", 2));
            mockReportData.add(createMockReportData("test product 2", "test client", "BARCODE2", 1));

            // Setup default behavior for reportFlow
            when(reportFlow.getReport(any(ReportForm.class))).thenReturn(mockReportData);

            // Setup behavior for non-existent client
            when(reportFlow.getReport(argThat(form -> 
                form != null && form.getClientName() != null && 
                "Non-existent Client".equalsIgnoreCase(form.getClientName())))).thenReturn(new ArrayList<>());

            // Setup behavior for non-existent barcode
            when(reportFlow.getReport(argThat(form -> 
                form != null && form.getBarcode() != null && 
                "NON-EXISTENT-BARCODE".equals(form.getBarcode())))).thenReturn(new ArrayList<>());

            // Setup behavior for specific product filter
            when(reportFlow.getReport(argThat(form -> 
                form != null && form.getProductName() != null && 
                "Test Product 1".equalsIgnoreCase(form.getProductName())))).thenReturn(
                    Arrays.asList(createMockReportData("test product 1", "test client", "BARCODE1", 2))
            );

            // Setup behavior for specific barcode filter
            when(reportFlow.getReport(argThat(form -> 
                form != null && form.getBarcode() != null && 
                "BARCODE1".equals(form.getBarcode())))).thenReturn(
                    Arrays.asList(createMockReportData("test product 1", "test client", "BARCODE1", 2))
            );

            // Setup behavior for client filter
            when(reportFlow.getReport(argThat(form -> 
                form != null && form.getClientName() != null && 
                "Test Client".equalsIgnoreCase(form.getClientName())))).thenReturn(mockReportData);

            // Setup behavior for date filter
            when(reportFlow.getReport(argThat(form -> 
                form != null && form.getFromDate() != null && form.getToDate() != null))).thenReturn(mockReportData);

            // Setup behavior for multiple filters
            when(reportFlow.getReport(argThat(form -> 
                form != null && 
                form.getProductName() != null && 
                form.getClientName() != null && 
                form.getBarcode() != null &&
                "Test Product 1".equalsIgnoreCase(form.getProductName()) &&
                "Test Client".equalsIgnoreCase(form.getClientName()) &&
                "BARCODE1".equals(form.getBarcode())))).thenReturn(
                    Arrays.asList(createMockReportData("test product 1", "test client", "BARCODE1", 2))
            );

        } catch (ApiException e) {
            fail("Setup failed: " + e.getMessage());
        }
    }

    private ReportData createMockReportData(String productName, String clientName, String barcode, int quantity) {
        ReportData data = new ReportData();
        data.setProductName(productName);
        data.setClientName(clientName);
        data.setBarcode(barcode);
        data.setQuantity(quantity);
        return data;
    }

}
