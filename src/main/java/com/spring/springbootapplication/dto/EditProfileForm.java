package com.spring.springbootapplication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EditProfileForm {

    @NotBlank(message = "自己紹介は50文字以上200文字以下で入力してください")
    @Size(min = 50, max = 200, message = "自己紹介は50文字以上200文字以下で入力してください")
    private String profile;
}
