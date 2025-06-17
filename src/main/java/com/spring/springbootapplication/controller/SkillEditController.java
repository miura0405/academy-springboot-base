package com.spring.springbootapplication.controller;

import com.spring.springbootapplication.dto.LearningDataDto;
import com.spring.springbootapplication.dto.LearningDataForm;
import com.spring.springbootapplication.dto.LearningEditForm;
import com.spring.springbootapplication.entity.LearningData;
import com.spring.springbootapplication.service.LearningDataService;
import com.spring.springbootapplication.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
public class SkillEditController {

    private final LearningDataService learningDataService;
    private final UserService userService;

    @GetMapping("/learning/edit")
    public String showEditPage(@RequestParam(name = "month", required = false) String month,
                            Model model, Principal principal) {
        Integer userId = userService.findByEmail(principal.getName()).getId();
    
        YearMonth targetMonth = (month != null) ? YearMonth.parse(month) : YearMonth.now();
        LocalDate firstDayOfMonth = targetMonth.atDay(1);
        
        List<LearningDataDto> backendList = convertToDtoList(
            learningDataService.getByUserIdMonthAndCategory(userId, firstDayOfMonth, 1)
        );
        List<LearningDataDto> frontendList = convertToDtoList(
            learningDataService.getByUserIdMonthAndCategory(userId, firstDayOfMonth, 2)
        );
        List<LearningDataDto> infraList = convertToDtoList(
            learningDataService.getByUserIdMonthAndCategory(userId, firstDayOfMonth, 3)
        );
    
        model.addAttribute("form", new LearningEditForm(backendList, frontendList, infraList, targetMonth));
        model.addAttribute("frontendList", frontendList);
        model.addAttribute("infraList", infraList);
        model.addAttribute("selectableMonths", getLastThreeMonths()); 
        model.addAttribute("targetMonth", targetMonth.toString());
    
        return "skillEdit";
    }
    
    @PostMapping("/learning/edit")
    public String updateLearningData(
            @ModelAttribute("form") LearningEditForm form,
            RedirectAttributes redirectAttributes
    ) {
        learningDataService.updateLearningData(form.getBackendList());
        redirectAttributes.addAttribute("month", form.getTargetMonth().toString());
        return "redirect:/learning/edit";
    }

    private List<LearningDataDto> convertToDtoList(List<LearningData> entityList) {
        return entityList.stream().map(entity -> {
            LearningDataDto dto = new LearningDataDto();
            dto.setId(entity.getId());
            dto.setLearningName(entity.getLearningName());
            dto.setLearningTime(entity.getLearningTime());
            return dto;
        }).collect(Collectors.toList());
    }

    private List<YearMonth> getLastThreeMonths() {
        YearMonth now = YearMonth.now();
        return IntStream.rangeClosed(0, 2)
                .mapToObj(i -> now.minusMonths(i))
                .collect(Collectors.toList());
    }
}
