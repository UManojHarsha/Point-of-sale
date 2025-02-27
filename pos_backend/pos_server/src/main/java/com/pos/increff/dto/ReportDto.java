package com.pos.increff.dto;

import java.util.List;

import com.pos.increff.api.ApiException;
import com.pos.increff.model.data.ReportData;
import com.pos.increff.model.form.ReportForm;
import com.pos.increff.flow.ReportFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReportDto extends AbstractDto {
    @Autowired
    private ReportFlow reportFlow;

    public List<ReportData> getReport(ReportForm form) throws ApiException {
        checkValid(form);
        return reportFlow.getReport(form);
    }
}
