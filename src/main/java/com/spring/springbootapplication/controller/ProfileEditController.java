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
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.security.Principal; // ★ 追加

@Controller
@RequiredArgsConstructor
public class ProfileEditController {

    private final UserRepository userRepository;

    @Value("${app.upload.base:/app/uploads}")
    private String uploadBase;

    @GetMapping("/profile/edit")
    public String showProfileEditPage(HttpSession session, Model model, Principal principal) { // ★ Principal 受け取り
        User user = (User) session.getAttribute("loggedInUser");

        // ★ セッションに無ければ、認証済みユーザー(= email)から補完
        if (user == null && principal != null) {
            user = userRepository.findByEmail(principal.getName()).orElse(null);
            if (user != null) {
                session.setAttribute("loggedInUser", user);
            }
        }

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
            Model model,
            Principal principal) throws IOException { // ★ Principal 受け取り

        MultipartFile avatarFile = form.getAvatarFile();

        if (!avatarFile.isEmpty()) {
            String contentType = avatarFile.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                bindingResult.rejectValue("avatarFile", "invalid.image", "画像ファイルを選択してください");
            }
        }

        // ★ ユーザー解決（セッション→Principalの順）
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null && principal != null) {
            user = userRepository.findByEmail(principal.getName()).orElse(null);
            if (user != null) {
                session.setAttribute("loggedInUser", user);
            }
        }
        if (user == null) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            return "profileEdit";
        }

        user.setProfile(form.getProfile());

        // 保存先を /app/uploads/avatars に統一（ホストは /var/app/uploads/avatars にバインド）
        if (!avatarFile.isEmpty()) {
            String originalFilename = avatarFile.getOriginalFilename();
            String extension = "";
            if (originalFilename != null) {
                int dot = originalFilename.lastIndexOf('.');
                if (dot >= 0) {
                    extension = originalFilename.substring(dot);
                }
            }
            String newFilename = UUID.randomUUID().toString() + extension;

            Path dir = Paths.get(uploadBase, "avatars");
            Files.createDirectories(dir);

            Path path = dir.resolve(newFilename);
            Files.write(path, avatarFile.getBytes());

            user.setAvatar(newFilename);
        }

        userRepository.save(user);
        session.setAttribute("loggedInUser", user);

        return "redirect:/top";
    }
}
