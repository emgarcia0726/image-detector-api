package com.heb.eg.interview.imagedetectorapi.controller;

import com.heb.eg.interview.imagedetectorapi.dto.CreateImageDto;
import com.heb.eg.interview.imagedetectorapi.entity.Image;
import com.heb.eg.interview.imagedetectorapi.service.ImageService;
import com.heb.eg.interview.imagedetectorapi.service.exception.ServiceDetectorDownException;
import com.heb.eg.interview.imagedetectorapi.service.exception.ServiceException;
import com.heb.eg.interview.imagedetectorapi.service.exception.ServiceMissingImageException;
import com.heb.eg.interview.imagedetectorapi.service.exception.ServiceInvalidImageException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/images")
public class ImageController {

    @Autowired private ImageService imageService;

    /**
     * Returns all images that are in the database if no objects filter is provided. Otherwise, returns all images that
     * contain the objects passed in.
     *
     * @param objects
     * @return iterable of images
     */
    @GetMapping
    public Iterable<Image> getImages(@RequestParam Optional<String> objects) {
        Iterable<Image> images = null;
        try {
            if (!objects.isPresent()) {
                images = imageService.getAllImages();
            } else {
                images = imageService.getImagesByObjectsDetected(objects.get().replaceAll("\"", ""));
            }
            if(StreamSupport.stream(images.spliterator(), false).count() == 0){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No images found with those objects.");
            }
            return images;
        } catch (ServiceException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    /**
     * Returns one image that matched the provided image ID.
     *
     * @param imageId
     * @return Optional of Image
     */
    @GetMapping("/{imageId}")
    public Optional<Image> getImageById(@PathVariable String imageId){
        try{
            return imageService.getImageById(imageId);
        }catch (ServiceMissingImageException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No image found with that ID.");
        }catch (ServiceInvalidImageException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }catch (ServiceException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    /**
     * Creates a new image object in the database.
     *
     * @param createImageDto
     * @return the image that was created
     */
    @PostMapping
    public Image createImage(@RequestBody CreateImageDto createImageDto){
        try{
            return imageService.createImage(createImageDto);
        }catch(ServiceInvalidImageException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }catch(ServiceDetectorDownException e){
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage(), e);
        }catch(ServiceException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }

    }
}
