package com.spring.springbootapplication.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class SkillNewForm {
    private String learningName;
    private Integer learningTime;
    private Integer categoryId;
    private LocalDate learningMonth;
}
