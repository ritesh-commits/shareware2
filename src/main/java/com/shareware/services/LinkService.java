package com.shareware.services;

import com.shareware.model.UploadLink;
import com.shareware.repository.LinkRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
public class LinkService {

    public UploadLink generate() {
        String randomString = getRandomString();
        String link = "localhost:9876/upload/" + randomString;
        UploadLink uploadLink = new UploadLink(randomString, link, LocalDateTime.now().plusHours(1), false);
        LinkRepository.put(randomString, uploadLink);
        return uploadLink;
    }

    public String getRandomString() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 36;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }

}
