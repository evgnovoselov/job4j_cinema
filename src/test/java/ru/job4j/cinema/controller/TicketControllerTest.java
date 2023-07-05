package ru.job4j.cinema.controller;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.dto.TicketDto;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.FilmSessionService;
import ru.job4j.cinema.service.TicketService;
import ru.job4j.cinema.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TicketControllerTest {
    private TicketController ticketController;
    private TicketService ticketService;
    private UserService userService;
    private FilmSessionService filmSessionService;

    @BeforeEach
    public void setUp() {
        ticketService = mock(TicketService.class);
        userService = mock(UserService.class);
        filmSessionService = mock(FilmSessionService.class);
        ticketController = new TicketController(ticketService, userService, filmSessionService);
    }

    private static FilmSessionDto makeFilmSessionDto(int seed) {
        Film film = new Film();
        Hall hall = new Hall(1, "hall", 3, 7, "description hall");
        return new FilmSessionDto(
                seed,
                film.getName(),
                film.getDurationInMinutes(),
                film.getMinimalAge(),
                hall.getName(),
                hall.getRowCount(),
                hall.getPlaceCount(),
                LocalDateTime.now().plusHours(seed),
                LocalDateTime.now().plusHours(1).plusHours(seed),
                100 + seed,
                List.of(
                        new TicketDto(1, seed, 1, 3, 1),
                        new TicketDto(2, seed, 2, 2, 1),
                        new TicketDto(3, seed, 2, 3, 1)
                )
        );
    }

    @Test
    public void whenBuyTicketWithRightDataThenSuccessBuyTicket() {
        Ticket ticket = new Ticket(1, 1, 1, 4, 1);
        when(ticketService.save(ticket)).thenReturn(Optional.of(ticket));
        User user = new User(ticket.getUserId(), "name", "name@example.com", "password");
        when(userService.findById(ticket.getUserId())).thenReturn(Optional.of(user));
        FilmSessionDto filmSessionDto = makeFilmSessionDto(1);
        when(filmSessionService.findById(ticket.getSessionId())).thenReturn(Optional.of(filmSessionDto));
        ConcurrentModel model = new ConcurrentModel();
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("user")).thenReturn(user);

        String view = ticketController.processBuy(ticket, model, session);
        Ticket actualTicket = (Ticket) model.getAttribute("ticket");
        FilmSessionDto actualFilmSessionDto = (FilmSessionDto) model.getAttribute("filmSession");
        User actualUser = (User) model.getAttribute("user");

        assertThat(view).isEqualTo("tickets/success-buy");
        assertThat(actualTicket).isEqualTo(ticket);
        assertThat(actualFilmSessionDto).isEqualTo(filmSessionDto);
        assertThat(actualUser).isEqualTo(user);
    }

    @Test
    public void whenBuyTicketIfNotHaveUserThenViewErrorPage() {
        Ticket ticket = new Ticket();
        ConcurrentModel model = new ConcurrentModel();
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("user")).thenReturn(null);
        when(ticketService.save(ticket)).thenReturn(Optional.empty());
        when(userService.findById(anyInt())).thenReturn(Optional.empty());

        String view = ticketController.processBuy(ticket, model, session);
        String error = (String) model.getAttribute("error");

        assertThat(view).isEqualTo("tickets/error-buy");
        assertThat(error).isEqualTo("Не удалось приобрести билет на заданное место. Вероятно оно уже занято. Перейдите на страницу бронирования билетов и попробуйте снова.");
    }

    @Test
    public void whenBuyTicketIfPlaceOccupiedThenViewErrorPage() {
        Ticket ticket = new Ticket();
        ConcurrentModel model = new ConcurrentModel();
        HttpSession session = mock(HttpSession.class);
        when(ticketService.save(ticket)).thenReturn(Optional.empty());

        String view = ticketController.processBuy(ticket, model, session);
        String error = (String) model.getAttribute("error");

        assertThat(view).isEqualTo("tickets/error-buy");
        assertThat(error).isEqualTo("Не удалось приобрести билет на заданное место. Вероятно оно уже занято. Перейдите на страницу бронирования билетов и попробуйте снова.");
    }
}