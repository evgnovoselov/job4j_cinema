package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.cinema.model.*;
import ru.job4j.cinema.repository.utility.Sql2oRepositoryTestUtility;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class Sql2oFilmSessionRepositoryTest {
    private static final LocalDateTime DATE_TIME = LocalDateTime.of(2023, 8, 7, 8, 0);
    private static Sql2oFilmSessionRepository sql2oFilmSessionRepository;
    private static Film testFilm;
    private static Hall testHall;
    private static Genre testGenre;
    private static File testFile;

    @BeforeAll
    public static void beforeAll() throws IOException {
        Sql2o sql2o = Sql2oRepositoryTestUtility.getSql2o();
        Sql2oRepositoryTestUtility.cleanDatabase(sql2o);
        sql2oFilmSessionRepository = new Sql2oFilmSessionRepository(sql2o);
        testHall = new Sql2oHallRepository(sql2o).save(new Hall(0, "nameHall", 3, 7, "description hall"));
        testGenre = new Sql2oGenreRepository(sql2o).save(new Genre(0, "genreName")).orElseThrow();
        testFile = new Sql2oFileRepository(sql2o).save(new File(0, "name", "path")).orElseThrow();
        testFilm = new Sql2oFilmRepository(sql2o).save(makeFilm());
    }

    private static Film makeFilm() {
        return new Film.Builder()
                .buildId(0)
                .buildName("name")
                .buildDescription("description")
                .buildYear(2000)
                .buildGenreId(testGenre.getId())
                .buildMinimalAge(16)
                .buildDurationInMinutes(120)
                .buildFileId(testFile.getId())
                .build();
    }

    @AfterEach
    public void tearDown() {
        sql2oFilmSessionRepository.findAll().stream().map(FilmSession::getId).forEach(sql2oFilmSessionRepository::deleteById);
    }

    private static FilmSession makeFilmSession(int seed) {
        LocalDateTime time = DATE_TIME.truncatedTo(ChronoUnit.MINUTES);
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
        Collection<FilmSession> filmSessions = sql2oFilmSessionRepository.findAllByDate(DATE_TIME.toLocalDate());

        assertThat(filmSessions).usingRecursiveComparison().isEqualTo(List.of(filmSession, filmSession2));
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(sql2oFilmSessionRepository.findAll()).isEqualTo(Collections.emptyList());
        assertThat(sql2oFilmSessionRepository.findAllByDate(DATE_TIME.toLocalDate())).isEqualTo(Collections.emptyList());
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