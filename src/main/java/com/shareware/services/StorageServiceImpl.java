package com.shareware.services;

import com.shareware.model.FileMetadata;
import com.shareware.uploadingfiles.storage.StorageService;
import com.shareware.utils.MetadataUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Service
public class StorageServiceImpl implements StorageService {
    private final Path root = Paths.get("/Users/riteshgoel/Documents/GitHub/shareware2/uploads");
    ;

    @Override
    public void init() {
        try {
            Files.createDirectory(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    @Override
    public void store(MultipartFile file) {
        FileMetadata fileMetadata = MetadataUtils.getFileMetadata(file);
        File f = new File(fileMetadata.getHashedFilename());
        try (OutputStream outputStream = new FileOutputStream(f)) {
            IOUtils.copy(file.getInputStream(), outputStream);
            writeMetadataFile(fileMetadata);
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

    public void writeMetadataFile(FileMetadata fileMetadata) {
        File metadataFile = new File(fileMetadata.getMetadataPath());
        try (PrintStream out = new PrintStream(new FileOutputStream(metadataFile))) {
            out.print(fileMetadata.toJson());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.root, 1).filter(path -> !path.equals(this.root)).map(this.root::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Could not load the files!");
        }
    }

    @Override
    public Path load(String filename) {
        return null;
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(root.toFile());
    }
}
