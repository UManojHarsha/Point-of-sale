package com.pos.increff.controller;

import java.util.List;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.pos.increff.api.ApiException;
import com.pos.increff.model.data.ReportData;
import com.pos.increff.model.form.ReportForm;
import com.pos.increff.dto.ReportDto;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;

@Api
@RestController
@RequestMapping("/orders")
public class ReportController {
    @Autowired
    private ReportDto dto;

    @ApiOperation(value = "Gets filtered report data")
    @RequestMapping(path = "/report", method = RequestMethod.POST)
    public ResponseEntity<List<ReportData>> getReport(@RequestBody ReportForm form) throws ApiException {
        return ResponseEntity.ok(dto.getReport(form));
    }
}
