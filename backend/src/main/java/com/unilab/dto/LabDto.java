package com.unilab.dto;

import jakarta.validation.constraints.*;
import com.unilab.model.Lab;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LabDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    
    @NotBlank(message = "Lab name is required")
    @Size(max = 100, message = "Lab name must not exceed 100 characters")
    private String name;
    
    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;
    
    @Size(max = 100, message = "Location must not exceed 100 characters")
    private String location;
    
    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    @Max(value = 1000, message = "Capacity must not exceed 1000")
    private Integer capacity;
    
    @Size(max = 20, message = "Status must not exceed 20 characters")
    private String status;
    
    // Constructors
    public LabDto() {}
    
    public LabDto(Long id, String name, String location, Integer capacity, String status) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.capacity = capacity;
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public Integer getCapacity() {
        return capacity;
    }
    
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }

    public static LabDto fromEntity(Lab lab) {
        LabDto dto = new LabDto();
        dto.setId(lab.getId());
        dto.setName(lab.getName());
        dto.setDescription(lab.getDescription());
        dto.setLocation(lab.getLocation());
        dto.setCapacity(lab.getCapacity());
        dto.setStatus(lab.getStatus());
        return dto;
    }

    public static Lab toEntity(LabDto dto) {
        Lab lab = new Lab();
        lab.setId(dto.getId());
        lab.setName(dto.getName());
        lab.setDescription(dto.getDescription());
        lab.setLocation(dto.getLocation());
        lab.setCapacity(dto.getCapacity());
        lab.setStatus(dto.getStatus());
        return lab;
    }

}
