package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.cinema.model.*;
import ru.job4j.cinema.repository.utility.Sql2oRepositoryTestUtility;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class Sql2oTicketRepositoryTest {
    private static Sql2oTicketRepository sql2oTicketRepository;
    private static User testUser;
    private static FilmSession testFilmSession;

    @BeforeAll
    public static void beforeAll() throws IOException {
        Sql2o sql2o = Sql2oRepositoryTestUtility.getSql2o();
        Sql2oRepositoryTestUtility.cleanDatabase(sql2o);
        sql2oTicketRepository = new Sql2oTicketRepository(sql2o);
        testUser = new Sql2oUserRepository(sql2o).save(new User(0, "fullName", "name@example.com", "password")).orElseThrow();
        Genre genre = new Sql2oGenreRepository(sql2o).save(new Genre(0, "genreName")).orElseThrow();
        File file = new Sql2oFileRepository(sql2o).save(new File(0, "name", "path")).orElseThrow();
        Film film = new Sql2oFilmRepository(sql2o).save(new Film() {{
            setName("filmName");
            setDescription("filmDescription");
            setGenreId(genre.getId());
            setFileId(file.getId());
        }});
        Hall hall = new Sql2oHallRepository(sql2o).save(new Hall(0, "nameHall", 3, 7, "description hall"));
        testFilmSession = new Sql2oFilmSessionRepository(sql2o).save(new FilmSession(
                0,
                film.getId(),
                hall.getId(),
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                150
        ));
    }

    @AfterEach
    public void tearDown() {
        sql2oTicketRepository.findAll().stream().map(Ticket::getId).forEach(sql2oTicketRepository::deleteById);
    }

    private static Ticket makeTicket(int rowNumber, int placeNumber) {
        return new Ticket(0, testFilmSession.getId(), rowNumber, placeNumber, testUser.getId());
    }

    @Test
    public void whenSaveSeveralThenGetAll() {
        Ticket ticket = sql2oTicketRepository.save(makeTicket(2, 5)).orElseThrow();
        Ticket ticket1 = sql2oTicketRepository.save(makeTicket(2, 6)).orElseThrow();
        Ticket ticket2 = sql2oTicketRepository.save(makeTicket(1, 3)).orElseThrow();
        Collection<Ticket> tickets = sql2oTicketRepository.findAll();

        assertThat(tickets).usingRecursiveComparison().isEqualTo(List.of(ticket, ticket1, ticket2));
    }

    @Test
    public void whenSaveSamePlaceThenHaveOnlyFirst() {
        Optional<Ticket> ticket = sql2oTicketRepository.save(makeTicket(1, 4));
        Optional<Ticket> ticket1 = sql2oTicketRepository.save(makeTicket(1, 4));
        Optional<Ticket> ticket2 = sql2oTicketRepository.save(makeTicket(1, 4));
        Collection<Ticket> tickets = sql2oTicketRepository.findAll();

        assertThat(tickets).isEqualTo(List.of(ticket.orElseThrow()));
        assertThat(ticket1).isEqualTo(Optional.empty());
        assertThat(ticket2).isEqualTo(Optional.empty());
    }

    @Test
    public void whenFindAllBySessionIdThenGetTickets() {
        Ticket ticket = sql2oTicketRepository.save(makeTicket(1, 2)).orElseThrow();
        Ticket ticket1 = sql2oTicketRepository.save(makeTicket(2, 3)).orElseThrow();
        Ticket ticket2 = sql2oTicketRepository.save(makeTicket(1, 5)).orElseThrow();
        Collection<Ticket> tickets = sql2oTicketRepository.findAllBySessionId(testFilmSession.getId());

        assertThat(tickets).usingRecursiveComparison().isEqualTo(List.of(ticket, ticket1, ticket2));
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(sql2oTicketRepository.findAll()).isEqualTo(Collections.emptyList());
        assertThat(sql2oTicketRepository.findAllBySessionId(1)).isEqualTo(Collections.emptyList());
    }

    @Test
    public void whenDeleteThenGetEmptyList() {
        Ticket ticket = sql2oTicketRepository.save(makeTicket(1, 1)).orElseThrow();

        boolean isDeleted = sql2oTicketRepository.deleteById(ticket.getId());
        Collection<Ticket> allTickets = sql2oTicketRepository.findAll();
        Collection<Ticket> allBySessionIdTickets = sql2oTicketRepository.findAllBySessionId(ticket.getSessionId());

        assertThat(isDeleted).isTrue();
        assertThat(allTickets).isEqualTo(Collections.emptyList());
        assertThat(allBySessionIdTickets).isEqualTo(Collections.emptyList());
    }

    @Test
    public void whenDeleteByIdInvalidIdThenGetFalse() {
        assertThat(sql2oTicketRepository.deleteById(1)).isFalse();
    }
}