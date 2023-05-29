package ru.job4j.cinema.dto;

import java.time.LocalDateTime;
import java.util.Objects;

public record FilmSessionTimetableDto(
        int id,
        int filmId,
        LocalDateTime startTime,
        int price
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FilmSessionTimetableDto that = (FilmSessionTimetableDto) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
