package com.unilab.dto;

public class EquipmentResponse {
    
    private Long id;
    private String name;
    private String type;
    private String description;
    private String status;
    
    public EquipmentResponse() {}
    
    public EquipmentResponse(Long id, String name, String type, String description, String status) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.status = status;
    }
    
    // Getters and Setters
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
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}
