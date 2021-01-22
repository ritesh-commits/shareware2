package com.shareware.utils;

import com.shareware.model.FileMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Base64;

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

}
