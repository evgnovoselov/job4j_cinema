package ru.job4j.cinema.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.Genre;
import ru.job4j.cinema.repository.FilmRepository;
import ru.job4j.cinema.repository.GenreRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class SimpleFilmServiceTest {
    private SimpleFilmService simpleFilmService;
    private FilmRepository filmRepository;
    private GenreRepository genreRepository;

    @BeforeEach
    public void setUp() {
        filmRepository = mock(FilmRepository.class);
        genreRepository = mock(GenreRepository.class);
        simpleFilmService = new SimpleFilmService(filmRepository, genreRepository);
    }

    private static Film makeFilm(int seed) {
        return new Film() {{
            setId(seed);
            setName("name" + seed);
            setDescription("description" + seed);
            setYear(2000);
            setGenreId(1);
            setMinimalAge(16);
            setDurationInMinutes(120);
            setFileId(1);
        }};
    }

    @Test
    public void whenFindAllThenGetAllFilmDto() {
        Film film = makeFilm(1);
        Film film1 = makeFilm(2);
        Film film2 = makeFilm(3);
        when(filmRepository.findAll()).thenReturn(List.of(film, film1, film2));
        Genre genre = new Genre(1, "genre");
        when(genreRepository.findAll()).thenReturn(List.of(genre));
        List<FilmDto> expectedFilmDtoList = List.of(
                new FilmDto(1, "name1", "description1", 2000, "genre", 16, 120, 1),
                new FilmDto(2, "name2", "description2", 2000, "genre", 16, 120, 1),
                new FilmDto(3, "name3", "description3", 2000, "genre", 16, 120, 1)
        );

        Collection<FilmDto> filmDtoCollection = simpleFilmService.findAll();

        assertThat(filmDtoCollection).usingRecursiveComparison().isEqualTo(expectedFilmDtoList);
    }

    @Test
    public void whenFindByIdThenGetFilmDto() {
        Film film = makeFilm(1);
        when(filmRepository.findById(1)).thenReturn(Optional.of(film));
        Genre genre = new Genre(1, "genre");
        when(genreRepository.findAll()).thenReturn(List.of(genre));

        Optional<FilmDto> filmDto = simpleFilmService.findById(1);

        assertThat(filmDto).usingRecursiveComparison().isEqualTo(Optional.of(
                new FilmDto(1, "name1", "description1", 2000, "genre", 16, 120, 1)
        ));
    }

    @Test
    public void whenFindByIdNotHaveThenEmpty() {
        when(filmRepository.findById(anyInt())).thenReturn(Optional.empty());

        Optional<FilmDto> filmDto = simpleFilmService.findById(1);

        assertThat(filmDto).isEqualTo(Optional.empty());
    }
}