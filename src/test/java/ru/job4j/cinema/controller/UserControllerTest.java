package ru.job4j.cinema.controller;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.UserService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserControllerTest {
    private UserService userService;
    private UserController userController;

    @BeforeEach
    public void setUp() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
    }

    @Test
    public void whenGetRegistrationPageThenGetPage() {
        String view = userController.getRegistrationPage();

        assertThat(view).isEqualTo("users/register");
    }

    @Test
    public void whenRegisterUserSuccessThenRedirectIndexPageWithUserInSession() {
        User user = new User();
        when(userService.save(any(User.class))).thenReturn(Optional.of(user));
        ConcurrentModel model = new ConcurrentModel();
        MockHttpSession session = new MockHttpSession();

        String view = userController.register(user, model, session);
        User userSession = (User) session.getAttribute("user");

        assertThat(view).isEqualTo("redirect:/");
        assertThat(userSession).isEqualTo(user);
    }

    @Test
    public void whenRegisterUserNotSaveThenGetPageRegisterUserWithMessage() {
        User user = new User();
        ConcurrentModel model = new ConcurrentModel();
        MockHttpSession session = new MockHttpSession();

        String view = userController.register(user, model, session);
        String error = (String) model.getAttribute("error");

        assertThat(view).isEqualTo("users/register");
        assertThat(error).isEqualTo("Пользователь с такой почтой уже существует.");
    }

    @Test
    public void whenGetLoginPageThenGetPage() {
        String view = userController.getLoginPage();

        assertThat(view).isEqualTo("users/login");
    }

    @Test
    public void whenLoginUserRightThenRedirectIndexPageWithUserInSession() {
        User user = new User(1, "Ivan", "name@example.com", "password");
        when(userService.findByEmailAndPassword(user.getEmail(), user.getPassword())).thenReturn(Optional.of(user));
        ConcurrentModel model = new ConcurrentModel();
        MockHttpSession session = new MockHttpSession();

        String view = userController.loginUser(user, model, session);
        User userSession = (User) session.getAttribute("user");

        assertThat(view).isEqualTo("redirect:/");
        assertThat(userSession).isEqualTo(user);
    }

    @Test
    public void whenLoginUserNotRightThenGetLoginPageWithErrorMessage() {
        User user = new User();
        when(userService.findByEmailAndPassword(user.getEmail(), user.getPassword())).thenReturn(Optional.empty());
        ConcurrentModel model = new ConcurrentModel();
        MockHttpSession session = new MockHttpSession();

        String view = userController.loginUser(user, model, session);
        String error = (String) model.getAttribute("error");

        assertThat(view).isEqualTo("users/login");
        assertThat(error).isEqualTo("Почта или пароль введены неверно.");
    }

    @Test
    public void whenGetLogoutPageThenSessionInvalidateAndRedirectLoginPage() {
        HttpSession session = mock(HttpSession.class);

        String view = userController.logout(session);

        verify(session).invalidate();
        assertThat(view).isEqualTo("redirect:/users/login");
    }
}