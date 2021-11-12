package com.example.demo.entity;

import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "USER_ROLE")
@Where(clause = "is_deleted = 0")
public class UserRoleEntity extends BaseEntity {

//    private static final long serialVersionUID = 8240166039112687603L;

    public static final String QUERY_ENTITY = "userRoleChannel";
    public static final String QUERY_TABLE = "UserRoleChannelEntity " + QUERY_ENTITY;
    public static final String QUERY_PARAM_CHANNEL_ID = QUERY_ENTITY + ".channelId";
    public static final String QUERY_PARAM_USER_ID = QUERY_ENTITY + ".userId";
    public static final String QUERY_PARAM_ROLE_ID = QUERY_ENTITY + ".roleId";
    public static final String QUERY_PARAM_IS_DELETED = QUERY_ENTITY + ".isDeleted";

    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "USER_ROLE_SEQ", sequenceName = "USER_ROLE_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "USER_ROLE_SEQ")
    private Long id;
    @Column(name = "USER_ID")
    private Long userId;
    @Column(name = "ROLE_ID")
    private Long roleId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_ID", insertable = false, updatable = false)
    private UserAppEntity user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ROLE_ID", insertable = false, updatable = false)
    private RoleEntity role;

    public UserAppEntity getUser() {
        return user;
    }

    public void setUser(UserAppEntity user) {
        this.user = user;
    }

    public RoleEntity getRole() {
        return role;
    }

    public void setRole(RoleEntity role) {
        this.role = role;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

}