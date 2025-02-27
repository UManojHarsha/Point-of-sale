package com.pos.increff.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import com.pos.increff.api.ApiException;
import com.pos.increff.dto.ProductDto;
import com.pos.increff.model.form.ProductForm;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductDto dto;

    @ApiOperation(value = "Adds a list of Products")
    @RequestMapping(path = "/add", method = RequestMethod.POST)
    public ResponseEntity<?> add(@RequestBody List<ProductForm> forms) throws ApiException {
        dto.add(forms);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Implements uploading products masters from tsv file")
    @RequestMapping(path = "/uploadProductMasters", method = RequestMethod.POST)
    public ResponseEntity<?> uploadProductMasters(@RequestParam("file") MultipartFile file) throws ApiException {
        dto.uploadProductMasters(file);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Gets a product by ID")
    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> get(@PathVariable int id) throws ApiException {
        return ResponseEntity.ok(dto.get(id));
    }
    
    @ApiOperation(value = "Gets list of all products")
    @RequestMapping(path = "/", method = RequestMethod.GET)
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) throws ApiException {
        return ResponseEntity.ok(dto.getAll(page, pageSize));
    }

    @ApiOperation(value = "Updates a product")
    @RequestMapping(path = "/update/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody ProductForm form) throws ApiException {
        dto.update(id, form);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Deletes a product")
    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable int id) throws ApiException {
        dto.delete(id);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Search products by name")
    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public ResponseEntity<?> searchByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) throws ApiException {
        return ResponseEntity.ok(dto.searchByName(name, page, pageSize));
    }

    @ApiOperation(value = "Download template tsv")
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET, value = "/template")
    public ResponseEntity<Resource> downloadTemplateTsv() {
        return dto.downloadTemplateTsv();
    }
}
