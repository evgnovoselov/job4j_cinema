package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.cinema.configuration.DataSourceConfiguration;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.model.Genre;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class Sql2oGenreRepositoryTest {
    private static Sql2oGenreRepository sql2oGenreRepository;

    @BeforeAll
    public static void beforeAll() throws IOException {
        Properties properties = new Properties();
        try (InputStream inputStream = Sql2oGenreRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        String url = properties.getProperty("datasource.url");
        String username = properties.getProperty("datasource.username");
        String password = properties.getProperty("datasource.password");
        DataSourceConfiguration configuration = new DataSourceConfiguration();
        DataSource dataSource = configuration.connectionPool(url, username, password);
        Sql2o sql2o = configuration.databaseClient(dataSource);
        sql2oGenreRepository = new Sql2oGenreRepository(sql2o);
        Sql2oFilmSessionRepository sql2oFilmSessionRepository = new Sql2oFilmSessionRepository(sql2o);
        sql2oFilmSessionRepository.findAll().stream().map(FilmSession::getId).forEach(sql2oFilmSessionRepository::deleteById);
        Sql2oFilmRepository sql2oFilmRepository = new Sql2oFilmRepository(sql2o);
        sql2oFilmRepository.findAll().stream().map(Film::getId).forEach(sql2oFilmRepository::deleteById);
        deleteAll();
    }

    @AfterEach
    public void tearDown() {
        deleteAll();
    }

    private static void deleteAll() {
        sql2oGenreRepository.findAll().stream().map(Genre::getId).forEach(sql2oGenreRepository::deleteById);
    }

    @Test
    public void whenSaveSeveralWithSameNameThenSaveOnlyFirst() {
        String name = "genre";
        Genre genre = new Genre(0, name);
        Genre genre1 = new Genre(0, name);
        Genre genre2 = new Genre(0, name);

        Optional<Genre> savedGenreOptional = sql2oGenreRepository.save(genre);
        Optional<Genre> savedGenreOptional1 = sql2oGenreRepository.save(genre1);
        Optional<Genre> savedGenreOptional2 = sql2oGenreRepository.save(genre2);

        assertThat(savedGenreOptional).usingRecursiveComparison().isEqualTo(Optional.of(genre));
        assertThat(savedGenreOptional1).usingRecursiveComparison().isEqualTo(Optional.empty());
        assertThat(savedGenreOptional2).usingRecursiveComparison().isEqualTo(Optional.empty());
    }

    @Test
    public void whenSaveSeveralThenFindAll() {
        Genre genre = sql2oGenreRepository.save(new Genre(0, "name")).orElseThrow();
        Genre genre1 = sql2oGenreRepository.save(new Genre(0, "name1")).orElseThrow();
        Genre genre2 = sql2oGenreRepository.save(new Genre(0, "name2")).orElseThrow();
        Collection<Genre> genres = sql2oGenreRepository.findAll();

        assertThat(genres).isEqualTo(List.of(genre, genre1, genre2));
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(sql2oGenreRepository.findAll()).isEqualTo(Collections.emptyList());
    }

    @Test
    public void whenDeleteThenGetEmptyList() {
        Genre genre = sql2oGenreRepository.save(new Genre(0, "name")).orElseThrow();

        boolean isDeleted = sql2oGenreRepository.deleteById(genre.getId());
        Collection<Genre> genres = sql2oGenreRepository.findAll();

        assertThat(isDeleted).isTrue();
        assertThat(genres).isEqualTo(Collections.emptyList());
    }

    @Test
    public void whenDeleteByInvalidIdThenGetFalse() {
        assertThat(sql2oGenreRepository.deleteById(1)).isFalse();
    }
}