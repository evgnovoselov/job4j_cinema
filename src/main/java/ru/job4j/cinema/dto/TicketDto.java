package ru.job4j.cinema.dto;

import java.util.Objects;

public record TicketDto(int id, int sessionId, int rowNumber, int placeNumber, int userId) {
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
