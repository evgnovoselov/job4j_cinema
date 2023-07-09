package ru.job4j.cinema.dto;

import java.util.Objects;

public record TicketPlaceDto(
        int id,
        int filmSessionId,
        int rowNumber,
        int placeNumber
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TicketPlaceDto ticketDto = (TicketPlaceDto) o;
        return id == ticketDto.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
