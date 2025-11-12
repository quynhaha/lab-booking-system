package com.unilab.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ReportDto {
    private String reportType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime generatedAt;
    private String generatedBy;
    private Map<String, Object> summary;
    private List<Map<String, Object>> data;
    private Map<String, Object> charts;
    
    // Constructors
    public ReportDto() {}
    
    public ReportDto(String reportType, LocalDateTime startDate, LocalDateTime endDate) {
        this.reportType = reportType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.generatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getReportType() {
        return reportType;
    }
    
    public void setReportType(String reportType) {
        this.reportType = reportType;
    }
    
    public LocalDateTime getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }
    
    public LocalDateTime getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
    
    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }
    
    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }
    
    public String getGeneratedBy() {
        return generatedBy;
    }
    
    public void setGeneratedBy(String generatedBy) {
        this.generatedBy = generatedBy;
    }
    
    public Map<String, Object> getSummary() {
        return summary;
    }
    
    public void setSummary(Map<String, Object> summary) {
        this.summary = summary;
    }
    
    public List<Map<String, Object>> getData() {
        return data;
    }
    
    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }
    
    public Map<String, Object> getCharts() {
        return charts;
    }
    
    public void setCharts(Map<String, Object> charts) {
        this.charts = charts;
    }
}
