package com.pos.increff.dao;

import com.pos.increff.pojo.DaySalesPojo;
import org.springframework.stereotype.Repository;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

@Repository
public class DaySalesDao extends AbstractDao {
    private static final String SELECT_BY_DATE = "select p from DaySalesPojo p where p.date = :date";
    private static final String SELECT_ALL = "select p from DaySalesPojo p order by p.date";
    private static final String SELECT_BY_DATE_RANGE = 
    "select p FROM DaySalesPojo p " +
    "where (:startDate IS NULL OR p.date >= :startDate) " +
    "and (:endDate IS NULL OR p.date <= :endDate) " +
    "order by p.date";

    public void insert(DaySalesPojo daySales) {
       insertQuery(daySales);;
    }

    public void update(DaySalesPojo daySales) {
        mergeQuery(daySales);
    }

    public DaySalesPojo selectByDate(Date date) {
        TypedQuery<DaySalesPojo> query = getQuery(SELECT_BY_DATE, DaySalesPojo.class);
        query.setParameter("date", date);
        return getSingle(query);
    }

    public List<DaySalesPojo> selectAll() {
        TypedQuery<DaySalesPojo> query = getQuery(SELECT_ALL, DaySalesPojo.class);
        return query.getResultList();
    }

    public List<DaySalesPojo> selectByDateRange(Date startDate, Date endDate) {
        TypedQuery<DaySalesPojo> query = getQuery(SELECT_BY_DATE_RANGE, DaySalesPojo.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }
} 