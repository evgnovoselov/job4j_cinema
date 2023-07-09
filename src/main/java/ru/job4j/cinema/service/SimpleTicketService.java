package ru.job4j.cinema.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.dto.TicketDto;
import ru.job4j.cinema.model.*;
import ru.job4j.cinema.repository.*;

import java.util.Optional;

@Service
public class SimpleTicketService implements TicketService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTicketService.class.getName());
    private final TicketRepository ticketRepository;
    private final FilmSessionRepository filmSessionRepository;
    private final HallRepository hallRepository;
    private final UserRepository userRepository;
    private final FilmRepository filmRepository;

    public SimpleTicketService(
            TicketRepository ticketRepository,
            FilmSessionRepository filmSessionRepository,
            HallRepository hallRepository,
            UserRepository userRepository,
            FilmRepository filmRepository
    ) {
        this.ticketRepository = ticketRepository;
        this.filmSessionRepository = filmSessionRepository;
        this.hallRepository = hallRepository;
        this.userRepository = userRepository;
        this.filmRepository = filmRepository;
    }

    @Override
    public Optional<TicketDto> save(Ticket ticket) {
        try {
            FilmSession filmSession = filmSessionRepository.findById(ticket.getSessionId())
                    .orElseThrow(() -> new IllegalArgumentException("Заданный в билете сеанс не найден."));
            Film film = filmRepository.findById(filmSession.getFilmId())
                    .orElseThrow(() -> new IllegalStateException("Фльм не найден."));
            Hall hall = hallRepository.findById(filmSession.getHallsId())
                    .orElseThrow(() -> new IllegalArgumentException("Заданный в билете зал не найден."));
            validateTicketPlace(hall, ticket);
            User user = userRepository.findById(ticket.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Покупатель билета не зарегистрирован в системе."));
            Optional<Ticket> ticketOptional = ticketRepository.save(ticket);
            if (ticketOptional.isEmpty()) {
                throw new Exception("Билет не удалось сохранить в системе.");
            }
            Ticket savedTicket = ticketOptional.get();
            TicketDto ticketDto = new TicketDto(
                    savedTicket.getId(),
                    savedTicket.getRowNumber(),
                    savedTicket.getPlaceNumber(),
                    film.getName(),
                    film.getDurationInMinutes(),
                    film.getMinimalAge(),
                    savedTicket.getSessionId(),
                    filmSession.getStartTime(),
                    hall.getName(),
                    savedTicket.getUserId(),
                    user.getFullName()
            );
            return Optional.of(ticketDto);
        } catch (Exception e) {
            LOGGER.error("Произошла ошибка валидации билета: ", e);
        }
        return Optional.empty();
    }

    private void validateTicketPlace(Hall hall, Ticket ticket) {
        if (ticket.getRowNumber() < 0 || ticket.getRowNumber() >= hall.getRowCount()) {
            throw new IllegalArgumentException("Заданный в билете ряд не доступен в зале.");
        }
        if (ticket.getPlaceNumber() < 0 || ticket.getPlaceNumber() >= hall.getPlaceCount()) {
            throw new IllegalArgumentException("Заданное в билете место не доступно в зале.");
        }
    }
}
