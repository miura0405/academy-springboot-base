package com.spring.springbootapplication.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserRegistrationForm {

    @NotBlank(message = "氏名は必ず入力してください")
    @Size(max = 255, message = "氏名は255文字以内で入力してください")
    private String name;

    @NotBlank(message = "メールアドレスは必ず入力してください")
    @Email(message = "メールアドレスが正しい形式ではありません")
    @Size(max = 255, message = "メールアドレスは255文字以内で入力してください")
    private String email;

    @NotBlank(message = "パスワードは必ず入力してください")
    @Pattern(
        regexp = "^$|^(?=.*[a-zA-Z])(?=.*[0-9]).{8,}$",
        message = "英数字8文字以上で入力してください"
    )
private String password;

}
