package com.heb.eg.interview.imagedetectorapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heb.eg.interview.imagedetectorapi.dto.CreateImageDto;
import com.heb.eg.interview.imagedetectorapi.entity.Image;
import com.heb.eg.interview.imagedetectorapi.repository.ImageRepository;
import com.heb.eg.interview.imagedetectorapi.service.ImageService;
import com.heb.eg.interview.imagedetectorapi.service.exception.ServiceException;
import com.heb.eg.interview.imagedetectorapi.service.exception.ServiceMissingImageException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import javax.swing.text.html.Option;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class ImageControllerTest {

    public static final String IMAGE_PATH = "/images";
    @Autowired private MockMvc mockmvc;

    @MockBean private ImageService imageService;

    private static final Image TEST_IMAGE_RESPONSE_F = Image.builder()
            .id(UUID.fromString("47b15a9c-7344-428d-ba9b-e63e31dafda"))
            .location("google.com")
            .enableDetection('F')
            .label("random image")
            .objectsDetected("")
            .build();

    private static final CreateImageDto TEST_CREATE_IMAGE_F = CreateImageDto.builder()
            .location("google.com")
            .enableDetection('F')
            .label("random image")
            .build();

    private static final Image TEST_IMAGE_RESPONSE_T = Image.builder()
            .id(UUID.fromString("47b15a9c-7344-428d-ba9b-e63e31dafda"))
            .location("google.com")
            .enableDetection('T')
            .label("random image")
            .objectsDetected("beach,dog")
            .build();

    @Test
    public void testGetAllImages200() throws Exception {
        List<Image> imagesReturned = new ArrayList<>();
        imagesReturned.add(TEST_IMAGE_RESPONSE_F);
        when(imageService.getAllImages()).thenReturn(imagesReturned);

        mockmvc.perform(get(IMAGE_PATH)).andExpect(status().isOk())
                .andExpect(content().string("[" + asJsonString(TEST_IMAGE_RESPONSE_F) + "]"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testGetAllImages500() throws Exception {
        when(imageService.getAllImages()).thenThrow(ServiceException.class);

        mockmvc.perform(get(IMAGE_PATH)).andExpect(status().isInternalServerError())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testGetImagesWithObjects200() throws Exception {
        HashSet imagesReturned = new HashSet<>();
        imagesReturned.add(TEST_IMAGE_RESPONSE_T);
        when(imageService.getImagesByObjectsDetected(anyString())).thenReturn(imagesReturned);

        mockmvc.perform(get(IMAGE_PATH).queryParam("objects","beach,dog"))
                .andExpect(content().string(asJsonString(imagesReturned)))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testGetImagesWithBlankObjects200() throws Exception {
        HashSet imagesReturned = new HashSet<>();
        imagesReturned.add(TEST_IMAGE_RESPONSE_T);
        when(imageService.getImagesByObjectsDetected(anyString())).thenReturn(imagesReturned);

        mockmvc.perform(get(IMAGE_PATH).queryParam("objects",""))
                .andExpect(content().string(asJsonString(imagesReturned)))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testGetImagesWithObjects404() throws Exception {
        when(imageService.getImagesByObjectsDetected(anyString())).thenReturn(new ArrayList<Image>());

        mockmvc.perform(get(IMAGE_PATH).queryParam("objects","abc"))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testGetImagesWithObjects500() throws Exception {
        when(imageService.getImagesByObjectsDetected(anyString())).thenThrow(ServiceException.class);

        mockmvc.perform(get(IMAGE_PATH).queryParam("objects","beach"))
                .andExpect(status().isInternalServerError())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testGetImageById200() throws Exception {
        when(imageService.getImageById(any())).thenReturn(Optional.ofNullable(TEST_IMAGE_RESPONSE_F));

        mockmvc.perform(get(IMAGE_PATH + "/" + TEST_IMAGE_RESPONSE_F.getId()))
                .andExpect(content().string(asJsonString(TEST_IMAGE_RESPONSE_F)))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testGetImageById404() throws Exception {
        when(imageService.getImageById(any())).thenThrow(new ServiceMissingImageException("No images found with this ID."));

        mockmvc.perform(get(IMAGE_PATH + "/" + TEST_IMAGE_RESPONSE_F.getId()))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testGetImageById400() throws Exception {
        when(imageService.getImageById(any())).thenReturn(null);

        mockmvc.perform(get(IMAGE_PATH + "/" + TEST_IMAGE_RESPONSE_F.getId()))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testCreateImage200() throws Exception {
        when(imageService.createImage(any())).thenReturn(TEST_IMAGE_RESPONSE_F);

        mockmvc.perform(post(IMAGE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(asJsonString(TEST_CREATE_IMAGE_F)))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    //..... MORE TESTS WOULD BE HERE ...... //
    private static String asJsonString(final Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }
}