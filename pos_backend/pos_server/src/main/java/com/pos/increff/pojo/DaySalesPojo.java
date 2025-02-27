package com.pos.increff.pojo;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Index;

import java.util.Date;

@Entity
@Table(name = "pos_day_sales", indexes = {
    @Index(name = "idx_day_sales_date", columnList = "date")
})
@Getter
@Setter
public class DaySalesPojo extends AbstractEntity {
    @Id
    @TableGenerator(
        name = "day_sales_id_generator",
        table = "id_sequences",
        pkColumnName = "sequence_name",
        valueColumnName = "sequence_value",
        pkColumnValue = "day_sales_sequence",
        initialValue = 1
    )
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "day_sales_id_generator")
    private Integer id;

    @Column(nullable = false)
    private Date date;

    @Column(nullable = false)
    private Integer orderCount;

    @Column(nullable = false)
    private Integer productCount;
    
    @Column(nullable = false)
    private Double revenue;
} 