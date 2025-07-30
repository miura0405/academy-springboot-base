package com.spring.springbootapplication.repository;

import com.spring.springbootapplication.entity.LearningData;
import com.spring.springbootapplication.dto.ChartDataDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LearningDataRepository extends JpaRepository<LearningData, Integer> {

    List<LearningData> findByUserIdAndLearningMonthAndCategory_IdOrderByIdAsc(
        Integer userId, LocalDate learningMonth, Integer categoryId);
    
    // List<LearningData> findByUserIdAndLearningMonthAndCategoryId(
    //     Integer userId, LocalDate learningMonth, Integer categoryId);

    boolean existsByUserIdAndLearningMonthAndLearningName(
        Integer userId, LocalDate learningMonth, String learningName);

        @Query("SELECT new com.spring.springbootapplication.dto.ChartDataDto(" +
        "c.name, l.learningMonth, SUM(l.learningTime)) " +
        "FROM LearningData l " +
        "JOIN l.category c " +
        "WHERE l.user.id = :userId " +
        "AND l.learningMonth IN :targetMonths " +
        // "AND l.deleted = false " +
        "GROUP BY c.name, l.learningMonth " +
        "ORDER BY l.learningMonth ASC")

    List<ChartDataDto> findChartDataByUserIdAndMonths(
    @Param("userId") Integer userId,
    @Param("targetMonths") List<LocalDate> targetMonths);
}
