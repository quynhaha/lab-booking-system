package com.unilab.dto;

import java.util.List;

public class LabSearchResponse {
    
    private Long id;
    private String name;
    private Integer capacity;
    private String location;
    private String description;
    private String status;
    private List<EquipmentResponse> equipment;
    
    public LabSearchResponse() {}
    
    public LabSearchResponse(Long id, String name, Integer capacity, String location, 
                           String description, String status, List<EquipmentResponse> equipment) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.location = location;
        this.description = description;
        this.status = status;
        this.equipment = equipment;
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
    
    public Integer getCapacity() {
        return capacity;
    }
    
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
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
    
    public List<EquipmentResponse> getEquipment() {
        return equipment;
    }
    
    public void setEquipment(List<EquipmentResponse> equipment) {
        this.equipment = equipment;
    }
}
