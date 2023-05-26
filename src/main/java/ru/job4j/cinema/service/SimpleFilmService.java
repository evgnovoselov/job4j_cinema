package ru.job4j.cinema.service;

import org.springframework.stereotype.Service;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.Genre;
import ru.job4j.cinema.repository.FilmRepository;
import ru.job4j.cinema.repository.GenreRepository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
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
        return films.stream().map(film -> createFilmDto(film, genreRepository.findAll())).toList();
    }

    @Override
    public Optional<FilmDto> findById(int id) {
        Optional<Film> filmOptional = filmRepository.findById(id);
        if (filmOptional.isEmpty()) {
            return Optional.empty();
        }
        Film film = filmOptional.get();
        return Optional.of(createFilmDto(film, genreRepository.findAll()));
    }

    private static FilmDto createFilmDto(Film film, Collection<Genre> genres) {
        Map<Integer, String> genresMap = genres.stream().collect(Collectors.toMap(Genre::getId, Genre::getName));
        return new FilmDto(film.getId(), film.getName(), film.getDescription(),
                film.getYear(), genresMap.get(film.getGenreId()), film.getMinimalAge(), film.getDurationInMinutes(),
                film.getFileId());
    }
}
