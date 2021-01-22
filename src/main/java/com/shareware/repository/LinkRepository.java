package com.shareware.repository;

import com.shareware.model.UploadLink;

import java.util.HashMap;
import java.util.Map;

public class LinkRepository {

    public static Map<String, UploadLink> uploadLinkMap = new HashMap<>();

    public static UploadLink getLink(String id) {
        return uploadLinkMap.get(id);
    }

    public static void put(String id, UploadLink uploadLink) {
        uploadLinkMap.put(id, uploadLink);
    }

}
