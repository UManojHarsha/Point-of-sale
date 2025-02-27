package com.pos.increff.dao;

import com.pos.increff.pojo.OrderItemsPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.persistence.Query;
import java.util.List;

@Repository
public class OrderItemsDao extends AbstractDao{
    String select_order_id = "select c from OrderItemsPojo c where c.orderId=:order_id" ;
    private static String select_all = "select p from OrderItemsPojo p";
    private static String select_by_orderId = "select p from OrderItemsPojo p where p.orderId=:orderId";

    public void insert(List<OrderItemsPojo> order_items) {
        batchInsertQuery(order_items);
    }

    public List<OrderItemsPojo> selectByUserId(int order_id){
        TypedQuery<OrderItemsPojo> query = getQuery(select_order_id, OrderItemsPojo.class);
        query.setParameter("order_id", order_id);
        return query.getResultList();
    }

    public List<OrderItemsPojo> selectAll() {
        TypedQuery<OrderItemsPojo> query = getQuery(select_all, OrderItemsPojo.class);
        return query.getResultList();
    }

    public List<OrderItemsPojo> selectByOrderId(int orderId) {
        TypedQuery<OrderItemsPojo> query = getQuery(select_by_orderId, OrderItemsPojo.class);
        query.setParameter("orderId", orderId);
        return query.getResultList();
    }

    public void deleteAll() {
        Query query = em().createQuery("delete from OrderItemsPojo");
        query.executeUpdate();
    }
}
