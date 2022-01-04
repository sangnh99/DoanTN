package com.example.demodatn.entity;

import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "FAVOURITE")
@Where(clause = "is_deleted = 0")
public class FavouriteEntity extends BaseEntity{
    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "FAVOURITE_SEQ", sequenceName = "FAVOURITE_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "FAVOURITE_SEQ")
    private Long id;
    @Column(name = "USER_APP_ID")
    private Long userAppId;
    @Column(name = "ITEM_ID")
    private Long itemId;
    @Column(name = "TYPE")
    private Integer type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserAppId() {
        return userAppId;
    }

    public void setUserAppId(Long userAppId) {
        this.userAppId = userAppId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
