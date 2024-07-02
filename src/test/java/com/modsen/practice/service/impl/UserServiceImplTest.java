package com.modsen.practice.service.impl;

import com.modsen.practice.dto.*;
import com.modsen.practice.entity.User;
import com.modsen.practice.enumeration.Gender;
import com.modsen.practice.enumeration.UserRole;
import com.modsen.practice.exception.UserIsNotExistsException;
import com.modsen.practice.repository.UserRepository;
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

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ConversionService conversionService;

    @Test
    void getById_whenExists() {
        User user = User.builder()
                .id(1L)
                .build();

        UserResponse expected = UserResponse.builder()
                .id(1L)
                .build();

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));
        Mockito.when(conversionService.convert(user, UserResponse.class)).thenReturn(expected);

        UserResponse actual = userServiceImpl.getById(1L);

        assertEquals(expected, actual);
    }

    @Test
    void getById_whenNotExists() {
        Mockito.when(userRepository.findById(1L)).thenThrow(new UserIsNotExistsException(""));

        assertThrows(UserIsNotExistsException.class, () -> userServiceImpl.getById(1L));
    }

    @Test
    void getAll() {
        List<User> userList = new ArrayList<>();
        User user = User.builder()
                .id(1L)
                .build();
        userList.add(user);

        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .build();

        List<UserResponse> expected = new ArrayList<>();
        expected.add(userResponse);

        Mockito.when(userRepository.findAll(PageRequest.of(1, 1, Sort.by("firstname")))).thenReturn(new PageImpl<>(userList));
        Mockito.when(conversionService.convert(userList.get(0), UserResponse.class)).thenReturn(userResponse);

        List<UserResponse> actual = userServiceImpl.getAll(1, 1, "firstname", null);

        assertEquals(expected, actual);
    }

    @Test
    void save() {

        User user = User.builder()
                .firstname("firstname")
                .lastname("lastname")
                .middleName("middlename")
                .gender(Gender.FEMALE)
                .login("login")
                .passwordHash("passwordhash")
                .role(UserRole.ADMIN)
                .phoneNumber("+375333333333")
                .email("test@email.com")
                .build();

        User savedUser = User.builder()
                .id(1L)
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .middleName(user.getMiddleName())
                .gender(user.getGender())
                .login(user.getLogin())
                .passwordHash(user.getPasswordHash())
                .role(user.getRole())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .build();

        UserResponse expected = UserResponse.builder()
                .id(1L)
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .middleName(user.getMiddleName())
                .gender(String.valueOf(user.getGender()))
                .login(user.getLogin())
                .role(String.valueOf(user.getRole()))
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .build();
        Mockito.when(userRepository.existsUserByLogin(user.getLogin())).thenReturn(false);
        Mockito.when(userRepository.existsUserByEmail(user.getEmail())).thenReturn(false);
        Mockito.when(userRepository.save(user)).thenReturn(savedUser);
        Mockito.when(conversionService.convert(savedUser, UserResponse.class)).thenReturn(expected);

        UserResponse actual = userServiceImpl.save(user);

        assertEquals(expected, actual);
    }

    @Test
    void delete_whenExists() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));

        userServiceImpl.delete(1L);

        Mockito.verify(userRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    void delete_whenNotExists() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserIsNotExistsException.class, () -> userServiceImpl.delete(1L));

        Mockito.verify(userRepository, Mockito.times(0)).deleteById(1L);
    }
}