package com.modsen.practice.service.impl;

import com.modsen.practice.dto.*;
import com.modsen.practice.entity.Category;
import com.modsen.practice.exception.CategoryIsNotExistsException;
import com.modsen.practice.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @InjectMocks
    private CategoryServiceImpl categoryServiceImpl;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ConversionService conversionService;

    @Test
    void getById_whenExists() {
        Category category = Category.builder()
                .id(1L)
                .build();

        CategoryResponse expected = CategoryResponse.builder()
                .id(1L)
                .build();

        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.ofNullable(category));
        Mockito.when(conversionService.convert(category, CategoryResponse.class)).thenReturn(expected);

        CategoryResponse actual = categoryServiceImpl.getById(1L);

        assertEquals(expected, actual);
    }

    @Test
    void getById_whenNotExists() {
        Mockito.when(categoryRepository.findById(1L)).thenThrow(new CategoryIsNotExistsException(""));

        assertThrows(CategoryIsNotExistsException.class, () -> categoryServiceImpl.getById(1L));
    }

    @Test
    void getAll() {
        List<Category> categoryList = new ArrayList<>();
        Category category = Category.builder()
                .id(1L)
                .build();
        categoryList.add(category);

        CategoryResponse categoryResponse = CategoryResponse.builder()
                .id(1L)
                .build();

        List<CategoryResponse> expected = new ArrayList<>();
        expected.add(categoryResponse);

        Mockito.when(categoryRepository.findAll(PageRequest.of(1, 1, Sort.by("name")))).thenReturn(new PageImpl<>(categoryList));
        Mockito.when(conversionService.convert(categoryList.get(0), CategoryResponse.class)).thenReturn(categoryResponse);

        List<CategoryResponse> actual = categoryServiceImpl.getAll(1, 1, "name", null);

        assertEquals(expected, actual);
    }

    @Test
    void save() {
        CategoryRequest request = CategoryRequest.builder()
                .name("test")
                .build();


        Category category = Category.builder()
                .name(request.getName())
                .build();

        Category savedCategory = Category.builder()
                .id(1L)
                .name(request.getName())
                .build();

        CategoryResponse expected = CategoryResponse.builder()
                .id(1L)
                .name(request.getName())
                .build();

        Mockito.when(conversionService.convert(request, Category.class)).thenReturn(category);
        Mockito.when(categoryRepository.save(category)).thenReturn(savedCategory);
        Mockito.when(conversionService.convert(savedCategory, CategoryResponse.class)).thenReturn(expected);

        CategoryResponse actual = categoryServiceImpl.save(request);

        assertEquals(expected, actual);
    }

    @Test
    void delete_whenExists() {
        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.of(new Category()));


        categoryServiceImpl.delete(1L);

        Mockito.verify(categoryRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    void delete_whenNotExists() {
        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CategoryIsNotExistsException.class, () -> categoryServiceImpl.delete(1L));

        Mockito.verify(categoryRepository, Mockito.times(0)).deleteById(1L);
    }
}