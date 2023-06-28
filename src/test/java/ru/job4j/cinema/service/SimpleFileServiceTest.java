package ru.job4j.cinema.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.dto.FileDto;
import ru.job4j.cinema.model.File;
import ru.job4j.cinema.repository.FileRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatRuntimeException;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SimpleFileServiceTest {
    private SimpleFileService simpleFileService;
    private FileRepository fileRepository;
    private Path tempDirectory;
    private File tempFile;

    @BeforeEach
    public void setUp() throws IOException {
        tempDirectory = Files.createTempDirectory("tmp-test-job4j-cinema");
        tempFile = new File(1, "tempFileCinema.tmp", "tempFileCinema.tmp");
        Path tempFilePath = Files.createFile(Path.of(tempDirectory.toString(), tempFile.getPath()));
        Files.write(tempFilePath, new byte[]{1, 2, 3, 4, 5});
        fileRepository = mock(FileRepository.class);
        simpleFileService = new SimpleFileService(tempDirectory.toString(), fileRepository);
    }

    @Test
    public void whenFindByIdThenGetDto() {
        when(fileRepository.findById(anyInt())).thenReturn(Optional.of(tempFile));

        Optional<FileDto> actualFileDto = simpleFileService.findById(1);

        assertThat(actualFileDto).usingRecursiveComparison().isEqualTo(Optional.of(new FileDto(tempFile.getName(), new byte[]{1, 2, 3, 4, 5})));
    }

    @Test
    public void whenFindByIdNotHaveThenGetEmpty() {
        when(fileRepository.findById(anyInt())).thenReturn(Optional.empty());

        Optional<FileDto> actualFileDto = simpleFileService.findById(1);

        assertThat(actualFileDto).isEqualTo(Optional.empty());
    }

    @Test
    public void whenExceptionCreateDirectoryThenException() {
        String storageDirectory = Path.of(tempDirectory.toString(), tempFile.getPath()).toString();
        assertThatRuntimeException().isThrownBy(() -> new SimpleFileService(storageDirectory, fileRepository));
    }

    @Test
    public void whenExceptionReadFileThenException() {
        when(fileRepository.findById(anyInt())).thenReturn(Optional.of(new File(1, "no-have", "no-have")));
        assertThatRuntimeException().isThrownBy(() -> simpleFileService.findById(1));
    }
}