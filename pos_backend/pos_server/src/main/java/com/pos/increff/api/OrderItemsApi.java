package com.pos.increff.api;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pos.increff.dao.OrderItemsDao;
import com.pos.increff.pojo.OrderItemsPojo;

@Service
public class OrderItemsApi{
    @Autowired
    private OrderItemsDao dao;

    @Transactional
    public void add(List<OrderItemsPojo> order_items) throws ApiException {
        dao.insert(order_items) ;
    }

    @Transactional(readOnly = true)
    public List<OrderItemsPojo> getAll() throws ApiException {
        return dao.selectAll();
    }

    @Transactional(readOnly = true)
    public List<OrderItemsPojo> getByOrderId(int order_id) throws ApiException {
        List<OrderItemsPojo> items = dao.selectByOrderId(order_id);
        if (items.isEmpty()) {
            throw new ApiException("No items found for order ID: " + order_id);
        }
        return items;
    }

    @Transactional
    public void deleteAll() {
        dao.deleteAll();
    }
}
