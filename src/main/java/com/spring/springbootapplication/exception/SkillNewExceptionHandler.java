package com.spring.springbootapplication.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class SkillNewExceptionHandler {

    // ① バリデーションエラー（@Valid）
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String field = error.getField();
            String defaultMessage = error.getDefaultMessage();
            errors.put(field, defaultMessage);
        });

        return ResponseEntity.badRequest().body(errors);
    }

    // ② 型変換エラー（"a"など）
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleTypeMismatch(HttpMessageNotReadableException ex) {
        Map<String, String> errors = new HashMap<>();

        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException formatException) {
            // JSONのパース失敗時、対象フィールド名を取得
            formatException.getPath().forEach(ref -> {
                String fieldName = ref.getFieldName();
                if ("learningTime".equals(fieldName)) {
                    errors.put("learningTime", "学習時間は0以上の数字で入力してください");
                } else {
                    errors.put(fieldName, "形式が不正です");
                }
            });
        } else {
            // 原因不明な場合
            errors.put("learningTime", "学習時間の形式が不正です");
        }

        return ResponseEntity.badRequest().body(errors);
    }
}
