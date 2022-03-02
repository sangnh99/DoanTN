package com.example.demodatn.constant;

public enum RoleConstant {
    ROLE_ADMIN(1L, "ROLE_ADMIN"),
    ROLE_USER(2L, "ROLE_USER"),
    ROLE_SHIPPER(3L, "ROLE_SHIPPER");

    private Long number;
    private String roleCode;

    RoleConstant(Long number, String roleCode) {
        this.number = number;
        this.roleCode = roleCode;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }
}
