package com.pos.increff.dto;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.pos.increff.api.ApiException;
import com.pos.increff.model.data.DaySalesData;
import com.pos.increff.pojo.DaySalesPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.pos.increff.api.DaySalesApi;
import com.pos.increff.util.ConversionUtil;


@Component
public class DaySalesDto extends AbstractDto {
    @Autowired
    private DaySalesApi daySalesApi;

    @Autowired
    private ConversionUtil conversionUtil;

    //TODO: Check if required and remove it
    public void add(DaySalesData data) throws ApiException {
        DaySalesPojo pojo = new DaySalesPojo();
        pojo.setDate(data.getDate());
        pojo.setOrderCount(data.getOrderCount());
        pojo.setProductCount(data.getProductCount());
        pojo.setRevenue(data.getRevenue());
        daySalesApi.insert(pojo);
    }

    public DaySalesData getDailySales(Date date) throws ApiException {
        DaySalesPojo pojo = daySalesApi.getByDate(date);
        if (pojo == null) {
            throw new ApiException("No sales data found for date: " + date);
        }
        return conversionUtil.convertDaySales(pojo);
    }

    public List<DaySalesData> getDailySalesRange(Date startDate, Date endDate) throws ApiException {
        List<DaySalesPojo> pojos = daySalesApi.getByDateRange(startDate, endDate);
        return pojos.stream()
            .map(pojo -> conversionUtil.convertDaySales(pojo))
            .collect(Collectors.toList());
    }
}
