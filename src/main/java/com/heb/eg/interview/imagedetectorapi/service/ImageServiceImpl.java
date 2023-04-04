package com.heb.eg.interview.imagedetectorapi.service;

import com.google.gson.*;
import com.heb.eg.interview.imagedetectorapi.dto.CreateImageDto;
import com.heb.eg.interview.imagedetectorapi.entity.Image;
import com.heb.eg.interview.imagedetectorapi.repository.ImageRepository;
import com.heb.eg.interview.imagedetectorapi.service.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class ImageServiceImpl implements  ImageService{

    @Autowired private ImageRepository imageRepository;

    @Override
    public List<Image> getAllImages() throws ServiceException {
        try {
            return imageRepository.findAll();
        } catch (Exception e){
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public Collection getImagesByObjectsDetected(String objects) throws ServiceException{
        try {
            if (objects.contains(",")) {
                HashSet images = new HashSet();
                List<String> strList = new ArrayList<>(Arrays.asList(objects.split(",")));
                for (String object : strList) {
                    images.add(imageRepository.findByObjectsDetectedContains(object));
                }
                return images;
            } else {
                return imageRepository.findByObjectsDetectedContains(objects);
            }
        } catch (Exception e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public Optional<Image> getImageById(UUID imageId) {
        return imageRepository.findById(imageId);
    }

    @Override
    public Image createImage(CreateImageDto createImageDto) {
        StringBuilder detectedObjects = new StringBuilder();
        if(Character.toUpperCase(createImageDto.getEnableDetection()) == 'T'){
            String credentialsToEncode = "acc_94559c77ea9520d" + ":" + "ed7bb369e2cb5b5e91db68716b0c69e0";
            String basicAuth = Base64.getEncoder().encodeToString(credentialsToEncode.getBytes(StandardCharsets.UTF_8));

            String endpoint_url = "https://api.imagga.com/v2/tags";
            String image_url = createImageDto.getLocation();

            String url = endpoint_url + "?image_url=" + image_url + "&threshold=70";
            URL urlObject = null;

            try {
                urlObject = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();

                connection.setRequestProperty("Authorization", "Basic " + basicAuth);

                if(connection.getResponseCode() == 200) {
                    BufferedReader connectionInput = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    String jsonResponse = connectionInput.readLine();

                    Gson gson = new GsonBuilder().create();

                    JsonObject job = gson.fromJson(jsonResponse, JsonObject.class);
                    JsonArray entry = job.getAsJsonObject("result").getAsJsonArray("tags");

                    for (JsonElement e : entry) {
                        JsonElement tag = e.getAsJsonObject().get("tag").getAsJsonObject().get("en");
                        detectedObjects.append(tag.toString().replaceAll("\"", "") +",");
                    }
                    detectedObjects.deleteCharAt(detectedObjects.length()-1);

                    connectionInput.close();

                }else{
                    //throw exception
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        Image image = Image.builder()
                .location(createImageDto.getLocation())
                .enableDetection(Character.toUpperCase(createImageDto.getEnableDetection()))
                .label(createImageDto.getLabel())
                .objectsDetected(String.valueOf(detectedObjects))
                .build();
        return imageRepository.save(image);
    }
}
