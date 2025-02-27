package com.pos.increff.dao;
import com.pos.increff.pojo.ProductPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.persistence.Query;
import java.util.List;

@Repository
public class ProductDao extends AbstractDao {
    String select_all = "select c from ProductPojo c";
    String update_details = "update ProductPojo c set c.name=:name, c.barcode=:barcode, c.price=:price , c.clientId=:clientId where c.id=:id" ;
    String select_name = "select c from ProductPojo c where c.name=:name";
    String search_by_name = "select c from ProductPojo c where lower(c.name) like :name escape '\\\\'";
    String select_barcode = "select c from ProductPojo c where c.barcode=:barcode";

    public void insert(List<ProductPojo> products) {
        batchInsertQuery(products);
    }

    public void insert(ProductPojo product) {
        insertQuery(product);
    }

    // Read operations with TypedQuery
    public ProductPojo select(int id) {
        return selectQuery(id, ProductPojo.class);
    }

    public List<ProductPojo> selectAll() {
        TypedQuery<ProductPojo> query = getQuery(select_all, ProductPojo.class) ;
        return query.getResultList();
    }

    public List<ProductPojo> selectAll(int page, int pageSize) {
        return getPaginatedResults(select_all, ProductPojo.class, page, pageSize);
    }

    public ProductPojo selectByName(String name) {
        TypedQuery<ProductPojo> query = getQuery(select_name, ProductPojo.class);
        query.setParameter("name", name);
        return getSingle(query);
    }


    public void delete(int id) {
        ProductPojo product = select(id);
        if (product != null) {
            deleteQuery(product);
        }
    }

    public ProductPojo selectByBarcode(String barcode) {
        TypedQuery<ProductPojo> query = getQuery(select_barcode, ProductPojo.class);
        query.setParameter("barcode", barcode);
        return getSingle(query);
    }


    public void updateDetails(int id, String name, Double price, String barcode, Integer clientId) {
        Query query = em().createQuery("update ProductPojo p set p.name=:name, p.price=:price, p.barcode=:barcode, p.clientId=:clientId where p.id=:id");
        query.setParameter("id", id);
        query.setParameter("name", name);
        query.setParameter("price", price);
        query.setParameter("barcode", barcode);
        query.setParameter("clientId", clientId);
        query.executeUpdate();
    }

    public void deleteAll() {
        Query query = em().createQuery("delete from ProductPojo");
        query.executeUpdate();
    }

    public List<ProductPojo> searchByName(String name, int page, int pageSize) {
        String searchPattern = "%" + name.toLowerCase() + "%";
        List<ProductPojo> results = getPaginatedResults(search_by_name, ProductPojo.class, page, pageSize, "name", searchPattern);
        return results;
    }
}
