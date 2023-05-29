package ru.job4j.cinema.repository;

import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import ru.job4j.cinema.model.FilmSession;

import java.time.LocalDate;
import java.util.Collection;

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
}
