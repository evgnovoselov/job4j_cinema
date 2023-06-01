package ru.job4j.cinema.utility;

import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.model.Ticket;

import java.util.Collection;

public final class PlaceUtility {
    public static int[][] makeMapAvailablePlaces(FilmSessionDto filmSessionDto) {
        Collection<Ticket> tickets = filmSessionDto.tickets();
        int[][] mapPlaces = new int[filmSessionDto.hall().getRowCount()][filmSessionDto.hall().getPlaceCount()];
        for (Ticket ticket : tickets) {
            mapPlaces[ticket.getRowNumber()][ticket.getPlaceNumber()] = 1;
        }
        return mapPlaces;
    }
}
