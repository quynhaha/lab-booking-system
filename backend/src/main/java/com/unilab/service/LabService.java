package com.unilab.service;

import com.unilab.dto.LabDto;
import com.unilab.model.Lab;
import com.unilab.repository.LabRepository;
import com.unilab.repository.EquipmentRepository;
import com.unilab.repository.EventRepository;
import com.unilab.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LabService {

    @Autowired
    private LabRepository labRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private BookingRepository bookingRepository;

    // Get all labs
    public List<LabDto> getAllLabs() {
        return labRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Get available labs
    public List<LabDto> getAvailableLabs() {
        return labRepository.findAllAvailableLabs().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Get lab by ID
    public LabDto getLabById(Long id) {
        Lab lab = labRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lab not found with id: " + id));
        return convertToDto(lab);
    }

    // Search labs
    public List<LabDto> searchLabs(String keyword) {
        return labRepository.searchLabs(keyword).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Create lab
    @Transactional
    public LabDto createLab(LabDto dto) {
        Lab lab = new Lab();
        lab.setName(dto.getName());
        lab.setDescription(dto.getDescription());
        lab.setLocation(dto.getLocation());
        lab.setCapacity(dto.getCapacity());
        lab.setStatus(dto.getStatus() != null ? dto.getStatus() : "AVAILABLE");
        // lab.setFacilities(dto.getFacilities()); // Removed: facilities field no longer exists

        Lab saved = labRepository.save(lab);
        return convertToDto(saved);
    }

    // Update lab
    @Transactional
    public LabDto updateLab(Long id, LabDto dto) {
        Lab lab = labRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lab not found with id: " + id));

        lab.setName(dto.getName());
        lab.setDescription(dto.getDescription());
        lab.setLocation(dto.getLocation());
        lab.setCapacity(dto.getCapacity());
        lab.setStatus(dto.getStatus());
        // lab.setFacilities(dto.getFacilities()); // Removed: facilities field no longer exists
        // lab.setUpdatedAt(LocalDateTime.now()); // Removed: updatedAt field no longer exists

        Lab saved = labRepository.save(lab);
        return convertToDto(saved);
    }

    // Delete lab
    @Transactional
    public void deleteLab(Long id) {
        if (!labRepository.existsById(id)) {
            throw new RuntimeException("Lab not found with id: " + id);
        }
        
        // Delete all equipment associated with this lab first
        equipmentRepository.findByLabId(id).forEach(equipment -> {
            equipmentRepository.delete(equipment);
        });
        
        // Delete all events associated with this lab
        eventRepository.findByLabId(id).forEach(event -> {
            eventRepository.delete(event);
        });
        
        // Delete all bookings associated with this lab
        bookingRepository.findByLabId(id).forEach(booking -> {
            bookingRepository.delete(booking);
        });
        
        // Now delete the lab
        labRepository.deleteById(id);
    }

    // Helper method
    private LabDto convertToDto(Lab lab) {
        LabDto dto = new LabDto();
        dto.setId(lab.getId());
        dto.setName(lab.getName());
        dto.setDescription(lab.getDescription());
        dto.setLocation(lab.getLocation());
        dto.setCapacity(lab.getCapacity());
        dto.setStatus(lab.getStatus());
        return dto;
    }
}
