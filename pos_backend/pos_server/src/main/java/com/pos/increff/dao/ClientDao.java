package com.pos.increff.dao;

import com.pos.increff.pojo.ClientPojo;
import com.pos.increff.util.DateTimeUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.persistence.Query;
import java.util.List;

@Repository
public class ClientDao extends AbstractDao {
    String select_all = "select c from ClientPojo c";
    String select_name = "select c from ClientPojo c where c.name=:name";
    String update_details = "update ClientPojo c set c.name=:name , c.email=:email , c.contactNo=:contact_no where c.id=:id" ;
    String search_by_name = "select c from ClientPojo c where lower(c.name) like :name escape '\\\\'";
    String select_email = "select c from ClientPojo c where c.email=:email";
    String select_contact = "select c from ClientPojo c where c.contactNo=:contact_no";
    // Create
    public void insert(ClientPojo client) {
        insertQuery(client);
    }

    // Read operations with TypedQuery
    public ClientPojo select(int id) {
        return selectQuery(id, ClientPojo.class);
    }
    
    public List<ClientPojo> selectAll() {
        TypedQuery<ClientPojo> query = getQuery(select_all, ClientPojo.class) ;
        return query.getResultList();
    }
    
    public List<ClientPojo> selectAll(int page, int pageSize) {
        return getPaginatedResults(select_all, ClientPojo.class, page, pageSize);
    }
    

    public void delete(int id) {
        ClientPojo client = select(id);
        if (client != null) {
            deleteQuery(client);
        }
    }
    
    public ClientPojo selectByName(String name) {
        TypedQuery<ClientPojo> query = getQuery(select_name, ClientPojo.class);
        query.setParameter("name", name);
        return getSingle(query);
    }

    public ClientPojo selectByEmail(String email) {
        TypedQuery<ClientPojo> query = getQuery(select_email, ClientPojo.class);
        query.setParameter("email", email);
        return getSingle(query);
    }

    public ClientPojo selectByContact(String contact_no) {
        TypedQuery<ClientPojo> query = getQuery(select_contact, ClientPojo.class);
        query.setParameter("contact_no", contact_no);
        return getSingle(query);
    }

    public void updateDetails(int id , String name , String email , String contact_no){
        Query query = updateQuery(update_details) ;
        query.setParameter("name", name);
        query.setParameter("email", email);
        query.setParameter("contact_no", contact_no) ;
        query.setParameter("id",id) ;
        query.executeUpdate() ;
    }

    public void deleteAll() {
        Query query = em().createQuery("delete from ClientPojo");
        query.executeUpdate();
    }

    public List<ClientPojo> searchByName(String name, int page, int pageSize) {
        String searchPattern = "%" + name.toLowerCase() + "%";
        return getPaginatedResults(search_by_name, ClientPojo.class, page, pageSize, "name", searchPattern);
    }
}
