package com.pos.increff.pojo;

import com.pos.commons.OrderStatus;
import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "orders", indexes = {
    @Index(name = "idx_orders_user_email", columnList = "userEmail"),
    @Index(name = "idx_orders_status", columnList = "status")
})
@Getter
@Setter
public class OrdersPojo extends AbstractEntity {

    @Id
    @TableGenerator(
        name = "orders_id_generator",
        table = "id_sequences",
        pkColumnName = "sequence_name",
        valueColumnName = "sequence_value",
        pkColumnValue = "orders_sequence",
        initialValue = 1
    )
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "orders_id_generator")
    private Integer id;

    @Column(nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private Double totalPrice;

    @Column
    private String invoicePath;

    @Column(nullable = false)
    private OrderStatus status;
}
