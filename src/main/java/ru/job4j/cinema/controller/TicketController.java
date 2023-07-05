package ru.job4j.cinema.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.FilmSessionService;
import ru.job4j.cinema.service.TicketService;
import ru.job4j.cinema.service.UserService;

import java.util.Optional;

@Controller
@RequestMapping("/tickets")
public class TicketController {
    private final TicketService ticketService;
    private final UserService userService;
    private final FilmSessionService filmSessionService;

    public TicketController(TicketService ticketService,
                            UserService userService,
                            FilmSessionService filmSessionService) {
        this.ticketService = ticketService;
        this.userService = userService;
        this.filmSessionService = filmSessionService;
    }

    @PostMapping("/buy")
    public String processBuy(@ModelAttribute Ticket ticket, Model model, HttpSession session) {
        FilmSessionDto filmSessionDto;
        Optional<Ticket> ticketOptional;
        User user = (User) session.getAttribute("user");
        try {
            ticket.setUserId(user != null ? user.getId() : 0);
            ticketOptional = ticketService.save(ticket);
            user = userService.findById(ticket.getUserId()).orElse(new User(0, "Гость", "", ""));
            if (ticketOptional.isEmpty()) {
                throw new IllegalArgumentException("Не удалось приобрести билет на заданное место. Вероятно оно уже занято. Перейдите на страницу бронирования билетов и попробуйте снова.");
            }
            filmSessionDto = filmSessionService.findById(ticket.getSessionId()).orElseThrow();
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "tickets/error-buy";
        }
        model.addAttribute("ticket", ticketOptional.get());
        model.addAttribute("filmSession", filmSessionDto);
        model.addAttribute("user", user);
        return "tickets/success-buy";
    }
}
