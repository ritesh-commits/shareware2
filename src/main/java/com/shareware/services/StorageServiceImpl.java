package com.shareware.services;

import com.shareware.model.FileMetadata;
import com.shareware.model.UploadLink;
import com.shareware.repository.LinkRepository;
import com.shareware.uploadingfiles.storage.StorageService;
import com.shareware.utils.CryptoUtils;
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
import java.time.LocalDateTime;
import java.util.stream.Stream;

@Service
public class StorageServiceImpl implements StorageService {
    private final Path root = Paths.get("/Users/riteshgoel/Documents/GitHub/shareware2/");
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
    public String store(String linkId, MultipartFile file) {
        UploadLink uploadLink = LinkRepository.getLink(linkId);

        if (uploadLink == null || uploadLink.getExpiryTime().isBefore(LocalDateTime.now()) || uploadLink.isExpired()) {
            return "Upload link doesn't exist or expired";
        }

        FileMetadata fileMetadata = MetadataUtils.getFileMetadata(linkId, file);
        File f = new File(fileMetadata.getHashedFilename());
        try (OutputStream outputStream = new FileOutputStream(f)) {
            IOUtils.copy(file.getInputStream(), outputStream);
            writeMetadataFile(fileMetadata);
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
        uploadLink.setExpired(true);
        return "File uploaded successfully: " + fileMetadata.getId();
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
            Path file = root.resolve(filename + ".json");
            Resource resource = new UrlResource(file.toUri());
            FileMetadata fileMetadata = null;
            if (resource.exists() || resource.isReadable()) {
                 fileMetadata = MetadataUtils.getfileFromMetadata(resource);
            } else {
                throw new RuntimeException("Could not read the file!");
            }

            if (fileMetadata.isDownloadExpired()) {
                return null;
            }

            Path binary = root.resolve(filename + ".binary");
            Resource r = new UrlResource(binary.toUri());

            File f = r.getFile();

            File original = new File(fileMetadata.getOriginalFilename());

            f.renameTo(original);

            Path toOriginalFile = root.resolve(fileMetadata.getOriginalFilename());
            Resource originalFile = CryptoUtils.decryptFile(new UrlResource(toOriginalFile.toUri()), null);
            if (originalFile.exists() || originalFile.isReadable()) {
                fileMetadata.setDownloadExpired(true);
                return originalFile;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(root.toFile());
    }
}
