package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.cinema.configuration.DataSourceConfiguration;
import ru.job4j.cinema.model.*;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class Sql2oFilmSessionRepositoryTest {
    private static Sql2oFilmSessionRepository sql2oFilmSessionRepository;
    private static Film testFilm;
    private static Hall testHall;
    private static Genre testGenre;
    private static File testFile;
    private static LocalDateTime testTime = LocalDateTime.of(2023, 8, 7, 8, 0);

    @BeforeAll
    public static void beforeAll() throws IOException {
        Properties properties = new Properties();
        try (InputStream inputStream = Sql2oFilmSessionRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        String url = properties.getProperty("datasource.url");
        String username = properties.getProperty("datasource.username");
        String password = properties.getProperty("datasource.password");
        DataSourceConfiguration configuration = new DataSourceConfiguration();
        DataSource dataSource = configuration.connectionPool(url, username, password);
        Sql2o sql2o = configuration.databaseClient(dataSource);
        sql2oFilmSessionRepository = new Sql2oFilmSessionRepository(sql2o);
        deleteAll();
        Sql2oFilmRepository sql2oFilmRepository = new Sql2oFilmRepository(sql2o);
        sql2oFilmRepository.findAll().stream().map(Film::getId).forEach(sql2oFilmRepository::deleteById);
        Sql2oHallRepository sql2oHallRepository = new Sql2oHallRepository(sql2o);
        sql2oHallRepository.findAll().stream().map(Hall::getId).forEach(sql2oHallRepository::deleteById);
        testHall = sql2oHallRepository.save(new Hall(0, "nameHall", 3, 7, "description hall"));
        Sql2oGenreRepository sql2oGenreRepository = new Sql2oGenreRepository(sql2o);
        sql2oGenreRepository.findAll().stream().map(Genre::getId).forEach(sql2oGenreRepository::deleteById);
        testGenre = sql2oGenreRepository.save(new Genre(0, "genreName")).orElseThrow();
        Sql2oFileRepository sql2oFileRepository = new Sql2oFileRepository(sql2o);
        sql2oFileRepository.findAll().stream().map(File::getId).forEach(sql2oFileRepository::deleteById);
        testFile = sql2oFileRepository.save(new File(0, "name", "path"));
        testFilm = sql2oFilmRepository.save(makeFilm());
    }

    private static Film makeFilm() {
        return new Film() {{
            setId(0);
            setName("name");
            setDescription("description");
            setYear(2000);
            setGenreId(testGenre.getId());
            setMinimalAge(16);
            setDurationInMinutes(120);
            setFileId(testFile.getId());
        }};
    }

    @AfterEach
    public void tearDown() {
        deleteAll();
    }

    private static void deleteAll() {
        sql2oFilmSessionRepository.findAll().stream().map(FilmSession::getId).forEach(sql2oFilmSessionRepository::deleteById);
    }

    private static FilmSession makeFilmSession(int seed) {
        LocalDateTime time = testTime.truncatedTo(ChronoUnit.MINUTES);
        return new FilmSession() {{
            setId(0);
            setFilmId(testFilm.getId());
            setHallsId(testHall.getId());
            setStartTime(time.plusHours(seed % 10));
            setEndTime(time.plusHours(1 + (seed % 10)));
            setPrice(100 + (seed % 100));
        }};
    }

    @Test
    public void whenSaveSeveralThenGetAll() {
        FilmSession filmSession = sql2oFilmSessionRepository.save(makeFilmSession(1));
        FilmSession filmSession1 = sql2oFilmSessionRepository.save(makeFilmSession(2));
        FilmSession filmSession2 = sql2oFilmSessionRepository.save(makeFilmSession(3));
        Collection<FilmSession> filmSessions = sql2oFilmSessionRepository.findAll();

        assertThat(filmSessions).usingRecursiveComparison().isEqualTo(List.of(filmSession, filmSession1, filmSession2));
    }

    @Test
    public void whenSaveSeveralDifferentStartTimeDayAndGetTodayThenGetToday() {
        FilmSession filmSession = sql2oFilmSessionRepository.save(makeFilmSession(1));
        FilmSession filmSession1 = makeFilmSession(2);
        filmSession1.setStartTime(filmSession1.getStartTime().plusDays(1));
        filmSession1.setEndTime(filmSession1.getEndTime().plusDays(1));
        sql2oFilmSessionRepository.save(filmSession1);
        FilmSession filmSession2 = sql2oFilmSessionRepository.save(makeFilmSession(3));
        Collection<FilmSession> filmSessions = sql2oFilmSessionRepository.findAllByDate(testTime.toLocalDate());

        assertThat(filmSessions).usingRecursiveComparison().isEqualTo(List.of(filmSession, filmSession2));
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(sql2oFilmSessionRepository.findAll()).isEqualTo(Collections.emptyList());
        assertThat(sql2oFilmSessionRepository.findAllByDate(testTime.toLocalDate())).isEqualTo(Collections.emptyList());
        assertThat(sql2oFilmSessionRepository.findById(1)).isEqualTo(Optional.empty());
    }

    @Test
    public void whenDeleteThenGetEmptyList() {
        FilmSession filmSession = sql2oFilmSessionRepository.save(makeFilmSession(1));

        boolean isDeleted = sql2oFilmSessionRepository.deleteById(filmSession.getId());
        Collection<FilmSession> filmSessions = sql2oFilmSessionRepository.findAll();

        assertThat(isDeleted).isTrue();
        assertThat(filmSessions).isEqualTo(Collections.emptyList());
    }

    @Test
    public void whenDeleteByInvalidIdThenGetFalse() {
        assertThat(sql2oFilmSessionRepository.deleteById(1)).isFalse();
    }
}