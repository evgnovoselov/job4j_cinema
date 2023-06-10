package ru.job4j.cinema.service;

import org.springframework.stereotype.Service;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.repository.FilmSessionRepository;
import ru.job4j.cinema.repository.HallRepository;
import ru.job4j.cinema.repository.TicketRepository;
import ru.job4j.cinema.repository.UserRepository;

import java.util.Optional;

@Service
public class SimpleTicketService implements TicketService {
    private final TicketRepository ticketRepository;
    private final FilmSessionRepository filmSessionRepository;
    private final HallRepository hallRepository;
    private final UserRepository userRepository;

    public SimpleTicketService(
            TicketRepository ticketRepository,
            FilmSessionRepository filmSessionRepository,
            HallRepository hallRepository,
            UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.filmSessionRepository = filmSessionRepository;
        this.hallRepository = hallRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<Ticket> save(Ticket ticket) {
        validateTicket(ticket);
        return ticketRepository.save(ticket);
    }

    private void validateTicket(Ticket ticket) {
        Optional<FilmSession> filmSessionOptional = filmSessionRepository.findById(ticket.getSessionId());
        if (filmSessionOptional.isEmpty()) {
            throw new IllegalArgumentException("Заданный в билете сеанс не найден.");
        }
        Optional<Hall> hallOptional = hallRepository.findById(filmSessionOptional.get().getHallsId());
        if (hallOptional.isEmpty()) {
            throw new IllegalArgumentException("Заданный в билете зал не найден.");
        }
        Hall hall = hallOptional.get();
        if (ticket.getRowNumber() < 0 || ticket.getRowNumber() >= hall.getRowCount()) {
            throw new IllegalArgumentException("Заданный в билете ряд не доступен в зале.");
        }
        if (ticket.getPlaceNumber() < 0 || ticket.getPlaceNumber() >= hall.getPlaceCount()) {
            throw new IllegalArgumentException("Заданное в билете место не доступно в зале.");
        }
        Optional<User> userOptional = userRepository.findById(ticket.getUserId());
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Покупатель билета не зарегистрирован в системе.");
        }
    }
}
