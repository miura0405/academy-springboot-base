package com.spring.springbootapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.YearMonth;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LearningEditForm {

    private List<LearningDataDto> backendList;
    private List<LearningDataDto> frontendList;
    private List<LearningDataDto> infraList;
    private YearMonth targetMonth;
}
