package com.spring.springbootapplication.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LearningDataForm {

    private Integer id;
    private Integer categoryId;
    private String name;
    private Integer time;
}
