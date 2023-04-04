package com.heb.eg.interview.imagedetectorapi.service;

import com.heb.eg.interview.imagedetectorapi.dto.CreateImageDto;
import com.heb.eg.interview.imagedetectorapi.entity.Image;
import com.heb.eg.interview.imagedetectorapi.repository.ImageRepository;
import com.heb.eg.interview.imagedetectorapi.service.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

public interface ImageService {

    Iterable<Image> getAllImages() throws ServiceException;

    Iterable<Image> getImagesByObjectsDetected(String objects) throws ServiceException;

    Optional<Image> getImageById(UUID imageId);

    Image createImage(CreateImageDto createImageDto);
}
