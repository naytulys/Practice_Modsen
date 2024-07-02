package com.modsen.practice.service.impl;

import com.modsen.practice.dto.CategoryResponse;
import com.modsen.practice.dto.ProductRequest;
import com.modsen.practice.dto.ProductResponse;
import com.modsen.practice.entity.Category;
import com.modsen.practice.entity.Product;
import com.modsen.practice.exception.product.ProductIsNotExistsException;
import com.modsen.practice.repository.ProductRepository;
import com.modsen.practice.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {
    @InjectMocks
    private ProductServiceImpl productServiceImpl;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ConversionService conversionService;

    @Test
    void getById_whenExists() {
        Product product = Product.builder()
                .id(1L)
                .build();

        ProductResponse expected = ProductResponse.builder()
                .id(1L)
                .build();

        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.ofNullable(product));
        Mockito.when(conversionService.convert(product, ProductResponse.class)).thenReturn(expected);

        ProductResponse actual = productServiceImpl.getById(1L);

        assertEquals(expected, actual);
    }

    @Test
    void getById_whenNotExists() {
        Mockito.when(productRepository.findById(1L)).thenThrow(new ProductIsNotExistsException(""));

        assertThrows(ProductIsNotExistsException.class, () -> productServiceImpl.getById(1L));
    }

    @Test
    void getAll() {
        List<Product> productList = new ArrayList<>();
        Product product = Product.builder()
                .id(1L)
                .build();
        productList.add(product);

        ProductResponse productResponse = ProductResponse.builder()
                .id(1L)
                .build();

        List<ProductResponse> expected = new ArrayList<>();
        expected.add(productResponse);

        Mockito.when(productRepository.findAll(PageRequest.of(1, 1, Sort.by("desc")))).thenReturn(new PageImpl<>(productList));
        Mockito.when(conversionService.convert(productList.get(0), ProductResponse.class)).thenReturn(productResponse);

        List<ProductResponse> actual = productServiceImpl.getAll(1, 1, "desc", null);

        assertEquals(expected, actual);
    }

    @Test
    void getAllByCategoryId() {
        List<Product> productList = new ArrayList<>();
        Product product = Product.builder()
                .id(1L)
                .build();
        productList.add(product);

        CategoryResponse categoryResponse = CategoryResponse.builder()
                .id(1L)
                .build();

        ProductResponse productResponse = ProductResponse.builder()
                .id(1L)
                .category(categoryResponse)
                .build();

        List<ProductResponse> expected = new ArrayList<>();
        expected.add(productResponse);

        Mockito.when(productRepository.findByCategory_id(1L, PageRequest.of(1, 1, Sort.by("desc")))).thenReturn(productList);
        Mockito.when(conversionService.convert(productList.get(0), ProductResponse.class)).thenReturn(productResponse);

        List<ProductResponse> actual = productServiceImpl.getAllByCategoryId(1L, 1, 1, "desc", null);

        assertEquals(expected, actual);
    }

    @Test
    void save() {
        ProductRequest productRequest = ProductRequest.builder()
                .id(1L)
                .name("pizza")
                .categoryId(1L)
                .ingredients("ing")
                .description("food")
                .price(BigDecimal.valueOf(1.11))
                .weight((short) 100)
                .caloricValue((short) 100)
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("pizza")
                .category(Category.builder()
                        .id(1L)
                        .name("dodo")
                        .build())
                .ingredients("ing")
                .description("food")
                .price(BigDecimal.valueOf(1.11))
                .weight((short) 100)
                .caloricValue((short) 100)
                .build();

        CategoryResponse categoryResponse = CategoryResponse.builder()
                .id(1L)
                .name("dodo")
                .build();

        Category category = Category.builder()
                .id(1L)
                .name("dodo")
                .build();

        ProductResponse expected = ProductResponse.builder()
                .id(1L)
                .name("pizza")
                .category(categoryResponse)
                .ingredients("ing")
                .description("food")
                .price(BigDecimal.valueOf(1.11))
                .weight((short) 100)
                .caloricValue((short) 100)
                .build();
        Mockito.when(categoryService.getById(1L)).thenReturn(categoryResponse);
        Mockito.when(conversionService.convert(productRequest, Product.class)).thenReturn(product);
        Mockito.when(modelMapper.map(categoryResponse, Category.class)).thenReturn(category);
        Mockito.when(productRepository.save(product)).thenReturn(product);
        product.setId(1L);
        Mockito.when(conversionService.convert(product, ProductResponse.class)).thenReturn(expected);

        ProductResponse actual = productServiceImpl.save(productRequest);

        assertEquals(expected, actual);
    }

    @Test
    void update() {
        ProductRequest productRequest = ProductRequest.builder()
                .id(1L)
                .name("pizza")
                .categoryId(1L)
                .ingredients("ing")
                .description("food")
                .price(BigDecimal.valueOf(1.11))
                .weight((short) 100)
                .caloricValue((short) 100)
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("pizza")
                .category(Category.builder()
                        .id(1L)
                        .name("dodo")
                        .build())
                .ingredients("ing")
                .description("food")
                .price(BigDecimal.valueOf(1.11))
                .weight((short) 100)
                .caloricValue((short) 100)
                .build();

        CategoryResponse categoryResponse = CategoryResponse.builder()
                .id(1L)
                .name("dodo")
                .build();

        Category category = Category.builder()
                .id(1L)
                .name("dodo")
                .build();

        ProductResponse expected = ProductResponse.builder()
                .id(1L)
                .name("pizza")
                .category(categoryResponse)
                .ingredients("ing")
                .description("food")
                .price(BigDecimal.valueOf(1.11))
                .weight((short) 100)
                .caloricValue((short) 100)
                .build();
        Mockito.when(productRepository.findById(productRequest.getId())).thenReturn(Optional.ofNullable(product));
        Mockito.when(categoryService.getById(1L)).thenReturn(categoryResponse);
        Mockito.when(conversionService.convert(productRequest, Product.class)).thenReturn(product);
        Mockito.when(modelMapper.map(categoryResponse, Category.class)).thenReturn(category);
        Mockito.when(productRepository.save(product)).thenReturn(product);
        product.setId(1L);
        Mockito.when(conversionService.convert(product, ProductResponse.class)).thenReturn(expected);

        ProductResponse actual = productServiceImpl.update(productRequest);

        assertEquals(expected, actual);
    }

    @Test
    void delete_whenExists() {
        Mockito.when(productRepository.existsOrderItemById(1L)).thenReturn(true);

        productServiceImpl.delete(1L);

        Mockito.verify(productRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    void delete_whenNotExists() {
        Mockito.when(productRepository.existsOrderItemById(1L)).thenReturn(false);

        assertThrows(ProductIsNotExistsException.class, () -> productServiceImpl.delete(1L));

        Mockito.verify(productRepository, Mockito.times(0)).deleteById(1L);
    }
}