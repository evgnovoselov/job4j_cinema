package ru.job4j.cinema.controller;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.dto.FilmSessionSetDto;
import ru.job4j.cinema.dto.FilmSessionTimetableDto;
import ru.job4j.cinema.dto.TicketDto;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.FilmSessionService;
import ru.job4j.cinema.service.TicketService;
import ru.job4j.cinema.service.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FilmSessionControllerTest {
    private FilmSessionController filmSessionController;
    private FilmSessionService filmSessionService;
    private TicketService ticketService;
    private UserService userService;

    @BeforeEach
    public void setUp() {
        filmSessionService = mock(FilmSessionService.class);
        ticketService = mock(TicketService.class);
        userService = mock(UserService.class);
        filmSessionController = new FilmSessionController(filmSessionService, ticketService, userService);
    }

    private static FilmSessionSetDto makeFilmSessionSetDto(int seed) {
        return new FilmSessionSetDto(
                seed,
                "name" + seed,
                2000 + seed,
                "genre" + seed,
                10 + seed,
                100 + seed,
                seed,
                Set.of(
                        new FilmSessionTimetableDto(10 + seed, 40 + seed, LocalDateTime.now().plusHours(seed), 100 + seed),
                        new FilmSessionTimetableDto(20 + seed, 50 + seed, LocalDateTime.now().plusHours(seed), 100 + seed),
                        new FilmSessionTimetableDto(30 + seed, 60 + seed, LocalDateTime.now().plusHours(seed), 100 + seed)
                )
        );
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
    public void whenRequestFilmSessionListPageByDateThenGetPageWithFilmSessionSetDtoList() {
        LocalDate now = LocalDate.now();
        ConcurrentModel model = new ConcurrentModel();
        List<FilmSessionSetDto> filmSessionSetDtoList = List.of(
                makeFilmSessionSetDto(1),
                makeFilmSessionSetDto(2),
                makeFilmSessionSetDto(3)
        );
        when(filmSessionService.findAllByDate(now)).thenReturn(filmSessionSetDtoList);

        String view = filmSessionController.getAllByDate(model, now);
        LocalDate actualDate = (LocalDate) model.getAttribute("date");
        List<FilmSessionSetDto> actualFilmSessionSetDtoList = (List<FilmSessionSetDto>) model.getAttribute("FilmSessionSetDtoList");

        assertThat(view).isEqualTo("film-sessions/list");
        assertThat(actualDate).isEqualTo(now);
        assertThat(actualFilmSessionSetDtoList).isEqualTo(filmSessionSetDtoList);
    }

    @Test
    public void whenRequestFilmSessionListPageByDateNullThenGetPageWithFilmSessionSetDtoListAndDateNow() {
        LocalDate now = LocalDate.now();
        ConcurrentModel model = new ConcurrentModel();
        List<FilmSessionSetDto> filmSessionSetDtoList = List.of(
                makeFilmSessionSetDto(1),
                makeFilmSessionSetDto(2),
                makeFilmSessionSetDto(3)
        );
        when(filmSessionService.findAllByDate(now)).thenReturn(filmSessionSetDtoList);

        String view = filmSessionController.getAllByDate(model, null);
        LocalDate actualDate = (LocalDate) model.getAttribute("date");
        List<FilmSessionSetDto> actualFilmSessionSetDtoList = (List<FilmSessionSetDto>) model.getAttribute("FilmSessionSetDtoList");

        assertThat(view).isEqualTo("film-sessions/list");
        assertThat(actualDate).isEqualTo(now);
        assertThat(actualFilmSessionSetDtoList).isEqualTo(filmSessionSetDtoList);
    }

    @Test
    public void whenRequestFilmSessionByIdThenGetPageWithFilmSessionAndMatrixPlaces() {
        ConcurrentModel model = new ConcurrentModel();
        FilmSessionDto filmSessionDto = makeFilmSessionDto(1);
        when(filmSessionService.findById(anyInt())).thenReturn(Optional.of(filmSessionDto));
        Ticket ticket = new Ticket(0, 1, 0, 0, 0);
        int[][] expectedMapPlaces = new int[][]{
                {0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 1, 0, 0, 0},
                {0, 0, 1, 1, 0, 0, 0}
        };
        int expectedPlacesSize = 7;

        String view = filmSessionController.getById(model, 1);
        Ticket actualTicket = (Ticket) model.getAttribute("ticket");
        FilmSessionDto actualFilmSessionDto = (FilmSessionDto) model.getAttribute("filmSession");
        int[][] actualMapPlaces = (int[][]) model.getAttribute("mapPlaces");
        Integer actualPlacesSize = (Integer) model.getAttribute("placesSize");

        assertThat(view).isEqualTo("film-sessions/one");
        assertThat(actualTicket).usingRecursiveComparison().isEqualTo(ticket);
        assertThat(actualFilmSessionDto).usingRecursiveComparison().isEqualTo(filmSessionDto);
        assertThat(actualMapPlaces).isEqualTo(expectedMapPlaces);
        assertThat(actualPlacesSize).isEqualTo(expectedPlacesSize);
    }

    @Test
    public void whenRequestFilmSessionByIdButThereNotHaveThenGetErrorPageWithMessage() {
        ConcurrentModel model = new ConcurrentModel();
        when(filmSessionService.findById(anyInt())).thenReturn(Optional.empty());

        String view = filmSessionController.getById(model, 1);
        String message = (String) model.getAttribute("message");

        assertThat(view).isEqualTo("error/404");
        assertThat(message).isEqualTo("Данный сеанс фильма не найден в системе.");
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

        String view = filmSessionController.processBuyTicket(ticket, model, session);
        Ticket actualTicket = (Ticket) model.getAttribute("ticket");
        FilmSessionDto actualFilmSessionDto = (FilmSessionDto) model.getAttribute("filmSession");
        User actualUser = (User) model.getAttribute("user");

        assertThat(view).isEqualTo("film-sessions/success-buy-ticket");
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

        String view = filmSessionController.processBuyTicket(ticket, model, session);
        String error = (String) model.getAttribute("error");

        assertThat(view).isEqualTo("film-sessions/error-buy-ticket");
        assertThat(error).isEqualTo("Не удалось приобрести билет на заданное место. Вероятно оно уже занято. Перейдите на страницу бронирования билетов и попробуйте снова.");
    }

    @Test
    public void whenBuyTicketIfPlaceOccupiedThenViewErrorPage() {
        Ticket ticket = new Ticket();
        ConcurrentModel model = new ConcurrentModel();
        HttpSession session = mock(HttpSession.class);
        when(ticketService.save(ticket)).thenReturn(Optional.empty());

        String view = filmSessionController.processBuyTicket(ticket, model, session);
        String error = (String) model.getAttribute("error");

        assertThat(view).isEqualTo("film-sessions/error-buy-ticket");
        assertThat(error).isEqualTo("Не удалось приобрести билет на заданное место. Вероятно оно уже занято. Перейдите на страницу бронирования билетов и попробуйте снова.");
    }
}
