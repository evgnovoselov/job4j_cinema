package ru.job4j.cinema.dto;

import java.time.LocalDateTime;
import java.util.Objects;

public record TicketDto(
        int id,
        int rowNumber,
        int placeNumber,
        String filmName,
        int filmDurationInMinutes,
        int filmMinimalAge,
        int sessionId,
        LocalDateTime sessionStartTime,
        String hallName,
        int userId,
        String userFullName
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TicketDto ticketDto = (TicketDto) o;
        return id == ticketDto.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
