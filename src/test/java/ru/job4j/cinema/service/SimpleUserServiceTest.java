package ru.job4j.cinema.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SimpleUserServiceTest {
    private SimpleUserService simpleUserService;
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userRepository = mock(UserRepository.class);
        simpleUserService = new SimpleUserService(userRepository);
    }

    @Test
    public void whenSaveThenGetSame() {
        when(userRepository.save(any(User.class)))
                .thenReturn(Optional.of(new User(0, "name", "name@example.com", "password")));
        User expected = new User(0, "name", "name@example.com", "password");

        Optional<User> actualUser = simpleUserService.save(new User());

        assertThat(actualUser).usingRecursiveComparison().isEqualTo(Optional.of(expected));
    }

    @Test
    public void whenFindByEmailAndPasswordThenGet() {
        when(userRepository.findByEmailAndPassword(anyString(), anyString()))
                .thenReturn(Optional.of(new User(0, "name", "name@example.com", "password")));
        User expected = new User(0, "name", "name@example.com", "password");

        Optional<User> actualUser = simpleUserService.findByEmailAndPassword("", "");

        assertThat(actualUser).usingRecursiveComparison().isEqualTo(Optional.of(expected));
    }

    @Test
    public void whenFindByIdThenGet() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(new User(0, "name", "name@example.com", "password")));
        User expected = new User(0, "name", "name@example.com", "password");

        Optional<User> actualUser = simpleUserService.findById(1);

        assertThat(actualUser).usingRecursiveComparison().isEqualTo(Optional.of(expected));
    }
}