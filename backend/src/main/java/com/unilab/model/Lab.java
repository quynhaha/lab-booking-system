package com.unilab.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "labs")
public class Lab {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long labId;
    
    @Column(name = "lab_name", nullable = false, length = 100)
    private String name;
    
    @Column(length = 255)
    private String description;
    
    @Column(length = 100)
    private String location;
    
    @Column(nullable = false)
    private Integer capacity;
    
    @Column(length = 20)
    private String status; // AVAILABLE, MAINTENANCE, OCCUPIED
    
    @OneToMany(mappedBy = "lab", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings = new ArrayList<>();
    
    // Constructors
    public Lab() {}
    
    public Lab(String name, String location, Integer capacity, String status) {
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.status = status;
    }
    
    // Getters and Setters
    public Long getId() {
        return labId;
    }
    
    public void setId(Long labId) {
        this.labId = labId;
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
    
    public List<Booking> getBookings() {
        return bookings;
    }
    
    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }
}
