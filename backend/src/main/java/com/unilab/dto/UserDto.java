package com.unilab.dto;

import java.time.OffsetDateTime;

public class UserDto {
    private Long id;
    private String fullName;
    private String email;
    private String roleName;
    private String studentId;
    private String faculty;
    private boolean status;
    private OffsetDateTime createdAt;
    
    public UserDto() {}
    
    public UserDto(Long id, String fullName, String email, String roleName, 
                   String studentId, String faculty, boolean status, OffsetDateTime createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.roleName = roleName;
        this.studentId = studentId;
        this.faculty = faculty;
        this.status = status;
        this.createdAt = createdAt;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getRoleName() {
        return roleName;
    }
    
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
    
    public String getStudentId() {
        return studentId;
    }
    
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    
    public String getFaculty() {
        return faculty;
    }
    
    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }
    
    public boolean isStatus() {
        return status;
    }
    
    public void setStatus(boolean status) {
        this.status = status;
    }
    
    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
