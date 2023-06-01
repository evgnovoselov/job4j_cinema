package ru.job4j.cinema.repository;

import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import ru.job4j.cinema.model.FilmSession;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@Repository
public class Sql2oFilmSessionRepository implements FilmSessionRepository {
    private final Sql2o sql2o;

    public Sql2oFilmSessionRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Collection<FilmSession> findAllByDate(LocalDate date) {
        try (Connection connection = sql2o.open()) {
            String sql = "SELECT * FROM film_sessions WHERE start_time > :date AND start_time < :nextDayDate";
            Query query = connection.createQuery(sql)
                    .addParameter("date", date)
                    .addParameter("nextDayDate", date.plusDays(1));
            return query.setColumnMappings(FilmSession.COLUMN_MAPPING).executeAndFetch(FilmSession.class);
        }
    }

    @Override
    public Optional<FilmSession> findById(int id) {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("SELECT * FROM film_sessions WHERE id = :id")
                    .addParameter("id", id);
            FilmSession filmSession = query.setColumnMappings(FilmSession.COLUMN_MAPPING).executeAndFetchFirst(FilmSession.class);
            return Optional.ofNullable(filmSession);
        }
    }
}
