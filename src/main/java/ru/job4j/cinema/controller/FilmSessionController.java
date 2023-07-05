package ru.job4j.cinema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.service.FilmSessionService;
import ru.job4j.cinema.utility.PlaceUtility;

import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/film-sessions")
public class FilmSessionController {
    private final FilmSessionService filmSessionService;

    public FilmSessionController(FilmSessionService filmSessionService) {
        this.filmSessionService = filmSessionService;
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
}
