package com.example.demodatn.entity;

import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "FOOD_TYPE")
@Where(clause = "is_deleted = 0")
public class FoodTypeEntity extends BaseEntity{

    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "FOOD_TYPE_SEQ", sequenceName = "FOOD_TYPE_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "FOOD_TYPE_SEQ")
    private Long id;
    @Column(name = "NAME", nullable = false, length = 100)
    private String name;
    @Column(name = "AVATAR")
    private String avatar;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
