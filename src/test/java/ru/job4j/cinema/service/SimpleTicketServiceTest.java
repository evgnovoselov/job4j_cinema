package ru.job4j.cinema.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.repository.FilmSessionRepository;
import ru.job4j.cinema.repository.HallRepository;
import ru.job4j.cinema.repository.TicketRepository;
import ru.job4j.cinema.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
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

    @BeforeEach
    public void setUp() {
        ticketRepository = mock(TicketRepository.class);
        filmSessionRepository = mock(FilmSessionRepository.class);
        hallRepository = mock(HallRepository.class);
        userRepository = mock(UserRepository.class);
        simpleTicketService = new SimpleTicketService(
                ticketRepository,
                filmSessionRepository,
                hallRepository,
                userRepository
        );
    }

    @Test
    public void whenSaveThenGetSame() {
        Ticket ticket = new Ticket(1, 1, 1, 3, 1);
        when(filmSessionRepository.findById(anyInt())).thenReturn(Optional.of(new FilmSession()));
        when(hallRepository.findById(anyInt())).thenReturn(Optional.of(new Hall(1, "", 5, 10, "")));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(new User()));
        when(ticketRepository.save(any(Ticket.class)))
                .thenReturn(Optional.of(new Ticket(1, 1, 1, 3, 1)));

        Optional<Ticket> actualTicket = simpleTicketService.save(ticket);

        assertThat(actualTicket).usingRecursiveComparison().isEqualTo(Optional.of(ticket));
    }

    @Test
    public void whenSaveAndFilmSessionNotFoundThenException() {
        assertThatIllegalArgumentException().isThrownBy(() -> simpleTicketService.save(new Ticket()));
    }

    @Test
    public void whenSaveAndHallNotFoundThenException() {
        when(filmSessionRepository.findById(anyInt())).thenReturn(Optional.of(new FilmSession()));

        assertThatIllegalArgumentException().isThrownBy(() -> simpleTicketService.save(new Ticket()));
    }

    @Test
    public void whenSaveAndTicketRowLess0ThenException() {
        when(filmSessionRepository.findById(anyInt())).thenReturn(Optional.of(new FilmSession()));
        when(hallRepository.findById(anyInt())).thenReturn(Optional.of(new Hall(1, "", 5, 10, "")));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> simpleTicketService.save(
                        new Ticket(1, 1, -1, 1, 1)
                ));
    }

    @Test
    public void whenSaveAndTicketRowMoreRowInHallThenException() {
        when(filmSessionRepository.findById(anyInt())).thenReturn(Optional.of(new FilmSession()));
        when(hallRepository.findById(anyInt())).thenReturn(Optional.of(new Hall(1, "", 5, 10, "")));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> simpleTicketService.save(
                        new Ticket(1, 1, 10, 1, 1)
                ));
    }

    @Test
    public void whenSaveAndTicketPlaceLess0ThenException() {
        when(filmSessionRepository.findById(anyInt())).thenReturn(Optional.of(new FilmSession()));
        when(hallRepository.findById(anyInt())).thenReturn(Optional.of(new Hall(1, "", 5, 10, "")));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> simpleTicketService.save(
                        new Ticket(1, 1, 1, -1, 1)
                ));
    }

    @Test
    public void whenSaveAndTicketPlaceMorePlaceInHallThenException() {
        when(filmSessionRepository.findById(anyInt())).thenReturn(Optional.of(new FilmSession()));
        when(hallRepository.findById(anyInt())).thenReturn(Optional.of(new Hall(1, "", 5, 10, "")));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> simpleTicketService.save(
                        new Ticket(1, 1, 1, 10, 1)
                ));
    }

    @Test
    public void whenSaveAndUserNotFoundThenException() {
        when(filmSessionRepository.findById(anyInt())).thenReturn(Optional.of(new FilmSession()));
        when(hallRepository.findById(anyInt())).thenReturn(Optional.of(new Hall(1, "", 5, 10, "")));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> simpleTicketService.save(
                        new Ticket(1, 1, 1, 1, 1)
                ));
    }
}