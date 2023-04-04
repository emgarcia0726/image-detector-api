package com.heb.eg.interview.imagedetectorapi.repository;

import com.heb.eg.interview.imagedetectorapi.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ImageRepository extends JpaRepository<Image, UUID> {

    /**
     * A customized added method to search by the passed in objects to see if the image's objects detected contains
     * any of the passed in objects.
     *
     * @param objects
     * @return list of images that has the objects detected
     */
    List<Image> findByObjectsDetectedContains(String objects);
}
