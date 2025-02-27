package com.pos.increff.dao;
import com.pos.increff.pojo.InventoryPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.persistence.Query;
import java.util.List;

@Repository
public class InventoryDao extends AbstractDao {
    String select_all = "select c from InventoryPojo c";
    String update_details = "update InventoryPojo c set c.totalQuantity=:quantity where c.productId=:product_id" ;
    String update_stock = "update InventoryPojo c set c.totalQuantity=c.totalQuantity - :quantity where c.productId=:product_id";
    String select_id = "select c from InventoryPojo c where c.productId=:product_id"; 

    public void insert(List<InventoryPojo> inventory) {
        batchInsertQuery(inventory);
    }

    public void insert(InventoryPojo product) {
        insertQuery(product);
    }

    // Read operations with TypedQuery
    public InventoryPojo select(int id) {
        return selectQuery(id, InventoryPojo.class);
    }

    public List<InventoryPojo> selectAll() {
        TypedQuery<InventoryPojo> query = getQuery(select_all, InventoryPojo.class);
        return query.getResultList();
    }

    public List<InventoryPojo> selectAll(int page, int pageSize) {
        return getPaginatedResults(select_all, InventoryPojo.class, page, pageSize);
    }

    public InventoryPojo selectByProductId(int id) {
        TypedQuery<InventoryPojo> query = getQuery(select_id, InventoryPojo.class);
        query.setParameter("product_id", id);
        return getSingle(query);
    }

    public void delete(int id) {
        InventoryPojo product = select(id);
        if (product != null) {
            deleteQuery(product);
        }
    }

    // Additional utility method to find by name
    public void updateDetails(int product_id , int quantity){
        Query query = updateQuery(update_details) ;
        query.setParameter("quantity", quantity);
        query.setParameter("product_id", product_id);
        query.executeUpdate() ;
    }

    public void updateStock(int product_id , int quantity){
        Query query = updateQuery(update_stock) ;
        query.setParameter("product_id", product_id) ;
        query.setParameter("quantity", quantity);
        query.executeUpdate();
    }

    public void deleteAll() {
        Query query = em().createQuery("delete from InventoryPojo");
        query.executeUpdate();
    }
}
