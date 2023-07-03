package ru.job4j.cinema.utility;

import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.dto.TicketDto;

import java.util.Collection;

public final class PlaceUtility {
    public static int[][] makeMapAvailablePlaces(FilmSessionDto filmSessionDto) {
        Collection<TicketDto> tickets = filmSessionDto.tickets();
        int[][] mapPlaces = new int[filmSessionDto.hallRowCount()][filmSessionDto.hallPlaceCount()];
        for (TicketDto ticket : tickets) {
            mapPlaces[ticket.rowNumber()][ticket.placeNumber()] = 1;
        }
        return mapPlaces;
    }
}
