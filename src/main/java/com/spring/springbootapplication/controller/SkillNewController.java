package com.spring.springbootapplication.controller;

import com.spring.springbootapplication.dto.LearningDataForm;
import com.spring.springbootapplication.dto.SkillNewForm;
import com.spring.springbootapplication.entity.LearningData;
import com.spring.springbootapplication.service.CategoryService;
import com.spring.springbootapplication.service.LearningDataService;
import com.spring.springbootapplication.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.time.YearMonth;

@Controller
@RequiredArgsConstructor
public class SkillNewController {

    private final CategoryService categoryService;
    private final LearningDataService learningDataService;
    private final UserService userService;

    @GetMapping("/skill/new")
    public String showSkillNewPage(
            @RequestParam("category") Integer categoryId,
            Model model) {
        SkillNewForm form = new SkillNewForm();
        form.setCategoryId(categoryId);
        form.setLearningMonth(YearMonth.now().atDay(1));

        String categoryName = categoryService.getCategoryNameById(categoryId);

        model.addAttribute("form", form);
        model.addAttribute("categoryName", categoryName);
        return "skillNew";
    }

    @PostMapping("/skill/new")
    public String saveSkill(
            @ModelAttribute("form") SkillNewForm form,
            Principal principal,
            RedirectAttributes redirectAttributes) {

                Integer userId = userService.findByEmail(principal.getName()).getId();

        LearningData newData = new LearningData();
        newData.setUserId(userId);
        newData.setCategoryId(form.getCategoryId());
        newData.setLearningName(form.getLearningName());
        newData.setLearningTime(form.getLearningTime());
        newData.setLearningMonth(form.getLearningMonth());

        learningDataService.save(newData);

        redirectAttributes.addAttribute("month", newData.getLearningMonth().toString().substring(0, 7));
        return "redirect:/learning/edit";
    }

}
