package ru.job4j.cinema.repository;

import ru.job4j.cinema.model.File;

import java.util.Collection;
import java.util.Optional;

public interface FileRepository {
    File save(File file);

    Collection<File> findAll();

    Optional<File> findById(int id);

    boolean deleteById(int id);
}
