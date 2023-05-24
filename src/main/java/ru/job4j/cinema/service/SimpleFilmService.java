package ru.job4j.cinema.service;

import org.springframework.stereotype.Service;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.Genre;
import ru.job4j.cinema.repository.FilmRepository;
import ru.job4j.cinema.repository.GenreRepository;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SimpleFilmService implements FilmService {
    private final FilmRepository filmRepository;
    private final GenreRepository genreRepository;

    public SimpleFilmService(FilmRepository filmRepository, GenreRepository genreRepository) {
        this.filmRepository = filmRepository;
        this.genreRepository = genreRepository;
    }

    @Override
    public Collection<FilmDto> findAll() {
        Collection<Film> films = filmRepository.findAll();
        Collection<Genre> genres = genreRepository.findAll();
        Map<Integer, String> genresMap = genres.stream().collect(Collectors.toMap(Genre::getId, Genre::getName));
        return films.stream().map(film -> {
            FilmDto filmDto = new FilmDto();
            filmDto.setId(film.getId());
            filmDto.setName(film.getName());
            filmDto.setDescription(filmDto.getDescription());
            filmDto.setYear(film.getYear());
            filmDto.setGenre(genresMap.get(film.getGenreId()));
            filmDto.setMinimalAge(film.getMinimalAge());
            filmDto.setDurationInMinutes(film.getDurationInMinutes());
            filmDto.setFileId(film.getFileId());
            return filmDto;
        }).toList();
    }
}
