package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.cinema.model.File;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.Genre;
import ru.job4j.cinema.repository.utility.Sql2oRepositoryTestUtility;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class Sql2oFilmRepositoryTest {
    private static Sql2oFilmRepository sql2oFilmRepository;
    private static Genre testGenre;
    private static File testFile;

    @BeforeAll
    static void beforeAll() throws IOException {
        Sql2o sql2o = Sql2oRepositoryTestUtility.getSql2o();
        Sql2oRepositoryTestUtility.cleanDatabase(sql2o);
        sql2oFilmRepository = new Sql2oFilmRepository(sql2o);
        testGenre = new Sql2oGenreRepository(sql2o).save(new Genre(0, "genreName")).orElseThrow();
        testFile = new Sql2oFileRepository(sql2o).save(new File(0, "name", "path")).orElseThrow();
    }

    @AfterEach
    public void tearDown() {
        sql2oFilmRepository.findAll().stream().map(Film::getId).forEach(sql2oFilmRepository::deleteById);
    }

    private static Film makeFilm(int seed) {
        return new Film.Builder()
                .buildId(seed)
                .buildName("name" + seed)
                .buildDescription("description" + seed)
                .buildYear(2000)
                .buildGenreId(testGenre.getId())
                .buildMinimalAge(16)
                .buildDurationInMinutes(120)
                .buildFileId(testFile.getId())
                .build();
    }

    @Test
    public void whenSaveSeveralThenGetAll() {
        Film film = sql2oFilmRepository.save(makeFilm(1));
        Film film1 = sql2oFilmRepository.save(makeFilm(2));
        Film film2 = sql2oFilmRepository.save(makeFilm(3));
        Collection<Film> films = sql2oFilmRepository.findAll();

        assertThat(films).usingRecursiveComparison().isEqualTo(List.of(film, film1, film2));
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(sql2oFilmRepository.findAll()).isEqualTo(Collections.emptyList());
        assertThat(sql2oFilmRepository.findById(1)).isEqualTo(Optional.empty());
    }

    @Test
    public void whenFindByIdThenGetFilm() {
        Film film = sql2oFilmRepository.save(makeFilm(1));
        Optional<Film> savedFilm = sql2oFilmRepository.findById(film.getId());

        assertThat(savedFilm).usingRecursiveComparison().isEqualTo(Optional.of(film));
    }

    @Test
    public void whenDeleteThenGetEmptyList() {
        Film film = sql2oFilmRepository.save(makeFilm(1));

        boolean isDeleted = sql2oFilmRepository.deleteById(film.getId());
        Collection<Film> films = sql2oFilmRepository.findAll();

        assertThat(isDeleted).isTrue();
        assertThat(films).isEqualTo(Collections.emptyList());
    }

    @Test
    public void whenDeleteByInvalidIdThenGetFalse() {
        assertThat(sql2oFilmRepository.deleteById(1)).isFalse();
    }
}