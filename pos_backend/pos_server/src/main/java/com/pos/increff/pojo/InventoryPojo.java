package com.pos.increff.pojo;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "inventory", indexes = {
    @Index(name = "idx_inventory_product_id", columnList = "productId")
})
@Getter
@Setter
public class InventoryPojo extends AbstractEntity {

    @Id
    @TableGenerator(
        name = "inventory_id_generator",
        table = "id_sequences",
        pkColumnName = "sequence_name",
        valueColumnName = "sequence_value",
        pkColumnValue = "inventory_sequence",
        initialValue = 1
    )
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "inventory_id_generator")
    private Integer id;

    @Column(nullable = false)
    private Integer productId;

    @Column(nullable = false)
    private Integer totalQuantity; 
}
