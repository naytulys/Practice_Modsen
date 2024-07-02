package com.modsen.practice.auth;

import com.modsen.practice.auth.jwt.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.practice.dto.UserResponse;
import com.modsen.practice.entity.User;
import com.modsen.practice.enumeration.Gender;
import com.modsen.practice.enumeration.UserRole;
import com.modsen.practice.repository.UserRepository;
import com.modsen.practice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Date;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserVODetailsService userVODetailsService;

    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .middleName(request.getMiddleName())
                .gender(request.getGender())
                .phoneNumber(request.getPhoneNumber())
                .birthDate(request.getBirthDate())
                .email(request.getEmail())
                .login(request.getLogin())
                .passwordHash(passwordEncoder.encode(request.getPasswordHash()))
                .role(UserRole.CUSTOMER)
                .build();
        UserResponse userResponse = userService.save(user);
        var accessToken = jwtService.generateToken(new UserVODetails(user));
        var refreshToken = jwtService.generateRefreshToken(new UserVODetails(user));
        return AuthenticationResponse.builder()
                .userId(userResponse.getId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .role(UserRole.CUSTOMER.toString())
                .userData(user.getEmail()).build();
    }
    public AuthenticationResponse authenticate(AuthenticationRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUserData(),
                        request.getPassword()
                )
        );
        var userVODetails = userVODetailsService.loadUserByUsername(request.getUserData());
        var accessToken = jwtService.generateToken(userVODetails);
        var refreshToken = jwtService.generateRefreshToken(userVODetails);
        return AuthenticationResponse.builder()
                .userId(userVODetails.getUser().getId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .role(userVODetails.getAuthorities().toString())
                .userData(userVODetails.getUsername()).build();
    }
    @SneakyThrows
    public void refreshToken(HttpServletRequest request,
                             HttpServletResponse response) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String username;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        username = jwtService.extractUsername(refreshToken);
        if (username != null) {
            var userVODetails =  userVODetailsService.loadUserByUsername(username);
            if (jwtService.isTokenValid(refreshToken, userVODetails)) {
                var accessToken = jwtService.generateToken(userVODetails);
                var authResponse = AuthenticationResponse.builder()
                        .userId(userVODetails.getUser().getId())
                        .userData(username)
                        .role(userVODetails.getAuthorities().toString())
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                response.setContentType("application/json");
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}
