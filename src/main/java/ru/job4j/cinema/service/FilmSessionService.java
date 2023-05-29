package ru.job4j.cinema.service;

import ru.job4j.cinema.dto.FilmSessionSetDto;

import java.time.LocalDate;
import java.util.Collection;

public interface FilmSessionService {
    Collection<FilmSessionSetDto> findAllByDate(LocalDate date);
}
