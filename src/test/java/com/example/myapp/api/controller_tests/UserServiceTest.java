package com.example.myapp.api.controller_tests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.example.myapp.entity.Patient;
import com.example.myapp.entity.User;
import com.example.myapp.repository.UserRepository;
import com.example.myapp.services.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private Patient user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new Patient();
        user.setId((long) 1);
        user.setFirstName("Test");
        user.setIsActivated(false);
    }

    @Test
    void updateUserStatus_Success() throws Exception {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        // llamamos
        userService.updateUserStatus(1, true);
        // verificamos
        assertTrue(user.getIsActivated());
        verify(userRepository).save(user);
    }

    @Test
    void updateUserStatus_UserNotFound_Throws() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        Exception ex = assertThrows(Exception.class, () -> {
            userService.updateUserStatus(99, true);
        });

        assertEquals("Usuario no encontrado", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void getAllUsers_ReturnsList() {
        Patient u2 = new Patient();
        u2.setId((long) 2);
        u2.setFirstName("Otro");
        List<User> mockList = Arrays.asList(user, u2);

        when(userRepository.findAll()).thenReturn(mockList);

        List<User> result = userService.getAllUsers();
        assertEquals(2, result.size());
        assertEquals("Test", result.get(0).getFirstName());
        verify(userRepository).findAll();
    }
}
