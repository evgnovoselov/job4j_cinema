package ru.job4j.cinema.dto;

import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.model.Ticket;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;

public record FilmSessionDto(
        int id,
        Film film,
        Hall hall,
        LocalDateTime startTime,
        LocalDateTime endTime,
        int price,
        Collection<Ticket> tickets
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FilmSessionDto that = (FilmSessionDto) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
