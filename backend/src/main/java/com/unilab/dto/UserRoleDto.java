package com.unilab.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public class UserRoleDto {
    @Email(message = "Valid email is required")
    private String email;
    
    @NotNull(message = "Role ID is required")
    private Long roleId;
    
    public UserRoleDto() {}
    
    public UserRoleDto(String email, Long roleId) {
        this.email = email;
        this.roleId = roleId;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Long getRoleId() {
        return roleId;
    }
    
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
