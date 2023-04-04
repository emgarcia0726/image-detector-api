package com.heb.eg.interview.imagedetectorapi.repository;

import com.heb.eg.interview.imagedetectorapi.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ImageRepository extends JpaRepository<Image, UUID> {

    List<Image> findByObjectsDetectedContains(String objects);
}
