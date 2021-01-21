package com.shareware;

import com.shareware.uploadingfiles.storage.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

@SpringBootApplication
public class ShareWareApplication {

    @Resource
    StorageService storageService;

    public static void main(String[] args) {
        SpringApplication.run(ShareWareApplication.class, args);
    }

//    @Override
//    public void run(String... arg) throws Exception {
//        storageService.deleteAll();
//        storageService.init();
//    }
}
