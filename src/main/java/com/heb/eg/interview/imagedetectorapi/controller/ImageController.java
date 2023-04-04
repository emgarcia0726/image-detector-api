package com.heb.eg.interview.imagedetectorapi.controller;

import com.heb.eg.interview.imagedetectorapi.dto.CreateImageDto;
import com.heb.eg.interview.imagedetectorapi.entity.Image;
import com.heb.eg.interview.imagedetectorapi.service.ImageService;
import com.heb.eg.interview.imagedetectorapi.service.exception.ServiceException;
import com.heb.eg.interview.imagedetectorapi.service.exception.ServiceMissingImageException;
import jdk.jfr.ContentType;
import org.aspectj.weaver.patterns.HasMemberTypePatternForPerThisMatching;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/images")
public class ImageController {

    @Autowired private ImageService imageService;

    @GetMapping
    public ResponseEntity<Object> getImages(@RequestParam Optional<String> objects) {
        Iterable<Image> images = null;
        try {
            if (!objects.isPresent()) {
                images = imageService.getAllImages();
            } else {
                images = imageService.getImagesByObjectsDetected(objects.get().replaceAll("\"", ""));
            }
            if(StreamSupport.stream(images.spliterator(), false).count() == 0){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessage(404, "No images found with those objects."));
            }
            return ResponseEntity.status(HttpStatus.OK).body(images);
        } catch (ServiceException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorMessage(500, e.getMessage()));
        }
    }

    @GetMapping("/{imageId}")
    public ResponseEntity<Object> getImageById(@PathVariable UUID imageId){
        Optional<Image> image = imageService.getImageById(imageId);
        if(image.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(image);
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessage(404, "No images found with this ID."));
        }
    }

    @PostMapping
    public ResponseEntity<Object> createImage(@RequestBody CreateImageDto createImageDto){
        return ResponseEntity.status(HttpStatus.OK).body(imageService.createImage(createImageDto));
    }
}
