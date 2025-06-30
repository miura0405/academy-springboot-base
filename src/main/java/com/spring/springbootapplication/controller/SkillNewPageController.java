package com.spring.springbootapplication.controller;

import com.spring.springbootapplication.dto.SkillNewForm;
import com.spring.springbootapplication.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.YearMonth;

@Controller
@RequiredArgsConstructor
public class SkillNewPageController {

    private final CategoryService categoryService;

    @GetMapping("/skill/new")
    public String showSkillNewPage(
            @RequestParam("category") Integer categoryId,
            @RequestParam(value = "month", required = false) String month,
            Model model) {

        SkillNewForm form = new SkillNewForm();
        form.setCategoryId(categoryId);
        form.setLearningMonth(
            (month != null && !month.trim().isEmpty())
                ? YearMonth.parse(month).atDay(1).toString()
                : YearMonth.now().atDay(1).toString()
        );

        String categoryName;
        try {
            categoryName = categoryService.getCategoryNameById(categoryId);
        } catch (Exception e) {
            System.out.println("⚠️ categoryName の取得に失敗: " + e.getMessage());
            categoryName = "不明カテゴリ";
        }

        model.addAttribute("categoryId", form.getCategoryId());
        model.addAttribute("learningMonth", form.getLearningMonth());
        model.addAttribute("categoryName", categoryName);

        return "skillNew";
    }
}
