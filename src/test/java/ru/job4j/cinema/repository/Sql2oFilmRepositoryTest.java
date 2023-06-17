package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.cinema.configuration.DataSourceConfiguration;
import ru.job4j.cinema.model.File;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.model.Genre;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class Sql2oFilmRepositoryTest {
    private static Sql2oFilmRepository sql2oFilmRepository;
    private static Genre testGenre;
    private static File testFile;

    @BeforeAll
    static void beforeAll() throws IOException {
        Properties properties = new Properties();
        try (InputStream inputStream = Sql2oFilmRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        String url = properties.getProperty("datasource.url");
        String username = properties.getProperty("datasource.username");
        String password = properties.getProperty("datasource.password");
        DataSourceConfiguration configuration = new DataSourceConfiguration();
        DataSource dataSource = configuration.connectionPool(url, username, password);
        Sql2o sql2o = configuration.databaseClient(dataSource);
        sql2oFilmRepository = new Sql2oFilmRepository(sql2o);
        Sql2oFilmSessionRepository sql2oFilmSessionRepository = new Sql2oFilmSessionRepository(sql2o);
        sql2oFilmSessionRepository.findAll().stream().map(FilmSession::getId).forEach(sql2oFilmSessionRepository::deleteById);
        Sql2oGenreRepository sql2oGenreRepository = new Sql2oGenreRepository(sql2o);
        Sql2oFileRepository sql2oFileRepository = new Sql2oFileRepository(sql2o);
        deleteAll();
        sql2oGenreRepository.findAll().stream().map(Genre::getId).forEach(sql2oGenreRepository::deleteById);
        sql2oFileRepository.findAll().stream().map(File::getId).forEach(sql2oFileRepository::deleteById);
        testGenre = sql2oGenreRepository.save(new Genre(0, "genreName")).orElseThrow();
        testFile = sql2oFileRepository.save(new File(0, "name", "path"));
    }

    @AfterEach
    public void tearDown() {
        deleteAll();
    }

    private static void deleteAll() {
        sql2oFilmRepository.findAll().stream().map(Film::getId).forEach(sql2oFilmRepository::deleteById);
    }

    private static Film makeFilm(int seed) {
        return new Film() {{
            setId(0);
            setName("name" + seed);
            setDescription("description" + seed);
            setYear(2000 + (seed % 100));
            setGenreId(testGenre.getId());
            setMinimalAge(10 + (seed % 10));
            setDurationInMinutes(100 + (seed % 100));
            setFileId(testFile.getId());
        }};
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