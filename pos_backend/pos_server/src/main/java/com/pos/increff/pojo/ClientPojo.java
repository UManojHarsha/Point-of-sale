package com.pos.increff.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name = "clients")
@Getter
@Setter
public class ClientPojo extends AbstractEntity {

    @Id
    @TableGenerator(
        name = "client_id_generator",
        table = "id_sequences",
        pkColumnName = "sequence_name",
        valueColumnName = "sequence_value",
        pkColumnValue = "client_sequence",
        initialValue = 1
    )
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "client_id_generator")
    private Integer id;

    @Column(nullable = false , unique = true)
    private String name;

    @Column(nullable = false , unique = true)
    private String email ;

    @Column(nullable = false , unique = true)
    private String contactNo ;
}