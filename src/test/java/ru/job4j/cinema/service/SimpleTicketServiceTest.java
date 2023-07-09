package ru.job4j.cinema.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.dto.TicketDto;
import ru.job4j.cinema.model.*;
import ru.job4j.cinema.repository.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SimpleTicketServiceTest {
    private SimpleTicketService simpleTicketService;
    private TicketRepository ticketRepository;
    private FilmSessionRepository filmSessionRepository;
    private HallRepository hallRepository;
    private UserRepository userRepository;
    private FilmRepository filmRepository;

    @BeforeEach
    public void setUp() {
        ticketRepository = mock(TicketRepository.class);
        filmSessionRepository = mock(FilmSessionRepository.class);
        hallRepository = mock(HallRepository.class);
        userRepository = mock(UserRepository.class);
        filmRepository = mock(FilmRepository.class);
        simpleTicketService = new SimpleTicketService(
                ticketRepository,
                filmSessionRepository,
                hallRepository,
                userRepository,
                filmRepository
        );
    }

    @Test
    public void whenSaveThenGetTicketDto() {
        LocalDateTime dateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Ticket ticket = new Ticket(1, 1, 1, 3, 1);
        when(filmSessionRepository.findById(anyInt())).thenReturn(Optional.of(
                new FilmSession(1, 1, 1, dateTime.plusMinutes(10), dateTime.plusHours(1), 250)
        ));
        when(filmRepository.findById(anyInt())).thenReturn(Optional.of(
                new Film.Builder()
                        .buildName("filmName")
                        .buildDurationInMinutes(120)
                        .buildMinimalAge(12)
                        .build()
        ));
        when(hallRepository.findById(anyInt())).thenReturn(Optional.of(
                new Hall(1, "hallName", 5, 10, "hallDescription")
        ));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(new User(1, "userFullName", "name@example.com", "password")));
        when(ticketRepository.save(any(Ticket.class)))
                .thenReturn(Optional.of(new Ticket(1, 1, 1, 3, 1)));

        Optional<TicketDto> actualTicketDto = simpleTicketService.save(ticket);

        TicketDto expectedTicketDto = new TicketDto(
                1,
                1,
                3,
                "filmName",
                120,
                12,
                1,
                dateTime.plusMinutes(10),
                "hallName",
                1,
                "userFullName"
        );
        assertThat(actualTicketDto).usingRecursiveComparison().isEqualTo(Optional.of(expectedTicketDto));
    }

    @Test
    public void whenSaveAndFilmSessionNotFoundThenOptionalEmpty() {
        when(filmSessionRepository.findById(anyInt())).thenReturn(Optional.empty());

        Optional<TicketDto> actualTicketDto = simpleTicketService.save(new Ticket());

        assertThat(actualTicketDto).isEqualTo(Optional.empty());
    }

    @Test
    public void whenSaveAndFilmNotFoundThenOptionalEmpty() {
        when(filmSessionRepository.findById(anyInt())).thenReturn(Optional.of(new FilmSession()));
        when(filmRepository.findById(anyInt())).thenReturn(Optional.empty());

        Optional<TicketDto> actualTicketDto = simpleTicketService.save(new Ticket());

        assertThat(actualTicketDto).isEqualTo(Optional.empty());
    }

    @Test
    public void whenSaveAndHallNotFoundThenOptionalEmpty() {
        when(filmSessionRepository.findById(anyInt())).thenReturn(Optional.of(new FilmSession()));
        when(filmRepository.findById(anyInt())).thenReturn(Optional.of(new Film()));
        when(hallRepository.findById(anyInt())).thenReturn(Optional.empty());

        Optional<TicketDto> actualTicketDto = simpleTicketService.save(new Ticket());

        assertThat(actualTicketDto).isEqualTo(Optional.empty());
    }

    @Test
    public void whenSaveAndTicketRowLess0ThenOptionalEmpty() {
        when(filmSessionRepository.findById(anyInt())).thenReturn(Optional.of(new FilmSession()));
        when(filmRepository.findById(anyInt())).thenReturn(Optional.of(new Film()));
        when(hallRepository.findById(anyInt())).thenReturn(Optional.of(new Hall(1, "", 5, 10, "")));

        Optional<TicketDto> actualTicketDto = simpleTicketService.save(new Ticket(1, 1, -1, 1, 1));

        assertThat(actualTicketDto).isEqualTo(Optional.empty());
    }

    @Test
    public void whenSaveAndTicketRowMoreRowInHallThenOptionalEmpty() {
        when(filmSessionRepository.findById(anyInt())).thenReturn(Optional.of(new FilmSession()));
        when(filmRepository.findById(anyInt())).thenReturn(Optional.of(new Film()));
        when(hallRepository.findById(anyInt())).thenReturn(Optional.of(new Hall(1, "", 5, 10, "")));

        Optional<TicketDto> actualTicketDto = simpleTicketService.save(
                new Ticket(1, 1, 10, 1, 1)
        );

        assertThat(actualTicketDto).isEqualTo(Optional.empty());
    }

    @Test
    public void whenSaveAndTicketPlaceLess0ThenOptionalEmpty() {
        when(filmSessionRepository.findById(anyInt())).thenReturn(Optional.of(new FilmSession()));
        when(filmRepository.findById(anyInt())).thenReturn(Optional.of(new Film()));
        when(hallRepository.findById(anyInt())).thenReturn(Optional.of(new Hall(1, "", 5, 10, "")));

        Optional<TicketDto> actualTicketDto = simpleTicketService.save(
                new Ticket(1, 1, 1, -1, 1)
        );

        assertThat(actualTicketDto).isEqualTo(Optional.empty());
    }

    @Test
    public void whenSaveAndTicketPlaceMorePlaceInHallThenOptionalEmpty() {
        when(filmSessionRepository.findById(anyInt())).thenReturn(Optional.of(new FilmSession()));
        when(filmRepository.findById(anyInt())).thenReturn(Optional.of(new Film()));
        when(hallRepository.findById(anyInt())).thenReturn(Optional.of(new Hall(1, "", 5, 10, "")));

        Optional<TicketDto> actualTicketDto = simpleTicketService.save(
                new Ticket(1, 1, 1, 20, 1)
        );

        assertThat(actualTicketDto).isEqualTo(Optional.empty());
    }

    @Test
    public void whenSaveAndUserNotFoundThenOptionalEmpty() {
        when(filmSessionRepository.findById(anyInt())).thenReturn(Optional.of(new FilmSession()));
        when(filmRepository.findById(anyInt())).thenReturn(Optional.of(new Film()));
        when(hallRepository.findById(anyInt())).thenReturn(Optional.of(new Hall(1, "", 5, 10, "")));
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        Optional<TicketDto> actualTicketDto = simpleTicketService.save(
                new Ticket(1, 1, 1, 1, 1)
        );

        assertThat(actualTicketDto).isEqualTo(Optional.empty());
    }

    @Test
    public void whenTicketNotSaveThenOptionalEmpty() {
        when(filmSessionRepository.findById(anyInt())).thenReturn(Optional.of(new FilmSession()));
        when(filmRepository.findById(anyInt())).thenReturn(Optional.of(new Film()));
        when(hallRepository.findById(anyInt())).thenReturn(Optional.of(new Hall(1, "", 5, 10, "")));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(new User()));

        Optional<TicketDto> actualTicketDto = simpleTicketService.save(
                new Ticket(1, 1, 1, 1, 1)
        );

        assertThat(actualTicketDto).isEqualTo(Optional.empty());
    }
}