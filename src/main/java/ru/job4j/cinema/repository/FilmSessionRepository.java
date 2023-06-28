package ru.job4j.cinema.repository;

import ru.job4j.cinema.model.FilmSession;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

public interface FilmSessionRepository {
    FilmSession save(FilmSession filmSession);

    Collection<FilmSession> findAll();

    Collection<FilmSession> findAllByDate(LocalDate date);

    Optional<FilmSession> findById(int id);

    boolean deleteById(int id);
}
