package com.example.demodatn.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.demodatn.domain.ResponseDataAPI;
import com.example.demodatn.domain.UserAppIdDomain;
import com.example.demodatn.entity.UserAppEntity;
import com.example.demodatn.exception.CustomException;
import com.example.demodatn.repository.FoodRepository;
import com.example.demodatn.repository.UserAppRepository;
import com.example.demodatn.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;


@CrossOrigin("*")
@RestController
@RequestMapping("/image")
public class ImageController {

    @Autowired
    private UserAppRepository userAppRepository;

    @Autowired
    private FoodRepository foodRepository;

    @RequestMapping(value = "/upload-user-avatar/{id}" ,method = RequestMethod.POST)
    public ResponseEntity<ResponseDataAPI> uploadImage(@RequestParam("filea") MultipartFile file, @PathVariable("id") String userAppId) throws IOException {
        Path filepath = Path.of("imagebbb.jpg");
        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "djifhw3lo",
                "api_key", "992726224781494",
                "api_secret", "Tol4roEhAhgOJ3NaNsnAyWDDrD0",
                "secure", true));
        String imageUrl = "";
        try (OutputStream os = Files.newOutputStream(filepath)) {
            os.write(file.getBytes());
            Map uploadResult = cloudinary.uploader().upload(new File("imagebbb.jpg"), ObjectUtils.emptyMap());
            System.out.println(uploadResult.get("url"));
            System.out.println(userAppId);
            imageUrl = (String) uploadResult.get("url");
            UserAppEntity userAppEntity = userAppRepository.getById(StringUtils.convertStringToLongOrNull(userAppId));
            if (userAppEntity == null){
                throw new CustomException("Khong tim thay user", "User not found", HttpStatus.BAD_REQUEST);
            }
            userAppEntity.setAvatar(imageUrl);
            userAppRepository.save(userAppEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(ResponseDataAPI.builder().data(imageUrl).build());
    }

    @RequestMapping(value = "/upload-image" ,method = RequestMethod.POST)
    public ResponseEntity<ResponseDataAPI> uploadImage(@RequestParam("filea") MultipartFile file) throws IOException {
        Path filepath = Path.of("imageupload.jpg");
        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "djifhw3lo",
                "api_key", "992726224781494",
                "api_secret", "Tol4roEhAhgOJ3NaNsnAyWDDrD0",
                "secure", true));
        String imageUrl = "";
        try (OutputStream os = Files.newOutputStream(filepath)) {
            os.write(file.getBytes());
            Map uploadResult = cloudinary.uploader().upload(new File("imageupload.jpg"), ObjectUtils.emptyMap());
            System.out.println("upload moi : " + uploadResult.get("url"));
            imageUrl = (String) uploadResult.get("url");
            ;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(ResponseDataAPI.builder().data(imageUrl).build());
    }
}
