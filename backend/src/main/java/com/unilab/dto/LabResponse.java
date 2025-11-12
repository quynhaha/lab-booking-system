package com.unilab.dto;

public class LabResponse {

    private Long id;
    private String name;
    private Integer capacity;
    private String location;
    private String description;
    private String status;

    public LabResponse() {}

    public LabResponse(Long id, String name, Integer capacity, String location, 
                      String description, String status) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.location = location;
        this.description = description;
        this.status = status;
    }

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
}

