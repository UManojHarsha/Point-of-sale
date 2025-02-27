package com.pos.increff.api;

import com.pos.increff.pojo.OrdersPojo;
import com.pos.increff.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pos.increff.dao.OrdersDao;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Date;
import java.time.ZonedDateTime;

@Service
public class OrdersApi{
    @Autowired
    private OrdersDao dao;

    @Transactional
    public int add(OrdersPojo order) throws ApiException {
        validateOrder(order);
        return dao.insert(order);
    }

    @Transactional(readOnly = true)
    public List<OrdersPojo> getAll(int page, int pageSize) throws ApiException {
        return dao.selectAll(page, pageSize);
    }

    //TODO: HAndle this persistance error in dao layer
    @Transactional(readOnly = true)
    public OrdersPojo get(int id) throws ApiException {
        try {
            OrdersPojo order = dao.select(id);
            if (order == null) {
                throw new ApiException("Order with ID " + id + " not found");
            }
            return order;
        } catch (javax.persistence.NoResultException e) {
            throw new ApiException("Order with ID " + id + " not found");
        }
    }

    @Transactional(readOnly = true)
    public List<OrdersPojo> getOrdersByDate(Date date) throws ApiException {
        if (date == null) {
            throw new ApiException("Date cannot be null");
        }
        ZonedDateTime zonedDate = DateTimeUtils.fromDate(date);
        Date startOfDay = DateTimeUtils.toDate(DateTimeUtils.getStartOfDay(zonedDate));
        Date endOfDay = DateTimeUtils.toDate(DateTimeUtils.getEndOfDay(zonedDate));
        return dao.selectByDateRange(startOfDay, endOfDay);
    }

    @Transactional
    public void deleteAll() {
        dao.deleteAll();
    }

    @Transactional(rollbackFor = ApiException.class)
    public void update(OrdersPojo order) throws ApiException {
        validateOrder(order);
        OrdersPojo existing = dao.select(order.getId());
        if (existing == null) {
            throw new ApiException("Order not found: " + order.getId());
        }
        
        // Update status and invoice path
        existing.setStatus(order.getStatus());
        existing.setInvoicePath(order.getInvoicePath());
        
        //dao.update(existing);
        
    }

    private void validateOrder(OrdersPojo order) throws ApiException {
        if (order == null) {
            throw new ApiException("Order cannot be null");
        }
        if (order.getUserEmail() == null || order.getUserEmail().trim().isEmpty()) {
            throw new ApiException("User email cannot be empty");
        }
        if (order.getTotalPrice() == null || order.getTotalPrice() < 0) {
            throw new ApiException("Total price must be non-negative");
        }
        if (order.getStatus() == null) {
            throw new ApiException("Order status cannot be null");
        }
    }
}
