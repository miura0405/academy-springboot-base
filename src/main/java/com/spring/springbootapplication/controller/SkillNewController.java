package com.spring.springbootapplication.controller;

import com.spring.springbootapplication.dto.SkillNewForm;
import com.spring.springbootapplication.entity.LearningData;
import com.spring.springbootapplication.service.CategoryService;
import com.spring.springbootapplication.service.LearningDataService;
import com.spring.springbootapplication.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/skill")
public class SkillNewController {

    private final CategoryService categoryService;
    private final LearningDataService learningDataService;
    private final UserService userService;

    @PostMapping("/new")
    public ResponseEntity<?> saveSkillAjax(
            @Valid @RequestBody SkillNewForm form,
            BindingResult bindingResult,
            Principal principal) {
    
        Map<String, Object> response = new HashMap<>();
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error ->
                response.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(response);
        }
    
        String email = principal.getName();
        Integer userId = userService.findByEmail(email).getId();
    
        boolean isDuplicate = learningDataService.existsByNameAndMonthAndUserId(
    form.getLearningName(),
    LocalDate.parse(form.getLearningMonth()), userId);
    
        if (isDuplicate) {
            response.put("learningName", form.getLearningName() + "は既に登録されています");
            return ResponseEntity.badRequest().body(response);
        }
    
        LearningData newData = new LearningData();
        newData.setUserId(userId);
        newData.setCategoryId(form.getCategoryId());
        newData.setLearningName(form.getLearningName());
        newData.setLearningTime(form.getLearningTime());
        newData.setLearningMonth(LocalDate.parse(form.getLearningMonth()));
    
        learningDataService.save(newData);
    
        response.put("message", "success");
        response.put("learningName", form.getLearningName());
        response.put("learningTime", form.getLearningTime());
        response.put("learningMonth", form.getLearningMonth());
        response.put("categoryId", form.getCategoryId());
        response.put("categoryName", categoryService.getCategoryNameById(form.getCategoryId()));
    
        return ResponseEntity.ok(response);
    }
}
    