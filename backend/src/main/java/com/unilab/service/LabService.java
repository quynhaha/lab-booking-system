package com.unilab.service;

import com.unilab.dto.LabDto;
import com.unilab.model.Lab;
import com.unilab.repository.LabRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LabService {

    @Autowired
    private LabRepository labRepository;

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
        if (!labRepository.existsById(id)) { // ✅ added
            throw new RuntimeException("Lab not found with id: " + id);
        }
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
