package com.spring.springbootapplication.controller;

import com.spring.springbootapplication.dto.ChartDataDto;
import com.spring.springbootapplication.repository.LearningDataRepository;
import com.spring.springbootapplication.entity.User;
import com.spring.springbootapplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class SkillChartController {

    private final LearningDataRepository learningDataRepository;
    private final UserService userService;

    @Autowired
    public SkillChartController(LearningDataRepository learningDataRepository, UserService userService) {
        this.learningDataRepository = learningDataRepository;
        this.userService = userService;
    }

    @GetMapping("/top")
    public String showTopPage(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();
        User user = userService.findByEmail(email);
        
        if (user == null) {
            return "redirect:/login";
        }

        LocalDate now = LocalDate.now();
        List<LocalDate> targetMonths = new ArrayList<>();
        targetMonths.add(now.withDayOfMonth(1)); // 今月
        targetMonths.add(now.minusMonths(1).withDayOfMonth(1)); // 先月
        targetMonths.add(now.minusMonths(2).withDayOfMonth(1)); // 先々月

        List<ChartDataDto> chartDataList = learningDataRepository.findChartDataByUserIdAndMonths(user.getId(), targetMonths);

        chartDataList.sort(Comparator
                .comparing(ChartDataDto::getLearningMonth)
                .thenComparing(ChartDataDto::getCategoryName));

        Map<String, List<Integer>> categoryToValues = new LinkedHashMap<>();
        List<String> months = getFormattedMonths(targetMonths);

        for (ChartDataDto dto : chartDataList) {
            categoryToValues.putIfAbsent(dto.getCategoryName(), Arrays.asList(0, 0, 0));
        }

        for (ChartDataDto dto : chartDataList) {
            int index = months.indexOf(dto.getFormattedMonth());
            if (index != -1) {
                List<Integer> values = new ArrayList<>(categoryToValues.get(dto.getCategoryName()));
                values.set(index, dto.getTotalLearningTime());
                categoryToValues.put(dto.getCategoryName(), values);
            }
        }

        model.addAttribute("months", months);
        model.addAttribute("categoryToValues", categoryToValues);
        model.addAttribute("user", user);

        return "topPage";
    }

    private List<String> getFormattedMonths(List<LocalDate> dates) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        return dates.stream()
                .map(date -> date.format(formatter))
                .toList();
    }
}