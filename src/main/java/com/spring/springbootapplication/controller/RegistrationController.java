// package com.spring.springbootapplication.controller;

// import com.spring.springbootapplication.dto.UserRegistrationForm;
// import com.spring.springbootapplication.entity.User;
// import com.spring.springbootapplication.service.UserService;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpSession;
// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.validation.BindingResult;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.ModelAttribute;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// @Controller
// @RequiredArgsConstructor
// public class RegistrationController {

//     private final UserService userService;
//     private final AuthenticationManager authenticationManager;

//     @GetMapping("/register")
//     public String showRegistrationForm(Model model) {
//         model.addAttribute("userForm", new UserRegistrationForm());
//         return "registrationForm";
//     }

//     @PostMapping("/register")
//     public String registerUser(
//         @Valid @ModelAttribute("userForm") UserRegistrationForm form,
//         BindingResult bindingResult,
//         RedirectAttributes redirectAttributes,
//         HttpServletRequest request,
//         HttpSession session) {

//     if (bindingResult.hasErrors()) {
//         return "registrationForm";
//     }

//     User user = new User();
//     user.setName(form.getName());
//     user.setEmail(form.getEmail());
//     user.setPassword(form.getPassword());

//     try {
//         userService.registerUser(user);

//         UsernamePasswordAuthenticationToken authRequest =
//                 new UsernamePasswordAuthenticationToken(user.getEmail(), form.getPassword());

//         Authentication authResult = authenticationManager.authenticate(authRequest);
//         SecurityContextHolder.getContext().setAuthentication(authResult);

//         session.setAttribute(
//                 HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
//                 SecurityContextHolder.getContext()
//         );

//         session.setAttribute("loggedInUser", user);

//         return "redirect:/top";

//     } catch (IllegalArgumentException e) {
//         redirectAttributes.addFlashAttribute("error", e.getMessage());
//         return "redirect:/register";
//     }
// }
// }

package com.spring.springbootapplication.controller;

import com.spring.springbootapplication.dto.UserRegistrationForm;
import com.spring.springbootapplication.entity.User;
import com.spring.springbootapplication.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException; // ←これを追加

import org.slf4j.Logger;                                   // ★ 追加
import org.slf4j.LoggerFactory;                           // ★ 追加

import org.springframework.dao.DataIntegrityViolationException; // ★ 追加
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

    private static final Logger log = LoggerFactory.getLogger(RegistrationController.class); // ★ 追加

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

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
            log.debug("BeanValidation errors: {}", bindingResult.getAllErrors());
            return "registrationForm";
        }

        User user = new User();
        user.setName(form.getName());
        user.setEmail(form.getEmail());
        user.setPassword(form.getPassword());

        try {
            userService.registerUser(user);
            log.info("User saved: email={}, nameLen={}, emailLen={}",
                     user.getEmail(),
                     user.getName() == null ? 0 : user.getName().length(),
                     user.getEmail() == null ? 0 : user.getEmail().length());

            // 自動ログイン
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

        } catch (DataIntegrityViolationException ex) {
            log.error("DB constraint error on register: email={}, nameLen={}, emailLen={}",
                      form.getEmail(),
                      form.getName() == null ? 0 : form.getName().length(),
                      form.getEmail() == null ? 0 : form.getEmail().length(), ex);

            // 画面にエラーを返す（フォームに留まる）
            if (form.getName() != null && form.getName().length() > 255) {
                bindingResult.rejectValue("name", "Size.userForm.name", "氏名は255文字以内で入力してください");
            }
            if (form.getEmail() != null && form.getEmail().length() > 255) {
                bindingResult.rejectValue("email", "Size.userForm.email", "メールアドレスは255文字以内で入力してください");
            }
            return "registrationForm";

        } catch (AuthenticationException ex) {
            log.error("Auto-login failed after registration: email={}", form.getEmail(), ex);
            bindingResult.reject("auth", "登録は完了しましたが自動ログインに失敗しました。ログイン画面から再度ログインしてください。");
            return "registrationForm";

        } catch (IllegalArgumentException e) {
            // 業務例外（重複メールなど）
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }
}