package ru.job4j.cinema.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import ru.job4j.cinema.model.Genre;

import java.util.Collection;
import java.util.Optional;

@Repository
public class Sql2oGenreRepository implements GenreRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(Sql2oGenreRepository.class.getName());
    private final Sql2o sql2o;

    public Sql2oGenreRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Optional<Genre> save(Genre genre) {
        try (Connection connection = sql2o.open()) {
            String sql = """
                    INSERT INTO genres(name)
                    VALUES (:name)
                    """;
            Query query = connection.createQuery(sql, true)
                    .addParameter("name", genre.getName());
            int generatedId = query.executeUpdate().getKey(Integer.class);
            genre.setId(generatedId);
            return Optional.of(genre);
        } catch (Exception e) {
            LOGGER.warn("Жанр с таким именем уже существует.", e);
        }
        return Optional.empty();
    }

    @Override
    public Collection<Genre> findAll() {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("SELECT * FROM genres");
            return query.executeAndFetch(Genre.class);
        }
    }

    @Override
    public boolean deleteById(int id) {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("DELETE FROM genres WHERE id = :id")
                    .addParameter("id", id);
            int affectedRows = query.executeUpdate().getResult();
            return affectedRows > 0;
        }
    }
}
