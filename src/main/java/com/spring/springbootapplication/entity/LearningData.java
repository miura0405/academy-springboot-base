package com.spring.springbootapplication.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "learning_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    


    @Column(name = "learning_name", nullable = false, length = 50)
    private String learningName;

    @Column(name = "learning_month", nullable = false)
    private LocalDate learningMonth;

    @Column(name = "learning_time", nullable = false)
    private Integer learningTime;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Optional: categoryIdを使いたい箇所のためのゲッター（参照専用）
    public Integer getCategoryId() {
        return category != null ? category.getId() : null;
    }
}
