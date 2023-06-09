package ru.job4j.cinema.dto;

import java.util.Objects;

public record FilmDto(
        int id,
        String name,
        String description,
        int year,
        String genre,
        int minimalAge,
        int durationInMinutes,
        int fileId
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FilmDto filmDto = (FilmDto) o;
        return id == filmDto.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
