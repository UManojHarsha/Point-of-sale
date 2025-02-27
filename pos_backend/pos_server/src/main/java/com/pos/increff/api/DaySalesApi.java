package com.pos.increff.api;

import com.pos.increff.dao.DaySalesDao;
import com.pos.increff.pojo.DaySalesPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class DaySalesApi {
    
    @Autowired
    private DaySalesDao dao;

    @Transactional
    public void insert(DaySalesPojo daySales) {
        dao.insert(daySales);
    }

    @Transactional
    public void update(DaySalesPojo daySales) {
        dao.update(daySales);
    }

    @Transactional(readOnly = true)
    public DaySalesPojo getByDate(Date date) {
        return dao.selectByDate(date);
    }

    @Transactional(readOnly = true)
    public List<DaySalesPojo> getAll() {
        return dao.selectAll();
    }

    @Transactional(readOnly = true)
    public List<DaySalesPojo> getByDateRange(Date startDate, Date endDate) {
        return dao.selectByDateRange(startDate, endDate);
    }
} 