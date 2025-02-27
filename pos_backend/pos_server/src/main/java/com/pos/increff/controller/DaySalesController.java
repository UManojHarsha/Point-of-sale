package com.pos.increff.controller;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.pos.increff.api.ApiException;
import com.pos.increff.model.data.DaySalesData;

import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;

import com.pos.increff.dto.DaySalesDto;

@Api
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/orders")

public class DaySalesController {
    @Autowired
    private DaySalesDto dto;

    @ApiOperation(value = "Gets daily sales report")
    @GetMapping("/daily-sales/{date}")
    public ResponseEntity<DaySalesData> getDailySales(@PathVariable Date date) throws ApiException {
        return ResponseEntity.ok(dto.getDailySales(date));
    }

    @ApiOperation(value = "Gets daily sales report for date range")
    @GetMapping("/daily-sales")
    public ResponseEntity<List<DaySalesData>> getDailySalesRange(
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) throws ApiException {
        return ResponseEntity.ok(dto.getDailySalesRange(startDate, endDate));
    }
}
