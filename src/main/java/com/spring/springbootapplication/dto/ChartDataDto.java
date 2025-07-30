package com.spring.springbootapplication.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ChartDataDto {
    private String categoryName;
    private LocalDate learningMonth;
    private Integer totalLearningTime;

    public ChartDataDto(String categoryName, LocalDate learningMonth, Long totalLearningTime) {
        this.categoryName = categoryName;
        this.learningMonth = learningMonth;
        this.totalLearningTime = totalLearningTime != null ? totalLearningTime.intValue() : 0;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public Integer getTotalLearningTime() {
        return totalLearningTime;
    }

    public LocalDate getLearningMonth() {
        return learningMonth;
    }

    public String getFormattedMonth() {
        return learningMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
    }
}
