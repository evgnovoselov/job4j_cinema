package ru.job4j.cinema.dto;

public record FilmDto(int id, String name, String description, int year, String genre, int minimalAge,
                      int durationInMinutes, int fileId) {
}
