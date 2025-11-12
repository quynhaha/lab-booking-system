package com.unilab.service;

import com.unilab.dto.EquipmentResponse;
import com.unilab.dto.LabSearchResponse;
import com.unilab.model.Equipment;
import com.unilab.model.Lab;
import com.unilab.repository.EquipmentRepository;
import com.unilab.repository.LabRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LabSearchService {
    
    @Autowired
    private EquipmentRepository equipmentRepository;
    
    @Autowired
    private LabRepository labRepository;
    
    public List<LabSearchResponse> searchLabsByEquipment(String equipmentName) {
        // Find equipment by name
        List<Equipment> equipmentList = equipmentRepository.findByNameContainingIgnoreCase(equipmentName);
        
        if (equipmentList.isEmpty()) {
            return List.of(); // Return empty list if no equipment found
        }
        
        // Get unique lab IDs from equipment
        List<Long> labIds = equipmentList.stream()
                .map(equipment -> equipment.getLab().getId())
                .distinct()
                .collect(Collectors.toList());
        
        // Get labs by IDs
        List<Lab> labs = labRepository.findAllById(labIds);
        
        // Convert to response DTOs
        return labs.stream()
                .map(this::convertToLabSearchResponse)
                .collect(Collectors.toList());
    }
    
    public List<LabSearchResponse> searchLabsByEquipmentType(String equipmentType) {
        // Find equipment by type
        List<Equipment> equipmentList = equipmentRepository.findByType(equipmentType);
        
        if (equipmentList.isEmpty()) {
            return List.of();
        }
        
        // Get unique lab IDs from equipment
        List<Long> labIds = equipmentList.stream()
                .map(equipment -> equipment.getLab().getId())
                .distinct()
                .collect(Collectors.toList());
        
        // Get labs by IDs
        List<Lab> labs = labRepository.findAllById(labIds);
        
        // Convert to response DTOs
        return labs.stream()
                .map(this::convertToLabSearchResponse)
                .collect(Collectors.toList());
    }
    
    private LabSearchResponse convertToLabSearchResponse(Lab lab) {
        // Get equipment for this lab
        List<Equipment> equipmentList = equipmentRepository.findByLabId(lab.getId());
        
        List<EquipmentResponse> equipmentResponses = equipmentList.stream()
                .map(this::convertToEquipmentResponse)
                .collect(Collectors.toList());
        
        return new LabSearchResponse(
                lab.getId(),
                lab.getName(),
                lab.getCapacity(),
                lab.getLocation(),
                lab.getDescription(),
                lab.getStatus(),
                equipmentResponses
        );
    }
    
    private EquipmentResponse convertToEquipmentResponse(Equipment equipment) {
        return new EquipmentResponse(
                equipment.getId(),
                equipment.getName(),
                equipment.getType(),
                equipment.getDescription(),
                equipment.getStatus()
        );
    }
}
