package com.basilisk.api.finance.service;

import com.basilisk.api.finance.dto.response.FinancialCategoryResponse;
import com.basilisk.api.finance.entity.FinancialCategory;
import com.basilisk.api.finance.repository.FinancialCategoryRepository;
import com.basilisk.api.shared.exception.BusinessException;
import com.basilisk.finance.enums.EntryType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FinancialCategoryService {

    private final FinancialCategoryRepository categoryRepository;

    public List<FinancialCategoryResponse> listAll() {
        return categoryRepository.findByActiveTrueOrderByTypeAscNameAsc()
                .stream().map(this::toResponse).toList();
    }

    public List<FinancialCategoryResponse> listByType(EntryType type) {
        return categoryRepository.findByTypeAndActiveTrueOrderByNameAsc(type)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public FinancialCategoryResponse create(String name, String description, EntryType type, String color) {
        if (categoryRepository.existsByNameAndType(name, type)) {
            throw new BusinessException("Categoria '" + name + "' já existe para o tipo " + type, HttpStatus.CONFLICT);
        }
        FinancialCategory saved = categoryRepository.save(
                FinancialCategory.builder()
                        .name(name)
                        .description(description)
                        .type(type)
                        .color(color)
                        .build()
        );
        return toResponse(saved);
    }

    @Transactional
    public FinancialCategoryResponse update(Long id, String name, String description, String color) {
        FinancialCategory category = findById(id);
        if (!category.getName().equals(name)
                && categoryRepository.existsByNameAndType(name, category.getType())) {
            throw new BusinessException("Já existe uma categoria com esse nome para o tipo " + category.getType(), HttpStatus.CONFLICT);
        }
        category.setName(name);
        category.setDescription(description);
        category.setColor(color);
        return toResponse(categoryRepository.save(category));
    }

    @Transactional
    public void deactivate(Long id) {
        FinancialCategory category = findById(id);
        category.setActive(false);
        categoryRepository.save(category);
    }

    public FinancialCategory findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Categoria não encontrada", HttpStatus.NOT_FOUND));
    }

    public FinancialCategoryResponse toResponse(FinancialCategory c) {
        return FinancialCategoryResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .description(c.getDescription())
                .type(c.getType())
                .color(c.getColor())
                .active(c.isActive())
                .createdAt(c.getCreatedAt())
                .build();
    }
}
