package com.book_shop.bookstore.uploads.application.port;

import com.book_shop.bookstore.uploads.domain.Upload;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Optional;

public interface UploadUseCase {

    Upload save(SaveUploadCommand command);
    Optional<Upload> getById(Long id);
    void removeById(Long id);

    @Value
    @AllArgsConstructor
    class SaveUploadCommand {
        String fileName;
        byte[] file;
        String contentType;
    }
}