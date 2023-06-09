package ru.job4j.cinema.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IndexControllerTest {

    @Test
    public void whenGetIndexPageThenGetIndexPage() {
        IndexController indexController = new IndexController();

        String view = indexController.index();

        assertThat(view).isEqualTo("index");
    }
}