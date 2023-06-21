package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.repository.utility.Sql2oRepositoryTestUtility;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class Sql2oHallRepositoryTest {
    private static Sql2oHallRepository sql2oHallRepository;

    @BeforeAll
    public static void beforeAll() throws IOException {
        Sql2o sql2o = Sql2oRepositoryTestUtility.getSql2o();
        Sql2oRepositoryTestUtility.cleanDatabase(sql2o);
        sql2oHallRepository = new Sql2oHallRepository(sql2o);
    }

    @AfterEach
    public void tearDown() {
        sql2oHallRepository.findAll().stream().map(Hall::getId).forEach(sql2oHallRepository::deleteById);
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