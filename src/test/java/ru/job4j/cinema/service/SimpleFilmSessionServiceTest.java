package ru.job4j.cinema.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.dto.FilmSessionSetDto;
import ru.job4j.cinema.dto.FilmSessionTimetableDto;
import ru.job4j.cinema.model.*;
import ru.job4j.cinema.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SimpleFilmSessionServiceTest {
    private static final LocalDateTime DATE_TIME = LocalDateTime.of(2023, 8, 7, 8, 0);
    private SimpleFilmSessionService simpleFilmSessionService;
    private FilmSessionRepository filmSessionRepository;
    private GenreRepository genreRepository;
    private FilmRepository filmRepository;
    private HallRepository hallRepository;
    private TicketRepository ticketRepository;

    @BeforeEach
    public void setUp() {
        filmSessionRepository = mock(FilmSessionRepository.class);
        genreRepository = mock(GenreRepository.class);
        filmRepository = mock(FilmRepository.class);
        hallRepository = mock(HallRepository.class);
        ticketRepository = mock(TicketRepository.class);
        simpleFilmSessionService = new SimpleFilmSessionService(
                filmSessionRepository,
                genreRepository,
                filmRepository,
                hallRepository,
                ticketRepository
        );
    }

    private static FilmSession makeFilmSession(int seed) {
        LocalDateTime time = DATE_TIME.truncatedTo(ChronoUnit.MINUTES);
        return new FilmSession() {{
            setId(seed);
            setFilmId(1);
            setHallsId(seed % 10);
            setStartTime(time.plusHours(seed % 100));
            setEndTime(time.plusHours(1 + (seed % 100)));
            setPrice(100 + (seed % 100));
        }};
    }

    @Test
    public void whenFindAllByDateThenGetFilmSessionSetDtoSetByDate() {
        List<FilmSession> filmSessions = List.of(makeFilmSession(5), makeFilmSession(3));
        when(filmSessionRepository.findAllByDate(any())).thenReturn(filmSessions);
        Film film = new Film() {{
            setId(1);
            setName("name");
            setDescription("description");
            setYear(2000);
            setGenreId(1);
            setMinimalAge(16);
            setDurationInMinutes(120);
            setFileId(1);
        }};
        when(filmRepository.findById(anyInt())).thenReturn(Optional.of(film));
        when(genreRepository.findAll()).thenReturn(List.of(new Genre(1, "name"), new Genre(2, "name2")));

        Collection<FilmSessionSetDto> actualFilmSessionSetDtoSet = simpleFilmSessionService.findAllByDate(LocalDate.now());

        Collection<FilmSessionSetDto> expectedFilmSessionSetDtoSet = Set.of(
                new FilmSessionSetDto(
                        1,
                        "name",
                        2000,
                        "name",
                        16,
                        120,
                        1,
                        Set.of(
                                new FilmSessionTimetableDto(3, 1, DATE_TIME.plusHours(3), 103),
                                new FilmSessionTimetableDto(5, 1, DATE_TIME.plusHours(5), 105)
                        )
                )
        );
        assertThat(actualFilmSessionSetDtoSet).usingRecursiveComparison().isEqualTo(expectedFilmSessionSetDtoSet);
    }

    @Test
    public void whenDontHaveThenEmpty() {
        when(filmSessionRepository.findAllByDate(any())).thenReturn(Collections.emptyList());
        when(filmSessionRepository.findById(anyInt())).thenReturn(Optional.empty());

        Collection<FilmSessionSetDto> actualFilmSessionSetDtoSet = simpleFilmSessionService.findAllByDate(LocalDate.now());
        Optional<FilmSessionDto> actualFilmSessionDto = simpleFilmSessionService.findById(1);

        Collection<FilmSessionSetDto> expectedFilmSessionSetDtoSet = Collections.emptyList();

        assertThat(actualFilmSessionSetDtoSet).isEqualTo(expectedFilmSessionSetDtoSet);
        assertThat(actualFilmSessionDto).isEqualTo(Optional.empty());
    }

    @Test
    public void whenFindAllByDateThenGetByDateWithSortStartTime() {
        FilmSession filmSession = makeFilmSession(3);
        filmSession.setStartTime(filmSession.getStartTime().plusHours(5));
        filmSession.setEndTime(filmSession.getEndTime().plusHours(5));
        List<FilmSession> filmSessions = List.of(makeFilmSession(7), filmSession);
        when(filmSessionRepository.findAllByDate(any())).thenReturn(filmSessions);
        Film film = new Film() {{
            setId(1);
            setName("name");
            setDescription("description");
            setYear(2000);
            setGenreId(1);
            setMinimalAge(16);
            setDurationInMinutes(120);
            setFileId(1);
        }};
        when(filmRepository.findById(anyInt())).thenReturn(Optional.of(film));
        when(genreRepository.findAll()).thenReturn(List.of(new Genre(1, "name"), new Genre(2, "name2")));

        Collection<FilmSessionSetDto> actualFilmSessionSetDtoSet = simpleFilmSessionService.findAllByDate(LocalDate.now());

        Collection<FilmSessionSetDto> expectedFilmSessionSetDtoSet = Set.of(
                new FilmSessionSetDto(
                        1,
                        "name",
                        2000,
                        "name",
                        16,
                        120,
                        1,
                        new LinkedHashSet<>() {{
                            add(new FilmSessionTimetableDto(7, 1, DATE_TIME.plusHours(7), 107));
                            add(new FilmSessionTimetableDto(3, 1, DATE_TIME.plusHours(8), 103));
                        }}
                )
        );
        assertThat(actualFilmSessionSetDtoSet).usingRecursiveComparison().isEqualTo(expectedFilmSessionSetDtoSet);
    }

    @Test
    public void whenFindByIdThenGet() {
        when(filmSessionRepository.findById(anyInt())).thenReturn(Optional.of(makeFilmSession(1)));
        Film film = new Film() {{
            setId(1);
            setName("name");
            setDescription("description");
            setYear(2000);
            setGenreId(1);
            setMinimalAge(16);
            setDurationInMinutes(120);
            setFileId(1);
        }};
        when(filmRepository.findById(anyInt())).thenReturn(Optional.of(film));
        Hall hall = new Hall(1, "nameHall", 3, 9, "descriptionHall");
        when(hallRepository.findById(anyInt())).thenReturn(Optional.of(hall));
        List<Ticket> tickets = List.of(new Ticket(1, 1, 1, 1, 1));
        when(ticketRepository.findAllBySessionId(anyInt())).thenReturn(tickets);

        Optional<FilmSessionDto> actualFilmSessionDto = simpleFilmSessionService.findById(1);

        Optional<FilmSessionDto> expectedFilmSessionDto = Optional.of(new FilmSessionDto(
                1,
                film,
                hall,
                DATE_TIME.plusHours(1),
                DATE_TIME.plusHours(2),
                101,
                tickets
        ));
        assertThat(actualFilmSessionDto).usingRecursiveComparison().isEqualTo(expectedFilmSessionDto);
    }
}