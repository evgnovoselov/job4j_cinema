package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.cinema.model.File;
import ru.job4j.cinema.repository.utility.Sql2oRepositoryTestUtility;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class Sql2oFileRepositoryTest {
    private static Sql2oFileRepository sql2oFileRepository;

    @BeforeAll
    public static void beforeAll() throws IOException {
        Sql2o sql2o = Sql2oRepositoryTestUtility.getSql2o();
        Sql2oRepositoryTestUtility.cleanDatabase(sql2o);
        sql2oFileRepository = new Sql2oFileRepository(sql2o);
    }

    @AfterEach
    public void tearDown() {
        sql2oFileRepository.findAll().stream().map(File::getId).forEach(sql2oFileRepository::deleteById);
    }

    @Test
    public void whenSaveThenGetById() {
        File file = sql2oFileRepository.save(new File(0, "name", "path1")).orElseThrow();

        File savedFile = sql2oFileRepository.findById(file.getId()).orElseThrow();

        assertThat(savedFile).usingRecursiveComparison().isEqualTo(file);
    }

    @Test
    public void whenSaveSeveralThenGetAll() {
        File file = sql2oFileRepository.save(new File(0, "name", "path1")).orElseThrow();
        File file1 = sql2oFileRepository.save(new File(0, "name1", "path2")).orElseThrow();
        File file2 = sql2oFileRepository.save(new File(0, "name2", "path3")).orElseThrow();
        Collection<File> files = sql2oFileRepository.findAll();

        assertThat(files).usingRecursiveComparison().isEqualTo(List.of(file, file1, file2));
    }

    @Test
    public void whenSaveSeveralWithSameNameThenSaveOnlyFirst() {
        String path = "path";
        File file = new File(0, "name", path);
        File file1 = new File(0, "name1", path);
        File file2 = new File(0, "name2", path);

        Optional<File> savedFileOptional = sql2oFileRepository.save(file);
        Optional<File> savedFileOptional1 = sql2oFileRepository.save(file1);
        Optional<File> savedFileOptional2 = sql2oFileRepository.save(file2);

        assertThat(savedFileOptional).usingRecursiveComparison().isEqualTo(Optional.of(file));
        assertThat(savedFileOptional1).isEqualTo(Optional.empty());
        assertThat(savedFileOptional2).isEqualTo(Optional.empty());
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(sql2oFileRepository.findAll()).isEqualTo(Collections.emptyList());
        assertThat(sql2oFileRepository.findById(1)).isEqualTo(Optional.empty());
    }

    @Test
    public void whenDeleteThenGetEmptyList() {
        File file = sql2oFileRepository.save(new File(0, "name", "path")).orElseThrow();

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