package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.cinema.dto.FileDto;
import ru.job4j.cinema.service.FileService;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class FileControllerTest {
    private FileService fileService;
    private FileController fileController;
    private MultipartFile testFile;

    @BeforeEach
    public void setUp() {
        fileService = mock(FileService.class);
        fileController = new FileController(fileService);
        testFile = new MockMultipartFile("testFile.jpg", new byte[]{1, 2, 3});
    }

    @Test
    public void whenGetFileWithIdThenReturnResponseFile() throws IOException {
        FileDto fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        when(fileService.findById(anyInt())).thenReturn(Optional.of(fileDto));

        ResponseEntity<?> actualResponse = fileController.getById(1);

        assertThat(actualResponse).isEqualTo(ResponseEntity.ok(testFile.getBytes()));
    }

    @Test
    public void whenGetFileButItsIdNotHaveThenReturnResponseNotFound() {
        when(fileService.findById(anyInt())).thenReturn(Optional.empty());

        ResponseEntity<?> actualResponse = fileController.getById(1);

        assertThat(actualResponse).isEqualTo(ResponseEntity.notFound().build());
    }
}