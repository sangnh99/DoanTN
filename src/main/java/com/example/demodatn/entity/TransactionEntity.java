package com.example.demodatn.entity;

import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "TRANSACTION")
@Where(clause = "is_deleted = 0")
public class TransactionEntity extends BaseEntity{
    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "TRANSACTION_SEQ", sequenceName = "TRANSACTION_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "TRANSACTION_SEQ")
    private Long id;
    @Column(name = "USER_APP_ID")
    private Long userAppId;
    @Column(name = "PAYMENT_METHOD")
    private String paymentMethod;
    @Column(name = "COMMENT")
    private String comment;
    @Column(name = "TOTAL")
    private Long total;
    // new
    @Column(name = "DISTANCE")
    private Double distance;
    @Column(name = "DELIVERY_ADDRESS_ID")
    private Long deliveryAddressId;
    @Column(name = "STORE_ID")
    private Long storeId;

    @Column(name = "STATUS")
    private Integer status;
    @Column(name = "SHIPPER_ID")
    private Long shipperId;
    @Column(name = "TIME_START")
    private Date timeStart;
    @Column(name = "TIME_END")
    private Date timeEnd;

    @Column(name = "DELIVERY_ADDRESS")
    private String deliveryAddress;
    @Column(name = "DELIVERY_LATITUDE")
    private Double deliveryLatitude;
    @Column(name = "DELIVERY_LONGITUDE")
    private Double deliveryLongitude;


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

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Long getDeliveryAddressId() {
        return deliveryAddressId;
    }

    public void setDeliveryAddressId(Long deliveryAddressId) {
        this.deliveryAddressId = deliveryAddressId;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getShipperId() {
        return shipperId;
    }

    public void setShipperId(Long shipperId) {
        this.shipperId = shipperId;
    }

    public Date getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Date timeStart) {
        this.timeStart = timeStart;
    }

    public Date getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Date timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public Double getDeliveryLatitude() {
        return deliveryLatitude;
    }

    public void setDeliveryLatitude(Double deliveryLatitude) {
        this.deliveryLatitude = deliveryLatitude;
    }

    public Double getDeliveryLongitude() {
        return deliveryLongitude;
    }

    public void setDeliveryLongitude(Double deliveryLongitude) {
        this.deliveryLongitude = deliveryLongitude;
    }
}
