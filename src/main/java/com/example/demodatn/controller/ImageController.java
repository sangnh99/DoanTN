package com.example.demodatn.controller;

import com.example.demodatn.domain.ResponseDataAPI;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@CrossOrigin("*")
@RestController
@RequestMapping("/image")
public class ImageController {

    @RequestMapping(value = "/upload" ,method = RequestMethod.POST)
    public ResponseEntity<ResponseDataAPI> uploadImage(@RequestParam("filea") MultipartFile file){
        Path filepath = Path.of("imagebbb.jpg");

        try (OutputStream os = Files.newOutputStream(filepath)) {
            os.write(file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }
}
