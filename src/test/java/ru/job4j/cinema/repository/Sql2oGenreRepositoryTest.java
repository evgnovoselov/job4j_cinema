package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.cinema.model.Genre;
import ru.job4j.cinema.repository.utility.Sql2oRepositoryTestUtility;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class Sql2oGenreRepositoryTest {
    private static Sql2oGenreRepository sql2oGenreRepository;

    @BeforeAll
    public static void beforeAll() throws IOException {
        Sql2o sql2o = Sql2oRepositoryTestUtility.getSql2o();
        Sql2oRepositoryTestUtility.cleanDatabase(sql2o);
        sql2oGenreRepository = new Sql2oGenreRepository(sql2o);
    }

    @AfterEach
    public void tearDown() {
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