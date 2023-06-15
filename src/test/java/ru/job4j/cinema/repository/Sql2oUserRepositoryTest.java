package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.cinema.configuration.DataSourceConfiguration;
import ru.job4j.cinema.model.User;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class Sql2oUserRepositoryTest {
    private static Sql2oUserRepository sql2oUserRepository;

    @BeforeAll
    public static void beforeAll() throws IOException {
        Properties properties = new Properties();
        try (InputStream inputStream = Sql2oUserRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        String url = properties.getProperty("datasource.url");
        String username = properties.getProperty("datasource.username");
        String password = properties.getProperty("datasource.password");
        DataSourceConfiguration configuration = new DataSourceConfiguration();
        DataSource dataSource = configuration.connectionPool(url, username, password);
        Sql2o sql2o = configuration.databaseClient(dataSource);
        sql2oUserRepository = new Sql2oUserRepository(sql2o);
    }

    @AfterEach
    public void tearDown() {
        Collection<User> users = sql2oUserRepository.findAll();
        for (User user : users) {
            sql2oUserRepository.deleteById(user.getId());
        }
    }

    @Test
    public void whenSaveThenGetSame() {
        String email = "name@example.com";
        String password = "password";

        User user = sql2oUserRepository.save(new User(0, "Name", email, password)).orElseThrow();
        User savedUser = sql2oUserRepository.findByEmailAndPassword(email, password).orElseThrow();

        assertThat(savedUser).usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    public void whenSaveSeveralWithSameEmailThenSaveOnlyFirst() {
        String fullName = "name";
        String email = "name@example.com";
        String password = "password";
        User user1 = new User(0, fullName, email, password);
        User user2 = new User(0, fullName, email, password);
        User user3 = new User(0, fullName, email, password);

        Optional<User> savedOptionalUser1 = sql2oUserRepository.save(user1);
        Optional<User> savedOptionalUser2 = sql2oUserRepository.save(user2);
        Optional<User> savedOptionalUser3 = sql2oUserRepository.save(user3);

        assertThat(savedOptionalUser1).usingRecursiveComparison().isEqualTo(Optional.of(user1));
        assertThat(savedOptionalUser2).usingRecursiveComparison().isEqualTo(Optional.empty());
        assertThat(savedOptionalUser3).usingRecursiveComparison().isEqualTo(Optional.empty());
    }

    @Test
    public void whenSaveSeveralThenGetAll() {
        String fullName = "name";
        String password = "password";

        User user1 = sql2oUserRepository.save(new User(0, fullName, "name1@example.com", password)).orElseThrow();
        User user2 = sql2oUserRepository.save(new User(0, fullName, "name2@example.com", password)).orElseThrow();
        User user3 = sql2oUserRepository.save(new User(0, fullName, "name3@example.com", password)).orElseThrow();
        Collection<User> users = sql2oUserRepository.findAll();

        assertThat(users).isEqualTo(List.of(user1, user2, user3));
    }

    @Test
    public void whenSaveSeveralWithSameEmailThenGetOnlyFirst() {
        String fullName = "name";
        String email = "name@example.com";
        String password = "password";
        User user = new User(0, fullName, email, password);
        User user1 = new User(0, fullName, email, password);
        User user2 = new User(0, fullName, email, password);

        sql2oUserRepository.save(user);
        sql2oUserRepository.save(user1);
        sql2oUserRepository.save(user2);
        Collection<User> users = sql2oUserRepository.findAll();

        assertThat(users).isEqualTo(List.of(user));
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(sql2oUserRepository.findByEmailAndPassword("name@example.com", "password"))
                .isEqualTo(Optional.empty());
        assertThat(sql2oUserRepository.findAll()).isEqualTo(Collections.emptyList());
    }

    @Test
    public void whenDeleteThenGetEmptyOptional() {
        String email = "name@example.com";
        String password = "password";

        User user = sql2oUserRepository.save(new User(0, "name", email, password)).orElseThrow();
        boolean isDeleted = sql2oUserRepository.deleteById(user.getId());
        Optional<User> savedUser = sql2oUserRepository.findByEmailAndPassword(email, password);

        assertThat(isDeleted).isTrue();
        assertThat(savedUser).isEqualTo(Optional.empty());
    }

    @Test
    public void whenUserFindByIdThenGetOptionalUser() {
        Optional<User> user = sql2oUserRepository.save(new User(0, "name", "name@example.com", "password"));
        Optional<User> savedUser = sql2oUserRepository.findById(user.orElseThrow().getId());

        assertThat(savedUser).usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    public void whenDeletedUserFindByIdThenOptionalEmpty() {
        Optional<User> user = sql2oUserRepository.save(new User(0, "name", "name@example.com", "password"));
        sql2oUserRepository.deleteById(user.orElseThrow().getId());
        Optional<User> savedUser = sql2oUserRepository.findById(user.orElseThrow().getId());

        assertThat(savedUser).usingRecursiveComparison().isEqualTo(Optional.empty());
    }

    @Test
    public void whenDeleteByInvalidIdThenGetFalse() {
        assertThat(sql2oUserRepository.deleteById(0)).isFalse();
    }
}