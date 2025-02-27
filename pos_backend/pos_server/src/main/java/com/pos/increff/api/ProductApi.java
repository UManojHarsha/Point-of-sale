package com.pos.increff.api;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.pos.increff.dao.ProductDao;
import com.pos.increff.pojo.ProductPojo;
import com.pos.increff.pojo.ClientPojo;
import com.pos.increff.api.ClientApi;

@Service
public class ProductApi {
    @Autowired
    private ProductDao dao;

    @Autowired
    private ClientApi clientService;

    @Transactional
    public void add(List<ProductPojo> products) throws ApiException {
        validateProducts(products);
        dao.insert(products);
    }

    @Transactional
    public void add(ProductPojo product) throws ApiException {
        validateProduct(product);
        // Check for existing product with same name or barcode
        ProductPojo existingName = dao.selectByName(product.getName());
        if (existingName != null) {
            throw new ApiException("Product with name '" + product.getName() + "' already exists");
        }
        ProductPojo existingBarcode = dao.selectByBarcode(product.getBarcode());
        if (existingBarcode != null) {
            throw new ApiException("Product with barcode '" + product.getBarcode() + "' already exists");
        }
        dao.insert(product);
    }

    @Transactional(readOnly = true)
    public ProductPojo get(int id) throws ApiException {
        ProductPojo product = dao.select(id);
        if (product == null) {
            throw new ApiException("Product with ID " + id + " does not exist");
        }
        return product;
    }

    @Transactional(readOnly = true)
    public ProductPojo getByName(String name) throws ApiException {
        if (name == null || name.trim().isEmpty()) {
            throw new ApiException("Product name cannot be empty");
        }
        name = normalize(name);
        ProductPojo product = dao.selectByName(name);
        if (product == null) {
            throw new ApiException("Product with name '" + name + "' does not exist");
        }
        return product;
    }

    @Transactional(readOnly = true)
    public ProductPojo getByBarcode(String barcode) throws ApiException {
        if (barcode == null || barcode.trim().isEmpty()) {
            throw new ApiException("Barcode cannot be empty");
        }
        ProductPojo product = dao.selectByBarcode(barcode);
        if (product == null) {
            throw new ApiException("Product with barcode '" + barcode + "' does not exist");
        }
        return product;
    }

    @Transactional(readOnly = true)
    public List<ProductPojo> getAll() {
        return dao.selectAll();
    }

    @Transactional(readOnly = true)
    public List<ProductPojo> getAll(int page, int pageSize) {
        return dao.selectAll(page, pageSize);
    }

    @Transactional
    public void update(int id, ProductPojo updated) throws ApiException {
        // Validate input
        validateProduct(updated);
        
        // Check if product exists
        ProductPojo existing = get(id);
        
        // Check for name uniqueness (excluding current product)
        ProductPojo nameCheck = dao.selectByName(updated.getName());
        if (nameCheck != null && !nameCheck.getId().equals(id)) {
            throw new ApiException("Another product with name '" + updated.getName() + "' already exists");
        }
        
        // Check for barcode uniqueness (excluding current product)
        ProductPojo barcodeCheck = dao.selectByBarcode(updated.getBarcode());
        if (barcodeCheck != null && !barcodeCheck.getId().equals(id)) {
            throw new ApiException("Another product with barcode '" + updated.getBarcode() + "' already exists");
        }

        dao.updateDetails(id, updated.getName(), updated.getPrice(), updated.getBarcode(), updated.getClientId());
    }

    @Transactional
    public void delete(int id) throws ApiException {
        ProductPojo existing = get(id);
        dao.delete(id);
    }

    @Transactional
    public void deleteAll() {
        dao.deleteAll();
    }

    @Transactional(readOnly = true)
    public List<ProductPojo> searchByName(String name, int page, int pageSize) throws ApiException {
        if (name == null || name.trim().isEmpty()) {
            throw new ApiException("Search name cannot be empty");
        }
        return dao.searchByName(normalize(name), page, pageSize);
    }

    private void validateProducts(List<ProductPojo> products) throws ApiException {
        if (products == null || products.isEmpty()) {
            throw new ApiException("Product list cannot be empty");
        }
        
        for (ProductPojo product : products) {
            validateProduct(product);
            
            // Check for duplicates within the list
            long nameCount = products.stream()
                .filter(p -> p.getName().equalsIgnoreCase(product.getName()))
                .count();
            if (nameCount > 1) {
                throw new ApiException("Duplicate product name '" + product.getName() + "' in the list");
            }
            
            long barcodeCount = products.stream()
                .filter(p -> p.getBarcode().equals(product.getBarcode()))
                .count();
            if (barcodeCount > 1) {
                throw new ApiException("Duplicate barcode '" + product.getBarcode() + "' in the list");
            }
            
            // Check against existing products in database
            ProductPojo existingName = dao.selectByName(product.getName());
            if (existingName != null) {
                throw new ApiException("Product with name '" + product.getName() + "' already exists");
            }
            
            ProductPojo existingBarcode = dao.selectByBarcode(product.getBarcode());
            if (existingBarcode != null) {
                throw new ApiException("Product with barcode '" + product.getBarcode() + "' already exists");
            }
        }
    }

    private void validateProduct(ProductPojo product) throws ApiException {
        if (product == null) {
            throw new ApiException("Product cannot be null");
        }
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new ApiException("Product name cannot be empty");
        }
        if (product.getBarcode() == null || product.getBarcode().trim().isEmpty()) {
            throw new ApiException("Product barcode cannot be empty");
        }
        if (product.getPrice() == null || product.getPrice() <= 0) {
            throw new ApiException("Product price must be greater than zero");
        }
        if (product.getClientId() == null || product.getClientId() <= 0) {
            throw new ApiException("Invalid client ID");
        }
        
        // Normalize the product name
        product.setName(normalize(product.getName()));
    }

    private String normalize(String name) {
        return name.toLowerCase().trim();
    }
}
