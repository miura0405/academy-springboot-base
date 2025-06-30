package com.spring.springbootapplication.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class SkillNewForm {

    @NotBlank(message = "項目名は必ず入力してください")
    @Size(max = 50, message = "項目名は50文字以内で入力してください")
    private String learningName;

    @NotNull(message = "学習時間は必ず入力してください")
    @Min(value = 1, message = "学習時間は0以上で入力してください")
    private Integer learningTime;

    @NotNull(message = "カテゴリIDが不正です")
    private Integer categoryId;

    @NotNull(message = "月が選択されていません")
    private String learningMonth;
}   
