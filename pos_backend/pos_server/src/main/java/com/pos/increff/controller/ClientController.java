package com.pos.increff.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.pos.increff.model.form.ClientForm;
import com.pos.increff.api.ApiException;
import com.pos.increff.dto.ClientDto;
import com.pos.increff.model.data.ErrorResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
@RequestMapping("/client")

public class ClientController {

    @Autowired
    private ClientDto dto;

    @ApiOperation(value = "Adds a client")
    @RequestMapping(path = "/add", method = RequestMethod.POST)
    public ResponseEntity<?> add(@RequestBody ClientForm form) throws ApiException {
        dto.add(form);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Gets a client by ID")
    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> get(@PathVariable int id) throws ApiException {
        return ResponseEntity.ok(dto.get(id));
    }

    @ApiOperation(value = "Gets list of all clients")
    @RequestMapping(path = "/", method = RequestMethod.GET)
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) throws ApiException {
        return ResponseEntity.ok(dto.getAll(page, pageSize));
    }

    @ApiOperation(value = "Updates a client")
    @RequestMapping(path = "/update/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody ClientForm form) throws ApiException {
        dto.update(id, form);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Deletes a client")
    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable int id) throws ApiException {
        dto.delete(id);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Search clients by name")
    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public ResponseEntity<?> searchByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) throws ApiException {
        return ResponseEntity.ok(dto.searchByName(name, page, pageSize));
    }
} 