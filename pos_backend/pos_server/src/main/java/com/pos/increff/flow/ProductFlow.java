package com.pos.increff.flow;

import com.pos.increff.api.ProductApi;
import com.pos.increff.pojo.ProductPojo;
import com.pos.increff.pojo.InventoryPojo;
import com.pos.increff.api.InventoryApi;
import com.pos.increff.api.ApiException;
import com.pos.increff.api.ClientApi;
import com.pos.increff.pojo.ClientPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ProductFlow {
    @Autowired
    private ProductApi productApi;

    @Autowired
    private InventoryApi inventoryApi;

    @Autowired
    private ClientApi clientApi;

    private static final int MAX_ROWS = 5000;

    public void add(List<ProductPojo> products) throws ApiException {
        productApi.add(products);
        List<InventoryPojo> inventories = new ArrayList<>();
        for (ProductPojo product : products) {
            InventoryPojo inventory = new InventoryPojo();
            inventory.setProductId(product.getId());
            inventory.setTotalQuantity(0);
            inventories.add(inventory);
        }
        inventoryApi.add(inventories);
    }

    public Map.Entry<List<ProductPojo>, List<ClientPojo>> getAll(int page, int pageSize) throws ApiException {
        List<ProductPojo> products = productApi.getAll(page, pageSize);
        List<ClientPojo> clients = new ArrayList<>();
        
        for(ProductPojo product : products){
            try{
                ClientPojo client = clientApi.get(product.getClientId());
                clients.add(client);
            }
            catch(ApiException e){
                throw new ApiException("Client with given ID does not exist");
            }
        }
        return new AbstractMap.SimpleEntry<>(products, clients);
    }

    public void processTSVFile(MultipartFile file) throws IOException, ApiException {
        List<String> errors = new ArrayList<>();
        List<ProductPojo> productList = processFileContents(file, errors);

        if (productList.isEmpty()) {
            throw new ApiException("No valid products found in file");
        }

        if (!errors.isEmpty()) {
            throw new ApiException("Validation errors:\n" + String.join("\n", errors));
        }

        addProductsWithInventory(productList);
    }

    private List<ProductPojo> processFileContents(MultipartFile file, List<String> errors) throws IOException, ApiException {
        List<ProductPojo> productList = new ArrayList<>();
        Set<String> namesInFile = new HashSet<>();
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

                processLine(line, lineNumber, productList, errors, namesInFile, barcodesInFile);
            }
        }
        return productList;
    }

    private void processLine(String line, int lineNumber, List<ProductPojo> productList, List<String> errors, 
                           Set<String> namesInFile, Set<String> barcodesInFile) throws ApiException {
        String[] data = line.split("\t");

        if (!isValidDataLength(data, lineNumber, errors)) {
            return;
        }

        String name = data[0].trim();
        String barcode = data[1].trim();
        String priceStr = data[2].trim();
        String clientName = data[3].trim();

        if (!isValidDataValues(name, barcode, priceStr, clientName, lineNumber, errors)) {
            return;
        }

        if (!isUniqueInFile(name, barcode, namesInFile, barcodesInFile, lineNumber, errors)) {
            return;
        }

        try {
            ProductPojo product = createProduct(name, barcode, priceStr, clientName, lineNumber, errors);
            if (product != null) {
                productList.add(product);
            }
        } catch (NumberFormatException e) {
            errors.add("Line " + lineNumber + ": Invalid number format for price");
        }
    }

    private boolean isValidDataLength(String[] data, int lineNumber, List<String> errors) {
        if (data.length < 4) {
            errors.add("Line " + lineNumber + ": Incomplete data. Expected name, barcode, price, and clientId.");
            return false;
        }
        return true;
    }

    private boolean isValidDataValues(String name, String barcode, String priceStr, String clientName, 
                                    int lineNumber, List<String> errors) {
        if (name.isEmpty() || barcode.isEmpty() || priceStr.isEmpty() || clientName.isEmpty()) {
            errors.add("Line " + lineNumber + ": Empty values found");
            return false;
        }
        return true;
    }

    private boolean isUniqueInFile(String name, String barcode, Set<String> namesInFile, 
                                 Set<String> barcodesInFile, int lineNumber, List<String> errors) {
        if (!namesInFile.add(name.toLowerCase())) {
            errors.add("Line " + lineNumber + ": Duplicate product name '" + name + "' found in file");
            return false;
        }
        if (!barcodesInFile.add(barcode)) {
            errors.add("Line " + lineNumber + ": Duplicate barcode '" + barcode + "' found in file");
            return false;
        }
        return true;
    }

    private ProductPojo createProduct(String name, String barcode, String priceStr, String clientName, 
                                    int lineNumber, List<String> errors) throws ApiException {
        double price = Double.parseDouble(priceStr);

        if (price <= 0) {
            errors.add("Line " + lineNumber + ": Invalid price. Price must be greater than 0");
            return null;
        }

        ClientPojo clientPojo;
        try {
            clientPojo = clientApi.getByName(clientName);
        } catch (ApiException e) {
            errors.add("Line " + lineNumber + ": Client with name '" + clientName + "' does not exist");
            return null;
        }

        try {
            ProductPojo existingByName = productApi.getByName(name);
            if (existingByName != null && existingByName.getClientId().equals(clientPojo.getId())) {
                errors.add("Line " + lineNumber + ": Product with name '" + name + "' and clientId '" + clientPojo.getId() + "' already exists for a different client");
                return null;
            }
        } catch (ApiException e) {
        }

        try {
            ProductPojo existingByBarcode = productApi.getByBarcode(barcode);
            if (existingByBarcode != null) {
                errors.add("Line " + lineNumber + ": Product with barcode '" + barcode + "' already exists in database");
                return null;
            }
        } catch (ApiException e) {
        }

        ProductPojo product = new ProductPojo();
        product.setName(name);
        product.setBarcode(barcode);
        product.setPrice(price);
        product.setClientId(clientPojo.getId());
        return product;
    }

    private void addProductsWithInventory(List<ProductPojo> productList) throws ApiException {
        productApi.add(productList);
    
        List<InventoryPojo> inventoryList = productList.stream()
            .map(product -> {
                InventoryPojo inventory = new InventoryPojo();
                inventory.setProductId(product.getId());
                inventory.setTotalQuantity(0);
                return inventory;
            })
            .collect(Collectors.toList());
        
        inventoryApi.add(inventoryList);
    }
}
