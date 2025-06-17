package com.spring.springbootapplication.dto;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningDataDto {
  private Integer id;
  private String learningName;
    private Integer learningTime;
    private Integer categoryId;
    private LocalDate learningDate;
}
