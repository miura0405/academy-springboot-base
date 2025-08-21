package com.spring.springbootapplication.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication;


@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginPage(
        @RequestParam(value = "error", required = false) String error,
        @RequestParam(value = "logout", required = false) String logout,
        Model model,
        Authentication authentication 
    ) {
    if (authentication != null && authentication.isAuthenticated()
        && !(authentication instanceof AnonymousAuthenticationToken)) {
        return "redirect:/top";
    }

    if (error != null) {
        model.addAttribute("errorMessage", "メールアドレス、もしくはパスワードが間違っています");
    }
    if (logout != null) {
        model.addAttribute("logoutMessage", "ログアウトしました。");
    }

    return "login";
}

}
