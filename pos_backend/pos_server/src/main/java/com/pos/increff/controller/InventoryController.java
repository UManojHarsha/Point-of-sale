package com.pos.increff.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.pos.increff.api.ApiException;
import com.pos.increff.dto.InventoryDto;
import com.pos.increff.model.form.InventoryForm;
import com.pos.increff.model.data.ErrorResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
@RequestMapping("/inventory")

public class InventoryController {

    @Autowired
    private InventoryDto dto;

    @ApiOperation(value = "Implements uploading inventory masters from tsv file")
    @RequestMapping(path = "/uploadInventoryMasters", method = RequestMethod.POST)
    public ResponseEntity<?> uploadProductMasters(@RequestParam("file") MultipartFile file) throws ApiException {
        dto.uploadInventoryMasters(file);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Adds a list of Products to the inventory")
    @RequestMapping(path = "/add", method = RequestMethod.POST)
    public ResponseEntity<?> add(@RequestBody InventoryForm form) throws ApiException {
        dto.add(form);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Gets inventory by ID")
    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> get(@PathVariable int id) throws ApiException {
        return ResponseEntity.ok(dto.get(id));
    }

    @ApiOperation(value = "Gets list of all inventory")
    @RequestMapping(path = "", method = RequestMethod.GET)
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) throws ApiException {
        return ResponseEntity.ok(dto.getAll(page, pageSize));
    }

    @ApiOperation(value = "Updates inventory")
    @RequestMapping(path = "/update/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody InventoryForm form) throws ApiException {
        dto.update(id, form);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Download template tsv")
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET, value = "/template")
    public ResponseEntity<Resource> downloadTemplateTsv() {
        return dto.downloadTemplateTsv();
    }

}

