package ru.job4j.cinema.service;

import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.dto.FilmSessionSetDto;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

public interface FilmSessionService {
    Collection<FilmSessionSetDto> findAllByDate(LocalDate date);

    Optional<FilmSessionDto> findById(int id);
}
