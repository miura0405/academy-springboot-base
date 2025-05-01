package com.spring.springbootapplication.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SkillEditController {
    @GetMapping("/learning/edit")
    public String showSkillEditPage() {
        return "skillEdit"; // templates/skillEdit.html を表示
    }
}
