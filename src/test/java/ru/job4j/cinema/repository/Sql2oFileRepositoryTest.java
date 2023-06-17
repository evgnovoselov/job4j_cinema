package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.cinema.configuration.DataSourceConfiguration;
import ru.job4j.cinema.model.File;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.FilmSession;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class Sql2oFileRepositoryTest {
    private static Sql2oFileRepository sql2oFileRepository;

    @BeforeAll
    public static void beforeAll() throws IOException {
        Properties properties = new Properties();
        try (InputStream inputStream = Sql2oFileRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        String url = properties.getProperty("datasource.url");
        String username = properties.getProperty("datasource.username");
        String password = properties.getProperty("datasource.password");
        DataSourceConfiguration configuration = new DataSourceConfiguration();
        DataSource dataSource = configuration.connectionPool(url, username, password);
        Sql2o sql2o = configuration.databaseClient(dataSource);
        Sql2oFilmSessionRepository sql2oFilmSessionRepository = new Sql2oFilmSessionRepository(sql2o);
        sql2oFilmSessionRepository.findAll().stream().map(FilmSession::getId).forEach(sql2oFilmSessionRepository::deleteById);
        Sql2oFilmRepository sql2oFilmRepository = new Sql2oFilmRepository(sql2o);
        sql2oFilmRepository.findAll().stream().map(Film::getId).forEach(sql2oFilmRepository::deleteById);
        sql2oFileRepository = new Sql2oFileRepository(sql2o);
        deleteAll();
    }

    @AfterEach
    public void tearDown() {
        deleteAll();
    }

    private static void deleteAll() {
        sql2oFileRepository.findAll().stream().map(File::getId).forEach(sql2oFileRepository::deleteById);
    }

    @Test
    public void whenSaveThenGetById() {
        File file = sql2oFileRepository.save(new File(0, "name", "path1"));

        File savedFile = sql2oFileRepository.findById(file.getId()).orElseThrow();

        assertThat(savedFile).usingRecursiveComparison().isEqualTo(file);
    }

    @Test
    public void whenSaveSeveralThenGetAll() {
        File file = sql2oFileRepository.save(new File(0, "name", "path1"));
        File file1 = sql2oFileRepository.save(new File(0, "name1", "path2"));
        File file2 = sql2oFileRepository.save(new File(0, "name2", "path3"));
        Collection<File> files = sql2oFileRepository.findAll();

        assertThat(files).usingRecursiveComparison().isEqualTo(List.of(file, file1, file2));
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(sql2oFileRepository.findAll()).isEqualTo(Collections.emptyList());
        assertThat(sql2oFileRepository.findById(1)).isEqualTo(Optional.empty());
    }

    @Test
    public void whenDeleteThenGetEmptyList() {
        File file = sql2oFileRepository.save(new File(0, "name", "path"));

        boolean isDeleted = sql2oFileRepository.deleteById(file.getId());
        Collection<File> files = sql2oFileRepository.findAll();

        assertThat(isDeleted).isTrue();
        assertThat(files).isEqualTo(Collections.emptyList());
    }

    @Test
    public void whenDeleteByInvalidIdThenGetFalse() {
        assertThat(sql2oFileRepository.deleteById(1)).isFalse();
    }
}