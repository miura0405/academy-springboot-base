package com.spring.springbootapplication.dto;

import com.spring.springbootapplication.dto.LearningDataDto;
import lombok.Data;

import java.util.List;

@Data
public class LearningDataForm {

    private String targetMonth; // フォーム内のhiddenで保持（例："2025-05"）
    private List<LearningDataDto> learningDataList; // 各行のデータ（id＋時間）
    private String learningName;
}
