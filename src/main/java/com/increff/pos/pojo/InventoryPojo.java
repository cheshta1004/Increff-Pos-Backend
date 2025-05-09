package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "inventory", uniqueConstraints = { @UniqueConstraint(columnNames = {"productId"})})
public class InventoryPojo extends AbstractVersionedPojo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column( nullable = false)
    private Integer productId;
    @Column(nullable = false)
    private Integer quantity;
}
