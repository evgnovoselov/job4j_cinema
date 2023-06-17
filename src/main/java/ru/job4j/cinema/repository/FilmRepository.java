package ru.job4j.cinema.repository;

import ru.job4j.cinema.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmRepository {
    Film save(Film film);

    Collection<Film> findAll();

    Optional<Film> findById(int id);

    boolean deleteById(int id);
}
