package com.pos.increff.dao;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.pos.increff.pojo.UserPojo;

@Repository
public class UserDao extends AbstractDao {
    
    private static String select_id = "select p from UserPojo p where id=:id";
    private static String select_email = "select p from UserPojo p where email=:email";
    private static final String SELECT_ALL = "select p from UserPojo p";


    @Transactional
    public void insert(UserPojo p) {
        em().persist(p);
    }

    public void delete(int id) {
       UserPojo client = selectQuery(id , UserPojo.class);
        if (client != null) {
            deleteQuery(client);
        }
    }

    public UserPojo select(int id) {
        TypedQuery<UserPojo> query = getQuery(select_id, UserPojo.class);
        query.setParameter("id", id);
        return getSingle(query);
    }

    public UserPojo select(String email) {
        TypedQuery<UserPojo> query = getQuery(select_email, UserPojo.class);
        query.setParameter("email", email);
        return getSingle(query);
    }

    public List<UserPojo> selectAll() {
        TypedQuery<UserPojo> query = getQuery(SELECT_ALL, UserPojo.class);
        return query.getResultList();
    }

    public void deleteAll() {
        Query query = em().createQuery("delete from UserPojo");
        query.executeUpdate();
    }

}

