package com.pos.increff.dto;

import com.pos.increff.api.ApiException;
import com.pos.increff.api.ProductApi;
import com.pos.increff.api.ClientApi;
import com.pos.increff.dao.ProductDao;
import com.pos.increff.model.data.ProductData;
import com.pos.increff.model.form.ProductForm;
import com.pos.increff.pojo.ClientPojo;
import com.pos.increff.pojo.ProductPojo;
import com.pos.increff.flow.ProductFlow;
import com.pos.increff.util.ConversionUtil;
import com.pos.increff.util.GenerateTemplateTsvUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import com.pos.increff.model.data.PaginatedData;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class ProductDto extends AbstractDto {

    @Autowired
    private ProductApi productApi;

    @Autowired
    private ProductFlow productFlow;

    @Autowired
    private ClientApi clientApi;

    public void add(List<ProductForm> forms) throws ApiException {
        if(forms == null || forms.isEmpty()) {
            throw new ApiException("Product list cannot be null");
        }
        for (ProductForm form : forms) {
            checkValid(form);
        }
        List<ProductPojo> products = new ArrayList<>();
        for (ProductForm form : forms) {
            products.add(ConversionUtil.convert(form, clientApi));
        }
        productFlow.add(products);
    }

    public void uploadProductMasters(MultipartFile file) throws ApiException {
        if (file.isEmpty()) {
            throw new ApiException("File is empty");
        }
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.endsWith(".tsv")) {
            throw new ApiException("Invalid file format. Please upload a .tsv file.");
        }
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String firstLine = reader.readLine();
            if (firstLine == null || !firstLine.contains("\t")) {
                throw new ApiException("Invalid TSV format. The file should contain tab-separated values.");
            }
            productFlow.processTSVFile(file);
        } catch (IOException e) {
            throw new ApiException("Error reading file: " + e.getMessage());
        }
    }

    public void add(ProductForm form) throws ApiException {
        checkValid(form);
        List<ProductForm> forms = new ArrayList<>();
        forms.add(form);
        add(forms);
    }

    public void delete(Integer id) throws ApiException {
        if (id == null) {
            throw new ApiException("Product ID cannot be null");
        }
        productApi.delete(id);
    }

    public void deleteAll() throws ApiException {
        productApi.deleteAll();
    }

    public ProductData get(Integer id) throws ApiException {
        if (id == null) {
            throw new ApiException("Product ID cannot be null");
        }
        ProductPojo p = productApi.get(id);
        return ConversionUtil.convert(p);
    }

    public PaginatedData<ProductData> getAll(int page, int pageSize) throws ApiException {
        Map.Entry<List<ProductPojo>, List<ClientPojo>> CombinedData = productFlow.getAll(page, pageSize);
        List<ProductPojo> products = CombinedData.getKey();
        List<ClientPojo> clients = CombinedData.getValue();
        boolean hasNextPage = products.size() > pageSize;
        if (hasNextPage) {
            products = products.subList(0, pageSize);
        }
        List<ProductData> productDataList = new ArrayList<>();
        for (int i = 0; i < products.size(); i++) {
            ProductPojo product = products.get(i);
            ClientPojo client = clients.get(i);
            ProductData data = ConversionUtil.convert(product);
            data.setClientName(client.getName());
            data.setClientEmail(client.getEmail());
            productDataList.add(data);
        }
        return new PaginatedData<>(productDataList, page, pageSize, hasNextPage);
    }

    public void update(Integer id, ProductForm form) throws ApiException {
        if (id == null) {
            throw new ApiException("Product ID cannot be null");
        }
        checkValid(form);
        ProductPojo p = ConversionUtil.convert(form, clientApi);
        productApi.update(id, p);
    }

    public PaginatedData<ProductData> searchByName(String name, int page, int pageSize) throws ApiException {
        List<ProductPojo> products = productApi.searchByName(name, page, pageSize);
        boolean hasNextPage = products.size() > pageSize;
        if (hasNextPage) {
            products = products.subList(0, pageSize);
        }
        List<ProductData> productDataList = new ArrayList<>();
        for (ProductPojo product : products) {
            productDataList.add(ConversionUtil.convert(product));
        }
        return new PaginatedData<>(productDataList, page, pageSize, hasNextPage);
    }

    public ResponseEntity<Resource> downloadTemplateTsv() {
        List<String> headers = Arrays.asList("name","barcode","price","client_name");
        Resource resource = GenerateTemplateTsvUtil.generateTsv(headers);
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=product_template.tsv")
            .header("Content-Type", "text/tab-separated-values")
            .body(resource);
    }
}

