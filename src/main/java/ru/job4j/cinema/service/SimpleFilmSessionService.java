package ru.job4j.cinema.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.dto.FilmSessionSetDto;
import ru.job4j.cinema.dto.FilmSessionTimetableDto;
import ru.job4j.cinema.dto.TicketPlaceDto;
import ru.job4j.cinema.model.*;
import ru.job4j.cinema.repository.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SimpleFilmSessionService implements FilmSessionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleFilmSessionService.class.getName());
    private final FilmSessionRepository filmSessionRepository;
    private final GenreRepository genreRepository;
    private final FilmRepository filmRepository;
    private final HallRepository hallRepository;
    private final TicketRepository ticketRepository;

    public SimpleFilmSessionService(
            FilmSessionRepository filmSessionRepository,
            GenreRepository genreRepository,
            FilmRepository filmRepository,
            HallRepository hallRepository,
            TicketRepository ticketRepository
    ) {
        this.filmSessionRepository = filmSessionRepository;
        this.genreRepository = genreRepository;
        this.filmRepository = filmRepository;
        this.hallRepository = hallRepository;
        this.ticketRepository = ticketRepository;
    }

    @Override
    public Collection<FilmSessionSetDto> findAllByDate(LocalDate date) {
        Collection<FilmSession> sessionsByDate = filmSessionRepository.findAllByDate(date);
        if (sessionsByDate.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Integer, Set<FilmSession>> filmIdSessions = getFilmIdSessions(sessionsByDate);
        Set<Film> films = filmIdSessions.keySet().stream().map(filmRepository::findById).filter(Optional::isPresent)
                .map(Optional::get).collect(Collectors.toSet());
        Collection<Genre> genres = genreRepository.findAll();
        return getFilmSessionSetDtoSet(filmIdSessions, films, genres);
    }

    /**
     * Генерирует множество содержащее фильмы и их сеансы для расписания.
     *
     * @param filmIdSessions Карта с идентификаторами фильма и множеством их сеансов.
     * @param films          Множество фильмов.
     * @param genres         Коллекция жанров.
     * @return Множетсов фильмов с их сессиями.
     */
    private static Set<FilmSessionSetDto> getFilmSessionSetDtoSet(Map<Integer, Set<FilmSession>> filmIdSessions, Set<Film> films, Collection<Genre> genres) {
        Set<FilmSessionSetDto> filmSessionSetDtoSet = new HashSet<>();
        Map<Integer, String> genresMap = genres.stream().collect(Collectors.toMap(Genre::getId, Genre::getName));
        for (Film film : films) {
            Set<FilmSession> filmSessions = filmIdSessions.get(film.getId());
            Set<FilmSessionTimetableDto> filmSessionTimetableDtoSet = filmSessions.stream()
                    .map(filmSession -> new FilmSessionTimetableDto(
                            filmSession.getId(),
                            filmSession.getFilmId(),
                            filmSession.getStartTime(),
                            filmSession.getPrice()
                    )).collect(Collectors.toCollection(() -> new TreeSet<>((o1, o2) -> {
                        int compare = 0;
                        if (o1.startTime().isBefore(o2.startTime())) {
                            compare = -1;
                        }
                        if (o1.startTime().isAfter((o2.startTime()))) {
                            compare = 1;
                        }
                        return compare;
                    })));
            filmSessionSetDtoSet.add(new FilmSessionSetDto(
                    film.getId(),
                    film.getName(),
                    film.getYear(),
                    genresMap.get(film.getGenreId()),
                    film.getMinimalAge(),
                    film.getDurationInMinutes(),
                    film.getFileId(),
                    filmSessionTimetableDtoSet
            ));
        }
        return filmSessionSetDtoSet;
    }

    /**
     * Создаем карту сеансов по идентификаторам фильмов.
     *
     * @param filmSessions Коллекция сеансов.
     * @return Карта, где key - идентификаторы фильма, value - множество сеансов.
     */
    private static Map<Integer, Set<FilmSession>> getFilmIdSessions(Collection<FilmSession> filmSessions) {
        return filmSessions.stream()
                .collect(Collectors.toMap(
                        FilmSession::getFilmId,
                        filmSession -> new HashSet<>(Set.of(filmSession)),
                        (value, value2) -> {
                            value.addAll(value2);
                            return value;
                        }
                ));
    }

    @Override
    public Optional<FilmSessionDto> findById(int id) {
        try {
            Optional<FilmSession> filmSessionOptional = filmSessionRepository.findById(id);
            if (filmSessionOptional.isEmpty()) {
                throw new IllegalArgumentException("Сеанс фильма не найден.");
            }
            FilmSession filmSession = filmSessionOptional.get();
            Film film = filmRepository.findById(filmSession.getFilmId()).orElseThrow();
            Hall hall = hallRepository.findById(filmSession.getHallsId()).orElseThrow();
            Collection<Ticket> tickets = ticketRepository.findAllBySessionId(filmSession.getId());
            FilmSessionDto filmSessionDto = new FilmSessionDto(
                    filmSession.getId(),
                    film.getName(),
                    film.getDurationInMinutes(),
                    film.getMinimalAge(),
                    hall.getName(),
                    hall.getRowCount(),
                    hall.getPlaceCount(),
                    filmSession.getStartTime(),
                    filmSession.getEndTime(),
                    filmSession.getPrice(),
                    tickets.stream().map(ticket -> new TicketPlaceDto(
                            ticket.getId(),
                            filmSession.getId(),
                            ticket.getRowNumber(),
                            ticket.getPlaceNumber()
                    )).toList()
            );
            return Optional.of(filmSessionDto);
        } catch (Exception e) {
            LOGGER.error("Произошла ошибка поиска сеанса фильма: ", e);
        }
        return Optional.empty();
    }
}
