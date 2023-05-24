package ru.job4j.cinema.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.dto.FileDto;
import ru.job4j.cinema.model.File;
import ru.job4j.cinema.repository.FileRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Service
public class SimpleFileService implements FileService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleFileService.class.getName());
    private final String storageDirectory;
    private final FileRepository fileRepository;

    public SimpleFileService(@Value("${file.directory}") String storageDirectory, FileRepository fileRepository) {
        this.storageDirectory = storageDirectory;
        createStorageDirectory(storageDirectory);
        this.fileRepository = fileRepository;
    }

    private void createStorageDirectory(String storageDirectory) {
        try {
            Files.createDirectories(Path.of(storageDirectory));
        } catch (IOException e) {
            LOGGER.error("Error create storage director: {}", storageDirectory);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<FileDto> findById(int id) {
        Optional<File> fileOptional = fileRepository.findById(id);
        if (fileOptional.isEmpty()) {
            return Optional.empty();
        }
        byte[] content = readFileAsByte(fileOptional.get().getPath());
        return Optional.of(new FileDto(fileOptional.get().getName(), content));
    }

    private byte[] readFileAsByte(String path) {
        try {
            return Files.readAllBytes(Path.of(storageDirectory, path));
        } catch (IOException e) {
            LOGGER.error("Error read file from path: {}", path);
            throw new RuntimeException(e);
        }
    }
}
