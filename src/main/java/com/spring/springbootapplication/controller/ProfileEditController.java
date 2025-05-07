package com.spring.springbootapplication.controller;

import com.spring.springbootapplication.entity.User;
import com.spring.springbootapplication.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequiredArgsConstructor
public class ProfileEditController {

    private final UserRepository userRepository;

    @GetMapping("/profile/edit")
    public String showProfileEditPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        
        if (user == null) {
            // 例えばログイン画面へリダイレクト
            return "redirect:/login";
        }
    
        model.addAttribute("user", user);
        return "profileEdit";
    }
    
    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam("profile") String profile,
                                @RequestParam("avatarFile") MultipartFile avatarFile,
                                HttpSession session) throws IOException {

        User user = (User) session.getAttribute("loggedInUser");

        if (!avatarFile.isEmpty()) {
            String filename = avatarFile.getOriginalFilename();
            Path path = Paths.get("src/main/resources/static/images/avatars", filename);
            Files.write(path, avatarFile.getBytes());
            user.setAvatar(filename);
        }

        user.setProfile(profile);
        userRepository.save(user);

        return "redirect:/top";
    }
}
