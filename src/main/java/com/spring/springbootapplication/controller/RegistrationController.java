package com.spring.springbootapplication.controller;

import com.spring.springbootapplication.dto.UserRegistrationForm;
import com.spring.springbootapplication.entity.User;
import com.spring.springbootapplication.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class RegistrationController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager; // ★ 追加

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userForm", new UserRegistrationForm());
        return "registrationForm";
    }

    @PostMapping("/register")
public String registerUser(
        @Valid @ModelAttribute("userForm") UserRegistrationForm form,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes,
        HttpServletRequest request,
        HttpSession session) {

    if (bindingResult.hasErrors()) {
        return "registrationForm";
    }

    User user = new User();
    user.setName(form.getName());
    user.setEmail(form.getEmail());
    user.setPassword(form.getPassword());

    try {
        userService.registerUser(user);

        UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(user.getEmail(), form.getPassword());

        Authentication authResult = authenticationManager.authenticate(authRequest);
        SecurityContextHolder.getContext().setAuthentication(authResult);

        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
        );

        session.setAttribute("loggedInUser", user);

        return "redirect:/top";

    } catch (IllegalArgumentException e) {
        redirectAttributes.addFlashAttribute("error", e.getMessage());
        return "redirect:/register";
    }
}
}