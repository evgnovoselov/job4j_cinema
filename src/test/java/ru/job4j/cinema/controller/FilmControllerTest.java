package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.service.FilmService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FilmControllerTest {
    private FilmService filmService;
    private FilmController filmController;

    @BeforeEach
    public void setUp() {
        filmService = mock(FilmService.class);
        filmController = new FilmController(filmService);
    }

    private static FilmDto makeFilmDto(int seed) {
        return new FilmDto(
                seed,
                "name" + seed,
                "description" + seed,
                2010 + seed,
                "genre" + seed,
                10 + seed,
                100 + seed,
                seed
        );
    }

    @Test
    public void whenRequestFilmListPageThenGetPageWithFilmDtoList() {
        FilmDto filmDto1 = makeFilmDto(1);
        FilmDto filmDto2 = makeFilmDto(2);
        Collection<FilmDto> filmDtoList = List.of(filmDto1, filmDto2);
        when(filmService.findAll()).thenReturn(filmDtoList);
        ConcurrentModel model = new ConcurrentModel();

        String view = filmController.getAll(model);
        Collection<FilmDto> actualFilmDtoList = (Collection<FilmDto>) model.getAttribute("films");

        assertThat(view).isEqualTo("films/list");
        assertThat(actualFilmDtoList).usingRecursiveComparison().isEqualTo(filmDtoList);
    }

    @Test
    public void whenGetFilmByIdThenGetFilmDto() {
        FilmDto filmDto = makeFilmDto(1);
        when(filmService.findById(anyInt())).thenReturn(Optional.of(filmDto));
        ConcurrentModel model = new ConcurrentModel();

        String view = filmController.getById(1, model);
        FilmDto actualFilmDto = (FilmDto) model.getAttribute("film");

        assertThat(view).isEqualTo("films/one");
        assertThat(actualFilmDto).usingRecursiveComparison().isEqualTo(filmDto);
    }

    @Test
    public void whenGetFilmByIdButThereNotHaveThenGetErrorPageWithMessage() {
        ConcurrentModel model = new ConcurrentModel();
        when(filmService.findById(anyInt())).thenReturn(Optional.empty());

        String view = filmController.getById(1, model);
        String message = (String) model.getAttribute("message");

        assertThat(view).isEqualTo("error/404");
        assertThat(message).isEqualTo("Фильм по данному адресу отсутствует.");
    }
}
