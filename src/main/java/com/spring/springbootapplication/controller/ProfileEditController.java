package com.spring.springbootapplication.controller;

import com.spring.springbootapplication.dto.EditProfileForm;
import com.spring.springbootapplication.entity.User;
import com.spring.springbootapplication.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ProfileEditController {

    private final UserRepository userRepository;

    @GetMapping("/profile/edit")
    public String showProfileEditPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        EditProfileForm form = new EditProfileForm();
        form.setProfile(user.getProfile());

        model.addAttribute("editProfileForm", form);
        model.addAttribute("user", user);
        return "profileEdit";
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            @ModelAttribute("editProfileForm") @Valid EditProfileForm form,
            BindingResult bindingResult,
            HttpSession session,
            Model model) throws IOException {

        MultipartFile avatarFile = form.getAvatarFile();

        if (!avatarFile.isEmpty()) {
            String contentType = avatarFile.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                bindingResult.rejectValue("avatarFile", "invalid.image", "画像ファイルを選択してください");
            }
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", session.getAttribute("loggedInUser"));
            return "profileEdit";
        }

        User user = (User) session.getAttribute("loggedInUser");
        user.setProfile(form.getProfile());

        if (!avatarFile.isEmpty()) {
            String originalFilename = avatarFile.getOriginalFilename();
        
            String extension = "";
        
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
        
            String newFilename = UUID.randomUUID().toString() + extension;
        
            Path path = Paths.get("uploads/avatars", newFilename);
            Files.createDirectories(path.getParent());
            Files.write(path, avatarFile.getBytes());
        
            user.setAvatar(newFilename);
        }
        
        

        userRepository.save(user);
        session.setAttribute("loggedInUser", user);

        return "redirect:/top";
    }
}
