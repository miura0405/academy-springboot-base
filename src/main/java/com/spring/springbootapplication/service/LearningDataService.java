package com.spring.springbootapplication.service;

import com.spring.springbootapplication.entity.LearningData;
import com.spring.springbootapplication.repository.LearningDataRepository;
import com.spring.springbootapplication.dto.LearningDataDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LearningDataService {

    private final LearningDataRepository learningDataRepository;

    public List<LearningData> getByUserIdMonthAndCategory(Integer userId, LocalDate month, Integer categoryId) {
        return learningDataRepository.findByUserIdAndLearningMonthAndCategoryIdOrderByIdAsc(userId, month, categoryId);
    }
    

    // public List<LearningData> getByUserIdMonthAndCategory(Integer userId, LocalDate month, Integer categoryId) {
    //     return learningDataRepository.findByUserIdAndLearningMonthAndCategoryId(userId, month, categoryId);
    // }

    public void updateLearningData(List<LearningDataDto> dtoList) {
        for (LearningDataDto dto : dtoList) {
            LearningData entity = learningDataRepository.findById(dto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("IDが存在しません: " + dto.getId()));
            entity.setLearningTime(dto.getLearningTime());
            learningDataRepository.save(entity);
        }
    }

    public void save(LearningData data) {
        learningDataRepository.save(data);
    }

    public boolean existsByNameAndMonthAndUserId(String name, LocalDate month, Integer userId) {
        return learningDataRepository.existsByUserIdAndLearningMonthAndLearningName(userId, month, name);
    }
    
    public void updateLearningTime(Integer id, Integer learningTime) {
        LearningData data = learningDataRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("対象の学習データが見つかりません"));
        data.setLearningTime(learningTime);
        learningDataRepository.save(data);
    }
}
