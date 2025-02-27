package com.pos.increff.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pos.increff.pojo.InventoryPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pos.increff.dao.InventoryDao;
import com.pos.increff.dao.ProductDao;
import com.pos.increff.pojo.ProductPojo;

@Service
public class InventoryApi {
    private static final Logger logger = LoggerFactory.getLogger(InventoryApi.class);
    private static final int MAX_ROWS = 5000;

    @Autowired
    private InventoryDao dao;

    @Autowired
    private ProductDao productDao;

    @Transactional
    public void processTSVFile(MultipartFile file) throws IOException, ApiException {
        List<String> errors = new ArrayList<>();
        List<InventoryPojo> inventoryList = processFileContents(file, errors);

        if (inventoryList.isEmpty()) {
            throw new ApiException("No valid inventory found in file");
        }

        if (!errors.isEmpty()) {
            throw new ApiException("Validation errors:\n" + String.join("\n", errors));
        }

        // Update inventory quantities
        updateInventoryQuantities(inventoryList);
    }

    private List<InventoryPojo> processFileContents(MultipartFile file, List<String> errors) throws IOException, ApiException {
        List<InventoryPojo> inventoryList = new ArrayList<>();
        Set<String> barcodesInFile = new HashSet<>();
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean isFirstLine = true;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (lineNumber > MAX_ROWS) {
                    throw new ApiException("The file contains more than " + MAX_ROWS + " rows. Only a maximum of " + MAX_ROWS + " entries are allowed per file.");
                }
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                processLine(line, lineNumber, inventoryList, errors, barcodesInFile);
            }
        }
        return inventoryList;
    }

    private void processLine(String line, int lineNumber, List<InventoryPojo> inventoryList, 
                           List<String> errors, Set<String> barcodesInFile) throws ApiException {
        String[] data = line.split("\t");

        if (!isValidDataLength(data, lineNumber, errors)) {
            return;
        }

        String barcode = data[0].trim();
        String quantityStr = data[1].trim();

        if (!isValidDataValues(barcode, quantityStr, lineNumber, errors)) {
            return;
        }

        if (!isUniqueInFile(barcode, barcodesInFile, lineNumber, errors)) {
            return;
        }

        try {
            InventoryPojo inventory = createInventory(barcode, quantityStr, lineNumber, errors);
            if (inventory != null) {
                inventoryList.add(inventory);
            }
        } catch (NumberFormatException e) {
            errors.add("Line " + lineNumber + ": Invalid number format for quantity");
        }
    }

    private boolean isValidDataLength(String[] data, int lineNumber, List<String> errors) {
        if (data.length < 2) {
            errors.add("Line " + lineNumber + ": Incomplete data. Expected barcode, quantity");
            return false;
        }
        return true;
    }

    private boolean isValidDataValues(String barcode, String quantityStr, int lineNumber, List<String> errors) {
        if (barcode.isEmpty() || quantityStr.isEmpty()) {
            errors.add("Line " + lineNumber + ": Empty values found");
            return false;
        }
        return true;
    }

    private boolean isUniqueInFile(String barcode, Set<String> barcodesInFile, int lineNumber, List<String> errors) {
        if (!barcodesInFile.add(barcode)) {
            errors.add("Line " + lineNumber + ": Duplicate barcode '" + barcode + "' found in file");
            return false;
        }
        return true;
    }

    private InventoryPojo createInventory(String barcode, String quantityStr, int lineNumber, List<String> errors) throws ApiException {
        int quantity = Integer.parseInt(quantityStr);

        if (quantity <= 0) {
            errors.add("Line " + lineNumber + ": Invalid quantity. Quantity must be greater than 0");
            return null;
        }

        // Validate product exists and has inventory
        ProductPojo product = productDao.selectByBarcode(barcode);
        if (product == null) {
            errors.add("Line " + lineNumber + ": Product with barcode " + barcode + " does not exist in the inventory");
            return null;
        }

        InventoryPojo existingInventory = dao.selectByProductId(product.getId());
        if (existingInventory == null) {
            errors.add("Line " + lineNumber + ": Product with barcode " + barcode + " is present but is yet to be added to the inventory");
            return null;
        }

        InventoryPojo inventory = new InventoryPojo();
        inventory.setProductId(product.getId());
        inventory.setTotalQuantity(quantity);
        return inventory;
    }

    private void updateInventoryQuantities(List<InventoryPojo> inventoryList) throws ApiException {
        for (InventoryPojo inventory : inventoryList) {
            dao.updateDetails(inventory.getProductId(), inventory.getTotalQuantity());
        }
    }

    @Transactional
    public void add(List<InventoryPojo> products) throws ApiException {
        dao.insert(products) ;
    }

    @Transactional
    public void add(InventoryPojo product) throws ApiException {
        dao.insert(product) ;
    }

    @Transactional(readOnly = true)
    public InventoryPojo get(int id) throws ApiException {
        InventoryPojo client = dao.select(id);
        if (client == null) {
            throw new ApiException("Product with given ID does not exist");
        }
        return client;
    }

    @Transactional(readOnly = true)
    public List<InventoryPojo> getAll() {
        return dao.selectAll();
    }

    @Transactional(readOnly = true)
    public List<InventoryPojo> getAll(int page, int pageSize) {
        return dao.selectAll(page, pageSize);
    }

    @Transactional
    public void update(int product_id, InventoryPojo updated) throws ApiException {
        InventoryPojo product = dao.selectByProductId(product_id);
        if (product == null) {
            throw new ApiException("Product with given ID does not exist");
        }
        dao.updateDetails(product_id , updated.getTotalQuantity());
    }
 
    @Transactional
    public void updateStock(int product_id , int quantity) throws ApiException{
        dao.updateStock(product_id , quantity);
    }

    @Transactional(readOnly = true)
    public int getStock(int product_id) throws ApiException{
        return dao.selectByProductId(product_id).getTotalQuantity();
    }

    @Transactional(readOnly = true)
    public void logCurrentStock(int product_id) {
        InventoryPojo inventory = dao.select(product_id);
        if (inventory != null) {
            logger.info("Current stock for product {}: {}", product_id, inventory.getTotalQuantity());
        } else {
            logger.warn("No inventory found for product {}", product_id);
        }
    }

    @Transactional
    public void deleteAll() {
        dao.deleteAll();
    }

    @Transactional(readOnly = true)
    public InventoryPojo getByProductId(int product_id) throws ApiException {
        InventoryPojo inventory = dao.selectByProductId(product_id);
        if (inventory == null) {
            throw new ApiException("No inventory found for product ID: " + product_id);
        }
        return inventory;
    }

    public ProductPojo getProduct(int productId) throws ApiException {
        ProductPojo product = productDao.select(productId);
        if (product == null) {
            throw new ApiException("Product not found with ID: " + productId);
        }
        return product;
    }
}

