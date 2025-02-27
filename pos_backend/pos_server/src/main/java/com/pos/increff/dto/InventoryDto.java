package com.pos.increff.dto;

import com.pos.increff.api.ApiException;
import com.pos.increff.api.InventoryApi;
import com.pos.increff.api.ProductApi;
import com.pos.increff.model.data.InventoryData;
import com.pos.increff.model.data.PaginatedData;
import com.pos.increff.model.form.InventoryForm;
import com.pos.increff.pojo.InventoryPojo;
import com.pos.increff.pojo.ProductPojo;
import com.pos.increff.util.ConversionUtil;
import com.pos.increff.util.GenerateTemplateTsvUtil;
import com.pos.increff.flow.InventoryFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class InventoryDto extends AbstractDto {

    @Autowired
    private InventoryApi inventoryApi;

    @Autowired
    private InventoryFlow inventoryFlow;

    @Autowired
    private ProductApi productApi;

    public void uploadInventoryMasters(MultipartFile file) throws ApiException {
        try {
            if (file.isEmpty()) {
                throw new ApiException("File is empty");
            }
            String fileName = file.getOriginalFilename();
            if (fileName == null || !fileName.endsWith(".tsv")) {
                throw new ApiException("Invalid file format. Please upload a .tsv file.");
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
            String firstLine = reader.readLine();
            if (firstLine == null || !firstLine.contains("\t")) {
                throw new ApiException("Invalid TSV format. The file should contain tab-separated values.");
            }
            inventoryApi.processTSVFile(file);
        } catch (IOException e) {
            throw new ApiException("Error reading file: " + e.getMessage());
        }
    }

    public void add(InventoryForm form) throws ApiException {
        checkValid(form);
        InventoryPojo p = ConversionUtil.convert(form);
        inventoryFlow.add(p);
    }

    public InventoryData get(Integer id) throws ApiException {
        if (id == null) {
            throw new ApiException("Inventory ID cannot be null");
        }
        InventoryPojo p = inventoryApi.get(id);
        return ConversionUtil.convert(p);
    }

    public List<InventoryData> getAll() throws ApiException {
        List<InventoryPojo> inventory = inventoryApi.getAll();
        List<InventoryData> list = new ArrayList<InventoryData>();
        for (InventoryPojo p : inventory) {
            list.add(ConversionUtil.convert(p));
        }
        return list;
    }

    public PaginatedData<InventoryData> getAll(int page, int pageSize) throws ApiException {
        List<InventoryPojo> inventory = inventoryApi.getAll(page, pageSize);
        boolean hasNextPage = inventory.size() > pageSize;
        if (hasNextPage) {
            inventory = inventory.subList(0, pageSize);
        }
        List<InventoryData> list = new ArrayList<>();
        for (InventoryPojo p : inventory) {
            list.add(ConversionUtil.convert(p));
        }
        return new PaginatedData<>(list, page, pageSize, hasNextPage);
    }

    public void update(Integer id, InventoryForm form) throws ApiException {
        if (id == null) {
            throw new ApiException("Inventory ID cannot be null");
        }
        checkValid(form);
        InventoryPojo p = ConversionUtil.convert(form);
        inventoryApi.update(id, p);
    }

    public void deleteAll() throws ApiException {
        inventoryApi.deleteAll();
    }

    public InventoryData getByProductId(int product_id) throws ApiException {
        ProductPojo product = productApi.get(product_id);
        InventoryPojo inventory = inventoryApi.getByProductId(product_id);
        return ConversionUtil.convert(inventory);
    }

    public ResponseEntity<Resource> downloadTemplateTsv() {
        List<String> headers = Arrays.asList("barcode","quantity");
        Resource resource = GenerateTemplateTsvUtil.generateTsv(headers);
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=inventory_template.tsv")
            .header("Content-Type", "text/tab-separated-values")
            .body(resource);
    }

    public void updateStock(int productId, int quantity) throws ApiException {
        inventoryApi.updateStock(productId, quantity);
    }
}

