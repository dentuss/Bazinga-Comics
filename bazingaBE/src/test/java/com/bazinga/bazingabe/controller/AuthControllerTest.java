package com.bazinga.bazingabe.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bazinga.bazingabe.dto.AuthRequest;
import com.bazinga.bazingabe.entity.User;
import com.bazinga.bazingabe.repository.UserRepository;
import com.bazinga.bazingabe.service.JwtService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthController authController;

    @Test
    void registerCreatesNewUserWithToken() {
        AuthRequest request = new AuthRequest();
        request.setEmail("new-user@example.com");
        request.setUsername("newUser");
        request.setPassword("password123");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("hashed-password");
        when(jwtService.generateToken(request.getEmail())).thenReturn("jwt-token");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        var response = authController.register(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("jwt-token", response.getBody().getToken());
        assertEquals(1L, response.getBody().getUserId());
        assertEquals("newUser", response.getBody().getUsername());
        assertEquals("new-user@example.com", response.getBody().getEmail());
    }

    @Test
    void registerReturnsBadRequestWhenUserExists() {
        AuthRequest request = new AuthRequest();
        request.setEmail("existing@example.com");
        request.setUsername("existing");
        request.setPassword("password123");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(new User()));

        var response = authController.register(request);

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void loginAuthenticatesAndReturnsToken() {
        AuthRequest request = new AuthRequest();
        request.setEmail("user@example.com");
        request.setPassword("password123");

        User user = new User();
        user.setId(5L);
        user.setUsername("user");
        user.setEmail(request.getEmail());

        Authentication authentication = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(request.getEmail())).thenReturn("jwt-token");

        var response = authController.login(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("jwt-token", response.getBody().getToken());
        assertEquals(5L, response.getBody().getUserId());
        assertEquals("user", response.getBody().getUsername());
        assertEquals("user@example.com", response.getBody().getEmail());
        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(authenticationManager).authenticate(any(Authentication.class));
    }
}
