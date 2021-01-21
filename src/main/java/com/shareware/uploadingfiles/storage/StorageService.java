package com.shareware.uploadingfiles.storage;

import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;

import org.springframework.core.io.Resource;
import java.util.stream.Stream;

public interface StorageService {

    void init();

    void store(MultipartFile file);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void deleteAll();

}