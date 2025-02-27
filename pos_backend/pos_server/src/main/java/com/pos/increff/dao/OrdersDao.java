package com.pos.increff.dao;

import com.pos.increff.pojo.OrdersPojo;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Repository
public class OrdersDao extends AbstractDao {
    String select_user_id = "select c from OrderPojo c where c.userId=:userId" ;
    private static String select_all = "select p from OrdersPojo p order by p.createdDate desc";
    private static String select_order_id = "select p from OrdersPojo p where p.id=:id";
    private static String select_by_date_range = "select p from OrdersPojo p where p.createdDate >= :fromDate and p.createdDate < :toDate";

    public int insert(OrdersPojo order) {
        em().persist(order);
        em().flush(); // Force flush to ensure ID is generated
        return order.getId();
    }

    public List<OrdersPojo> selectByUserId(int user_id){
        TypedQuery<OrdersPojo> query = getQuery(select_user_id, OrdersPojo.class);
        query.setParameter("user_id", user_id);
        return query.getResultList();
    }

    public List<OrdersPojo> selectAll(int page, int pageSize) {
        return getPaginatedResults(select_all, OrdersPojo.class, page, pageSize);
    }

    public OrdersPojo select(int id) {
        TypedQuery<OrdersPojo> query = getQuery(select_order_id, OrdersPojo.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    @Transactional(readOnly = true)
    public List<OrdersPojo> selectByDateRange(Date fromDate, Date toDate) {
        TypedQuery<OrdersPojo> query = getQuery(select_by_date_range, OrdersPojo.class);
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);
        return query.getResultList();
    }

    public void deleteAll() {
        Query query = em().createQuery("delete from OrdersPojo");
        query.executeUpdate();
    }

    public void update(OrdersPojo order) {
        em().merge(order);
    }
}
