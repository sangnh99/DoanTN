package com.example.demodatn.entity;

import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "SUB_FOOD_TYPE")
@Where(clause = "is_deleted = 0")
public class SubFoodTypeEntity extends BaseEntity {
    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "SUB_FOOD_TYPE_SEQ", sequenceName = "SUB_FOOD_TYPE_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SUB_FOOD_TYPE_SEQ")
    private Long id;
    @Column(name = "STORE_ID")
    private Long storeId;
    @Column(name = "PARENT_ID")
    private Long parentId;
    @Column(name = "NAME")
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
