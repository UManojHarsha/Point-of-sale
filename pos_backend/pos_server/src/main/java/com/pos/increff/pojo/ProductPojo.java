package com.pos.increff.pojo;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "products", indexes = {
    @Index(name = "idx_products_client_id", columnList = "clientId")
})
@Getter
@Setter
public class ProductPojo extends AbstractEntity {

    @Id
    @TableGenerator(
        name = "product_id_generator",
        table = "id_sequences",
        pkColumnName = "sequence_name",
        valueColumnName = "sequence_value",
        pkColumnValue = "product_sequence",
        initialValue = 1
    )
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "product_id_generator")
    private Integer id;

    @Column(nullable = false , unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String barcode;

    @Column(nullable = false)
    private Integer clientId;

    @Column(nullable = false)
    private Double price;

}
