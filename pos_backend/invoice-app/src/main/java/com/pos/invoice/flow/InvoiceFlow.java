package com.pos.invoice.flow;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import com.pos.commons.OrderStatus;
import com.pos.commons.api.ApiException;
import com.pos.invoice.model.InvoiceDetails;
import com.pos.invoice.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("invoiceAppFlow")
public class InvoiceFlow {
    @Value("${invoice.storage.path}")
    private String invoiceStoragePath;

    @Autowired
    private InvoiceService invoiceService;

 //ToDO: Catch specific exceptions
    public String generateInvoice(InvoiceDetails details) throws ApiException {
        try {
            // Generate PDF bytes
            byte[] pdfBytes = invoiceService.generateInvoiceBytes(details);
            
            Path storagePath = Paths.get(invoiceStoragePath);
            Files.createDirectories(storagePath);
        
            String filename = String.format("invoice_order_%d.pdf", details.getOrderId());
            
            Path filePath = storagePath.resolve(filename);
            
            Files.write(filePath, pdfBytes);
            
            return filePath.toAbsolutePath().toString();
        } catch (Exception e) {
            throw new ApiException("Failed to generate invoice: " + e.getMessage(), e);
        }
    }
}
