package me.dio.service.impl;

import me.dio.domain.model.*;
import me.dio.domain.repository.UserRepository;
import me.dio.service.exception.BusinessException;
import me.dio.service.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {

        Feature feature = new Feature();
        feature.setId(1L);
        feature.setDescription("feature");
        feature.setIcon("icon");

        News news = new News();
        news.setId(1L);
        news.setDescription("news");
        news.setIcon("icon");

        Account account = new Account();
        account.setId(1L);
        account.setAgency("agencia");
        account.setBalance(BigDecimal.valueOf(100.00));
        account.setLimit(BigDecimal.valueOf(2000));
        account.setNumber("123");

        Card card = new Card();
        card.setLimit(BigDecimal.valueOf(1000));
        card.setNumber("123-123");

        user = new User();
//        user.setId(1L);
        user.setNews(List.of(news));
        user.setFeatures(List.of(feature));
        user.setAccount(account);
        user.setName("name");
        user.setCard(card);

        // Set other necessary fields for the user object
    }

    @Test
    void findAll_ShouldReturnListOfUsers() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        var result = userService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void findById_ShouldReturnUser_WhenUserExists() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        var result = userService.findById(2L);

        assertNotNull(result);
        assertEquals(user, result);
        verify(userRepository, times(1)).findById(2L);
    }

    @Test
    void findById_ShouldThrowNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findById(2L));
        verify(userRepository, times(1)).findById(2L);
    }

    @Test
    void create_ShouldReturnCreatedUser() {
        when(userRepository.existsByAccountNumber(any())).thenReturn(false);
        when(userRepository.existsByCardNumber(any())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        var result = userService.create(user);

        assertNotNull(result);
        assertEquals(user, result);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void create_ShouldThrowBusinessException_WhenUserIsNull() {
        assertThrows(BusinessException.class, () -> userService.create(null));
    }

    @Test
    void create_ShouldThrowBusinessException_WhenAccountIsNull() {
        user.setAccount(null);
        assertThrows(BusinessException.class, () -> userService.create(user));
    }

    @Test
    void create_ShouldThrowBusinessException_WhenCardIsNull() {
        user.setCard(null);
        assertThrows(BusinessException.class, () -> userService.create(user));
    }

    @Test
    void update_ShouldReturnUpdatedUser() {
        user.setId(2L);
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        var result = userService.update(2L, user);

        assertNotNull(result);
        assertEquals(user, result);
        verify(userRepository, times(1)).findById(2L);
        verify(userRepository, times(1)).save(user);
    }


    @Test
    void delete_ShouldDeleteUser() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        userService.delete(2L);

        verify(userRepository, times(1)).findById(2L);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void validateChangeableId_ShouldThrowBusinessException_WhenIdIsUnchangeable() {
        var exception = assertThrows(BusinessException.class, () -> userService.delete(1L));
        assertEquals("User with ID 1 can not be deleted.", exception.getMessage());
    }
}
