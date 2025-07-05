package com.spring.springbootapplication.repository;

import com.spring.springbootapplication.entity.LearningData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LearningDataRepository extends JpaRepository<LearningData, Integer> {

    List<LearningData> findByUserIdAndLearningMonth(Integer userId, LocalDate learningMonth);

    List<LearningData> findByUserIdAndLearningMonthAndCategoryId(Integer userId, LocalDate learningMonth, Integer categoryId);

    boolean existsByUserIdAndLearningMonthAndLearningName(Integer userId, LocalDate learningMonth, String learningName);
}
