package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.cinema.configuration.DataSourceConfiguration;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.model.Hall;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class Sql2oHallRepositoryTest {
    private static Sql2oHallRepository sql2oHallRepository;

    @BeforeAll
    public static void beforeAll() throws IOException {
        Properties properties = new Properties();
        try (InputStream inputStream = Sql2oHallRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
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
        sql2oHallRepository = new Sql2oHallRepository(sql2o);
        deleteAll();
    }

    private static void deleteAll() {
        sql2oHallRepository.findAll().stream().map(Hall::getId).forEach(sql2oHallRepository::deleteById);
    }

    @AfterEach
    public void tearDown() {
        deleteAll();
    }

    private static Hall makeHall(int seed) {
        return new Hall(0, "name" + seed, 3, 7, "description" + seed);
    }

    @Test
    public void whenSaveThenGetById() {
        Hall hall = sql2oHallRepository.save(makeHall(1));

        Hall savedHall = sql2oHallRepository.findById(hall.getId()).orElseThrow();

        assertThat(savedHall).usingRecursiveComparison().isEqualTo(hall);
    }

    @Test
    public void whenSaveSeveralThenGetAll() {
        Hall hall = sql2oHallRepository.save(makeHall(1));
        Hall hall1 = sql2oHallRepository.save(makeHall(2));
        Hall hall2 = sql2oHallRepository.save(makeHall(3));
        Collection<Hall> halls = sql2oHallRepository.findAll();

        assertThat(halls).isEqualTo(List.of(hall, hall1, hall2));
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(sql2oHallRepository.findAll()).isEqualTo(Collections.emptyList());
        assertThat(sql2oHallRepository.findById(1)).isEqualTo(Optional.empty());
    }

    @Test
    public void whenDeleteThenGetEmptyList() {
        Hall hall = sql2oHallRepository.save(makeHall(1));

        boolean isDeleted = sql2oHallRepository.deleteById(hall.getId());
        Collection<Hall> halls = sql2oHallRepository.findAll();

        assertThat(isDeleted).isTrue();
        assertThat(halls).isEqualTo(Collections.emptyList());
    }

    @Test
    public void whenDeleteByInvalidIdThenGetFalse() {
        assertThat(sql2oHallRepository.deleteById(1)).isFalse();
    }
}