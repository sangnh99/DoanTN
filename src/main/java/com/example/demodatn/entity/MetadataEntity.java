package com.example.demodatn.entity;

import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "METADATA")
@Where(clause = "is_deleted = 0")
public class MetadataEntity extends BaseEntity {
    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "METADATA_SEQ", sequenceName = "METADATA_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "METADATA_SEQ")
    private Long id;
    @Column(name = "TYPE")
    private Integer type;
    @Column(name = "VALUE")
    private String value;
    @Column(name = "TOTAL_INCOME")
    private Long totalIncome;
    @Column(name = "TOTAL_FOOD")
    private Integer totalFood;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(Long totalIncome) {
        this.totalIncome = totalIncome;
    }

    public Integer getTotalFood() {
        return totalFood;
    }

    public void setTotalFood(Integer totalFood) {
        this.totalFood = totalFood;
    }
}
