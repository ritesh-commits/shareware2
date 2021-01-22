package com.shareware.controllers;

import com.shareware.services.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LinkController {

    @Autowired
    LinkService linkService;

    @PostMapping("/upload_link")
    public String generateLink() {
        return linkService.generate().toJson();
    }
}
