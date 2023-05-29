package ru.job4j.cinema.repository;

import ru.job4j.cinema.model.FilmSession;

import java.time.LocalDate;
import java.util.Collection;

public interface FilmSessionRepository {
    Collection<FilmSession> findAllByDate(LocalDate date);
}
