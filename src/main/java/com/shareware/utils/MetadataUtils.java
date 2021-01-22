package com.shareware.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shareware.model.FileMetadata;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

public class MetadataUtils {

    public static FileMetadata getFileMetadata(String linkId, MultipartFile file) {
        FileMetadata fileMetadata = new FileMetadata();
        fileMetadata.setUploadTimestamp(System.currentTimeMillis());
        fileMetadata.setExpiryTimestamp(System.currentTimeMillis() + 10000000);
        fileMetadata.setSize(file.getSize());
        String name = file.getOriginalFilename();
        String hashedName = linkId;
        fileMetadata.setId(hashedName);
        fileMetadata.setOriginalFilename(name);

        fileMetadata.setHashedFilename(hashedName + ".binary");
        fileMetadata.setMetadataPath(fileMetadata.getId() + ".json");

        fileMetadata.setLink("link");

        return fileMetadata;
    }

    public static FileMetadata getfileFromMetadata(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
            String json =  FileCopyUtils.copyToString(reader);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, FileMetadata.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
