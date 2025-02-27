package com.pos.invoice.util;

import java.io.*;
import java.util.List;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.springframework.stereotype.Component;

import com.pos.commons.OrderItemsData;
import com.pos.commons.OrdersData;
import com.pos.commons.api.ApiException;

@Component
public class InvoiceUtil {

    private static final String XSL_PATH = "invoice.xsl";
    private static final String CONFIG_PATH = "fop-config.xml";

    public byte[] generateInvoice(OrdersData order, List<OrderItemsData> items) throws ApiException {
        try {
            // Create XML data
            String xmlData = createXMLData(order, items);

            // Get XSL file from resources
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream xslFile = classLoader.getResourceAsStream(XSL_PATH);
            InputStream configFile = classLoader.getResourceAsStream(CONFIG_PATH);

            if (xslFile == null || configFile == null) {
                throw new ApiException("Required template files not found");
            }

            // Setup FOP
            FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
            FOUserAgent foUserAgent = fopFactory.newFOUserAgent();

            // Setup output
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            // Setup FOP with output format
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);

            // Setup Transformer
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(new StreamSource(xslFile));

            // Create source from XML string
            Source src = new StreamSource(new StringReader(xmlData));
            Result res = new SAXResult(fop.getDefaultHandler());

            // Transform
            transformer.transform(src, res);

            // Return the raw PDF bytes
            return out.toByteArray();

        } catch (Exception e) {
            throw new ApiException("Error generating invoice: " + e.getMessage(), e);
        }
    }

    private String createXMLData(OrdersData order, List<OrderItemsData> items) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<invoice>");
        
        // Add order details
        xml.append("<orderId>").append(order.getId()).append("</orderId>");
        xml.append("<date>").append(order.getUpdatedDate()).append("</date>");
        xml.append("<customerEmail>").append(order.getUserEmail()).append("</customerEmail>");
        
        // Add items
        xml.append("<items>");
        for (OrderItemsData item : items) {
            xml.append("<item>");
            xml.append("<productName>").append(item.getProductName()).append("</productName>");
            xml.append("<quantity>").append(item.getQuantity()).append("</quantity>");
            xml.append("<total>").append(item.getPrice()).append("</total>");
            xml.append("</item>");
        }
        xml.append("</items>");
        
        // Add total amount
        xml.append("<totalAmount>").append(order.getTotalPrice()).append("</totalAmount>");
        
        xml.append("</invoice>");
        return xml.toString();
    }
} 