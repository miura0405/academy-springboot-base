package com.spring.springbootapplication.service;

import com.spring.springbootapplication.entity.Category;
import com.spring.springbootapplication.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public String getCategoryNameById(Integer id) {
        return categoryRepository.findById(id)
                .map(Category::getName)
                .orElse("不明なカテゴリ");
    }

    public Category findById(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found: id = " + id));
    }
    
}
