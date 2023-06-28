package ru.job4j.cinema.repository.utility;

import org.sql2o.Sql2o;
import ru.job4j.cinema.configuration.DataSourceConfiguration;
import ru.job4j.cinema.model.*;
import ru.job4j.cinema.repository.*;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class Sql2oRepositoryTestUtility {
    public static Sql2o getSql2o() throws IOException {
        Properties properties = new Properties();
        try (InputStream inputStream = Sql2oRepositoryTestUtility.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        String url = properties.getProperty("datasource.url");
        String username = properties.getProperty("datasource.username");
        String password = properties.getProperty("datasource.password");
        DataSourceConfiguration configuration = new DataSourceConfiguration();
        DataSource dataSource = configuration.connectionPool(url, username, password);
        return configuration.databaseClient(dataSource);
    }

    public static void cleanDatabase(Sql2o sql2o) {
        Sql2oTicketRepository sql2oTicketRepository = new Sql2oTicketRepository(sql2o);
        sql2oTicketRepository.findAll().stream().map(Ticket::getId).forEach(sql2oTicketRepository::deleteById);
        Sql2oUserRepository sql2oUserRepository = new Sql2oUserRepository(sql2o);
        sql2oUserRepository.findAll().stream().map(User::getId).forEach(sql2oUserRepository::deleteById);
        Sql2oFilmSessionRepository sql2oFilmSessionRepository = new Sql2oFilmSessionRepository(sql2o);
        sql2oFilmSessionRepository.findAll().stream().map(FilmSession::getId).forEach(sql2oFilmSessionRepository::deleteById);
        Sql2oHallRepository sql2oHallRepository = new Sql2oHallRepository(sql2o);
        sql2oHallRepository.findAll().stream().map(Hall::getId).forEach(sql2oHallRepository::deleteById);
        Sql2oFilmRepository sql2oFilmRepository = new Sql2oFilmRepository(sql2o);
        sql2oFilmRepository.findAll().stream().map(Film::getId).forEach(sql2oFilmRepository::deleteById);
        Sql2oGenreRepository sql2oGenreRepository = new Sql2oGenreRepository(sql2o);
        sql2oGenreRepository.findAll().stream().map(Genre::getId).forEach(sql2oGenreRepository::deleteById);
        Sql2oFileRepository sql2oFileRepository = new Sql2oFileRepository(sql2o);
        sql2oFileRepository.findAll().stream().map(File::getId).forEach(sql2oFileRepository::deleteById);
    }
}
