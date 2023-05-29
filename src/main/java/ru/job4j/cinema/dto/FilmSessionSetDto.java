package ru.job4j.cinema.dto;

import java.util.Objects;
import java.util.Set;

public record FilmSessionSetDto(
        int filmId,
        String name,
        int year,
        String genre,
        int minimalAge,
        int durationInMinutes,
        int fileId,
        Set<FilmSessionTimetableDto> filmSessions
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FilmSessionSetDto that = (FilmSessionSetDto) o;
        return filmId == that.filmId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(filmId);
    }
}
