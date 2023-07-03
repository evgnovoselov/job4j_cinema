package ru.job4j.cinema.dto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;

public record FilmSessionDto(
        int id,
        String filmName,
        int filmDurationInMinutes,
        int filmMinimalAge,
        String hallName,
        int hallRowCount,
        int hallPlaceCount,
        LocalDateTime startTime,
        LocalDateTime endTime,
        int price,
        Collection<TicketDto> tickets
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
