package ru.job4j.cinema.controller;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.cinema.dto.TicketDto;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.TicketService;
import ru.job4j.cinema.service.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TicketControllerTest {
    private TicketController ticketController;
    private TicketService ticketService;
    private UserService userService;

    @BeforeEach
    public void setUp() {
        ticketService = mock(TicketService.class);
        userService = mock(UserService.class);
        ticketController = new TicketController(ticketService);
    }

    @Test
    public void whenBuyTicketWithRightDataThenSuccessBuyTicket() {
        Ticket ticket = new Ticket(1, 1, 1, 4, 1);
        TicketDto ticketDto = new TicketDto(
                1,
                1,
                4,
                "filmName",
                120,
                12,
                1,
                LocalDateTime.now(),
                "hallName",
                1,
                "userFillName"
        );
        when(ticketService.save(ticket)).thenReturn(Optional.of(ticketDto));
        User user = new User(ticketDto.userId(), "name", "name@example.com", "password");
        ConcurrentModel model = new ConcurrentModel();
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("user")).thenReturn(user);

        String view = ticketController.processBuy(ticket, model, session);
        TicketDto actualTicket = (TicketDto) model.getAttribute("ticket");

        assertThat(view).isEqualTo("tickets/success-buy");
        assertThat(actualTicket).usingRecursiveComparison().isEqualTo(ticketDto);
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