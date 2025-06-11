package com.spring.springbootapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;
import java.util.List;

@Data
@NoArgsConstructor
public class LearningEditForm {

    private List<LearningDataDto> backendList;
    private List<LearningDataDto> frontendList;
    private List<LearningDataDto> infraList;

    private YearMonth targetMonth;

    public LearningEditForm(List<LearningDataDto> backendList, List<LearningDataDto> frontendList, List<LearningDataDto> infraList, YearMonth month) {
        this.backendList = backendList;
        this.frontendList = frontendList;
        this.infraList = infraList;
        this.targetMonth = month;
    }
}
