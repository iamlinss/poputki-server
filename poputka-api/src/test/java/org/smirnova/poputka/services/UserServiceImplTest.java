package org.smirnova.poputka.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.smirnova.poputka.domain.entities.UserEntity;
import org.smirnova.poputka.repositories.UserRepository;
import org.smirnova.poputka.services.impl.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_ShouldSaveAndReturnUser() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setEmail("test@example.com");

        when(userRepository.save(user)).thenReturn(user);

        UserEntity result = userService.save(user);

        assertThat(result).isEqualTo(user);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void findAll_ShouldReturnAllUsers() {
        List<UserEntity> users = List.of(new UserEntity(), new UserEntity());
        when(userRepository.findAll()).thenReturn(users);

        List<UserEntity> result = userService.findAll();

        assertThat(result).hasSize(2);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void findOne_ShouldReturnOptionalUser_WhenUserExists() {
        UserEntity user = new UserEntity();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<UserEntity> result = userService.findOne(1L);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(user);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void findOne_ShouldReturnEmptyOptional_WhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<UserEntity> result = userService.findOne(1L);

        assertThat(result).isEmpty();
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void isExists_ShouldReturnTrue_WhenUserExists() {
        when(userRepository.existsById(1L)).thenReturn(true);

        boolean result = userService.isExists(1L);

        assertThat(result).isTrue();
        verify(userRepository, times(1)).existsById(1L);
    }

    @Test
    void isExists_ShouldReturnFalse_WhenUserDoesNotExist() {
        when(userRepository.existsById(1L)).thenReturn(false);

        boolean result = userService.isExists(1L);

        assertThat(result).isFalse();
        verify(userRepository, times(1)).existsById(1L);
    }

    @Test
    void isEmailExists_ShouldReturnTrue_WhenEmailExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new UserEntity()));

        boolean result = userService.isEmailExists("test@example.com");

        assertThat(result).isTrue();
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void isEmailExists_ShouldReturnFalse_WhenEmailDoesNotExist() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        boolean result = userService.isEmailExists("test@example.com");

        assertThat(result).isFalse();
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }
}
