package com.example.demodatn.entity;

import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "USER_APP")
@Where(clause = "is_deleted = 0")
public class UserAppEntity extends BaseEntity{
    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "USER_APP_SEQ", sequenceName = "USER_APP_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "USER_APP_SEQ")
    private Long id;
    @Column(name = "USERNAME")
    private String username;
    @Column(name = "PASSWORD")
    private String password;
    @Column(name = "FULL_NAME")
    private String fullName;
    @Column(name = "PHONE")
    private String phone;
    @Column(name = "ADDRESS")
    private String address;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "AVATAR")
    private String avatar;
    @Column(name = "RESET_PASSWORD_TOKEN")
    private String resetPasswordToken;
    @Column(name = "VERIFY_EMAIL_TOKEN")
    private String verifyEmailToken;
    @Column(name = "ACTIVE_ADDRESS_ID")
    private Long activeAddressId;
    @Column(name = "IS_LOCKED")
    private Integer isLocked;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "USER_ID", insertable = false, updatable = false)
    private List<UserRoleEntity> userRoleEntities = null;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public List<UserRoleEntity> getUserRoleEntities() {
        return userRoleEntities;
    }

    public void setUserRoleEntities(List<UserRoleEntity> userRoleEntities) {
        this.userRoleEntities = userRoleEntities;
    }

    public String getResetPasswordToken() {
        return resetPasswordToken;
    }

    public void setResetPasswordToken(String resetPasswordToken) {
        this.resetPasswordToken = resetPasswordToken;
    }

    public String getVerifyEmailToken() {
        return verifyEmailToken;
    }

    public void setVerifyEmailToken(String verifyEmailToken) {
        this.verifyEmailToken = verifyEmailToken;
    }

    public Long getActiveAddressId() {
        return activeAddressId;
    }

    public void setActiveAddressId(Long activeAddressId) {
        this.activeAddressId = activeAddressId;
    }

    public Integer getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(Integer isLocked) {
        this.isLocked = isLocked;
    }
}
