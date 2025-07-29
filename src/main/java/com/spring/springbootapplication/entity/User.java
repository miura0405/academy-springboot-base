package com.spring.springbootapplication.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.spring.springbootapplication.validation.ProfileEditGroup;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;



@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User implements UserDetails{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(columnDefinition = "TEXT")
    @Size(min = 50, max = 200, message = "自己紹介は50文字以上200文字以下で入力してください", groups = ProfileEditGroup.class)
    @NotBlank(message = "自己紹介は50文字以上200文字以下で入力してください", groups = ProfileEditGroup.class)
    private String profile;


    @Column(length = 255)
    private String avatar;

    @Column(nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 権限は "ROLE_USER" 固定
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        // Spring Security 上での識別子（username）を email にします
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // アカウントの有効期限を使用しない場合は true 固定
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // ロック機能を使用しないなら true
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 資格情報の有効期限を使用しないなら true
    }

    @Override
    public boolean isEnabled() {
        return true; // ユーザーを常に有効として扱うなら true
    }
}
