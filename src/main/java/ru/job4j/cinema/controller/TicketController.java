package ru.job4j.cinema.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.cinema.dto.TicketDto;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.TicketService;

import java.util.Optional;

@Controller
@RequestMapping("/tickets")
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping("/buy")
    public String processBuy(@ModelAttribute Ticket ticket, Model model, HttpSession session) {
        Optional<TicketDto> ticketOptional;
        User user = (User) session.getAttribute("user");
        ticket.setUserId(user != null ? user.getId() : 0);
        ticketOptional = ticketService.save(ticket);
        if (ticketOptional.isEmpty()) {
            model.addAttribute("error", "Не удалось приобрести билет на заданное место."
                    + " Вероятно оно уже занято. Перейдите на страницу бронирования билетов и попробуйте снова.");
            return "tickets/error-buy";
        }
        model.addAttribute("ticket", ticketOptional.get());
        return "tickets/success-buy";
    }
}
