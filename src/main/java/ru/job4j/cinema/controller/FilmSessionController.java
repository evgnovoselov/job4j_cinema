package ru.job4j.cinema.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.FilmSessionService;
import ru.job4j.cinema.service.TicketService;
import ru.job4j.cinema.service.UserService;
import ru.job4j.cinema.utility.PlaceUtility;

import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/film-sessions")
public class FilmSessionController {
    private final FilmSessionService filmSessionService;
    private final TicketService ticketService;
    private final UserService userService;

    public FilmSessionController(FilmSessionService filmSessionService, TicketService ticketService, UserService userService) {
        this.filmSessionService = filmSessionService;
        this.ticketService = ticketService;
        this.userService = userService;
    }

    @GetMapping
    public String getAllByDate(Model model, @RequestParam(value = "date", required = false) LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        model.addAttribute("date", date);
        model.addAttribute("FilmSessionSetDtoList", filmSessionService.findAllByDate(date));
        return "film-sessions/list";
    }

    @GetMapping("/{id}")
    public String getById(Model model, @PathVariable int id) {
        Optional<FilmSessionDto> filmSessionDtoOptional = filmSessionService.findById(id);
        if (filmSessionDtoOptional.isEmpty()) {
            model.addAttribute("message", "Данный сеанс фильма не найден в системе.");
            return "error/404";
        }
        Ticket ticket = new Ticket();
        ticket.setSessionId(filmSessionDtoOptional.get().id());
        model.addAttribute("ticket", ticket);
        model.addAttribute("filmSession", filmSessionDtoOptional.get());
        int[][] availablePlaces = PlaceUtility.makeMapAvailablePlaces(filmSessionDtoOptional.get());
        model.addAttribute("mapPlaces", availablePlaces);
        model.addAttribute("placesSize", availablePlaces[0].length);
        return "film-sessions/one";
    }

    @PostMapping("/buy-ticket")
    public String processBuyTicket(@ModelAttribute Ticket ticket, Model model, HttpSession session) {
        FilmSessionDto filmSessionDto;
        Optional<Ticket> ticketOptional;
        Optional<User> userOptional;
        User user = (User) session.getAttribute("user");
        try {
            ticket.setUserId(user != null ? user.getId() : 0);
            ticketOptional = ticketService.save(ticket);
            userOptional = userService.findById(ticket.getUserId());
            if (ticketOptional.isEmpty() || userOptional.isEmpty()) {
                throw new IllegalArgumentException("Не удалось приобрести билет на заданное место. Вероятно оно уже занято. Перейдите на страницу бронирования билетов и попробуйте снова.");
            }
            filmSessionDto = filmSessionService.findById(ticket.getSessionId()).orElseThrow();
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "film-sessions/error-buy-ticket";
        }
        model.addAttribute("ticket", ticketOptional.get());
        model.addAttribute("filmSession", filmSessionDto);
        model.addAttribute("user", userOptional.get());
        return "film-sessions/success-buy-ticket";
    }
}
