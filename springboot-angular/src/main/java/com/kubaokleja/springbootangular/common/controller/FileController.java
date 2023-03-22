package com.kubaokleja.springbootangular.common.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.io.IOException;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class FileController {

    private final ResourceLoader resourceLoader;

    @GetMapping("template/{filename}")
    ResponseEntity<Resource> downloadTemplate(@PathVariable("filename") String filename) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:" + "templates/" + filename);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("File-Name", filename);
        httpHeaders.add(CONTENT_DISPOSITION, "attachment;File-Name=" + resource.getFilename());

        return ResponseEntity.ok().contentType(MediaType.valueOf("text/csv"))
                .headers(httpHeaders).body(resource);
    }
}
