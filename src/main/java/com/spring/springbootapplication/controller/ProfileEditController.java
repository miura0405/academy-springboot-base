package com.spring.springbootapplication.controller;

import com.spring.springbootapplication.dto.EditProfileForm;
import com.spring.springbootapplication.entity.User;
import com.spring.springbootapplication.repository.UserRepository;
import com.spring.springbootapplication.service.storage.StorageService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal; // 認証済みユーザーの解決に使用

@Controller
@RequiredArgsConstructor
public class ProfileEditController {

    private final UserRepository userRepository;
    private final StorageService storageService;

    @GetMapping("/profile/edit")
    public String showProfileEditPage(HttpSession session, Model model, Principal principal) { // S3/既存画像の表示用URLも組み立てる
        User user = (User) session.getAttribute("loggedInUser");

        // セッションにユーザーが無い場合は、認証情報の email から補完する
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
        model.addAttribute("avatarUrl", storageService.resolveAvatarUrl(user.getAvatar()));
        return "profileEdit";
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            @ModelAttribute("editProfileForm") @Valid EditProfileForm form,
            BindingResult bindingResult,
            HttpSession session,
            Model model,
            Principal principal) throws IOException { // アップロードは StorageService に委譲する

        MultipartFile avatarFile = form.getAvatarFile();

        if (!avatarFile.isEmpty()) {
            String contentType = avatarFile.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                bindingResult.rejectValue("avatarFile", "invalid.image", "画像ファイルを選択してください");
            }
        }

        // 更新対象ユーザーはセッション優先で解決し、無ければ認証情報から補完する
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
            model.addAttribute("avatarUrl", storageService.resolveAvatarUrl(user.getAvatar()));
            return "profileEdit";
        }

        user.setProfile(form.getProfile());

        if (!avatarFile.isEmpty()) {
            // 新規アップロード分は S3 に保存し、戻り値のキーを avatar カラムに保持する
            user.setAvatar(storageService.uploadAvatar(avatarFile));
        }

        userRepository.save(user);
        session.setAttribute("loggedInUser", user);

        return "redirect:/top";
    }
}
