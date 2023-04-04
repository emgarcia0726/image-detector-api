package com.heb.eg.interview.imagedetectorapi.service;

import com.google.gson.*;
import com.heb.eg.interview.imagedetectorapi.dto.CreateImageDto;
import com.heb.eg.interview.imagedetectorapi.entity.Image;
import com.heb.eg.interview.imagedetectorapi.repository.ImageRepository;
import com.heb.eg.interview.imagedetectorapi.service.exception.ServiceDetectorDownException;
import com.heb.eg.interview.imagedetectorapi.service.exception.ServiceException;
import com.heb.eg.interview.imagedetectorapi.service.exception.ServiceMissingImageException;
import com.heb.eg.interview.imagedetectorapi.service.exception.ServiceInvalidImageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class ImageServiceImpl implements  ImageService{

    @Autowired private ImageRepository imageRepository;

    /**
     * Returns all images from the database.
     *
     * @return all images from the database
     * @throws ServiceException
     */
    @Override
    public List<Image> getAllImages() throws ServiceException {
        try {
            return imageRepository.findAll();
        } catch (Exception e){
            throw new ServiceException(e.getMessage(), e);
        }
    }

    /**
     * Returns all images from the database that have the objects detected.
     *
     * @param objects
     * @return collection of images
     * @throws ServiceException
     */
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

    /**
     * Returns one image based on the passed in unique ID.
     *
     * @param imageId
     * @return optional of an image
     * @throws ServiceException
     */
    @Override
    public Optional<Image> getImageById(String imageId) throws ServiceException{
        if(!validImageId(imageId)){
            throw new ServiceInvalidImageException("Proper UUID is required.");
        }
        try {
            Optional<Image> image = imageRepository.findById(UUID.fromString(imageId));
            if (!image.isEmpty()) {
                return image;
            } else {
                throw new ServiceMissingImageException("No images found with this ID.");
            }
        }catch (Exception e){
            throw new ServiceException(e.getMessage(), e);
        }
    }

    /**
     * Saves an image to the database and if the image's detection is enabled, then will call out to Imagga's image detection
     * service to gather objects in the image. Threshold has been set to 50% confidence.
     *
     * @param createImageDto
     * @return the image that was saved
     * @throws ServiceException
     */
    @Override
    public Image createImage(CreateImageDto createImageDto) throws ServiceException{
        if(!validNewImage(createImageDto)){
            throw new ServiceInvalidImageException("Location is required.");
        }
        StringBuilder detectedObjects = new StringBuilder();
        boolean detectionEnabled = Character.toUpperCase(createImageDto.getEnableDetection()) == 'T';
        if(detectionEnabled){
            String credentialsToEncode = "acc_94559c77ea9520d" + ":" + "ed7bb369e2cb5b5e91db68716b0c69e0";
            String basicAuth = Base64.getEncoder().encodeToString(credentialsToEncode.getBytes(StandardCharsets.UTF_8));

            String endpoint_url = "https://api.imagga.com/v2/tags";
            String image_url = createImageDto.getLocation();

            String url = endpoint_url + "?image_url=" + image_url + "&threshold=50";
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

                    if(!detectedObjects.isEmpty()) {
                        detectedObjects.deleteCharAt(detectedObjects.length() - 1);
                    }

                    connectionInput.close();

                }else{
                    throw new ServiceDetectorDownException("Image detector service is down right now. Status Code: " + connection.getResponseCode());
                }
            } catch (Exception e) {
                throw new ServiceException(e.getMessage(), e);
            }

        }

        Image image = Image.builder()
                .location(createImageDto.getLocation())
                .enableDetection(detectionEnabled ? 'T':'F')
                .label(getLabel(createImageDto.getLabel()))
                .objectsDetected(String.valueOf(detectedObjects))
                .build();

        return imageRepository.save(image);
    }

    //Would replace this with spring validations
    private boolean validNewImage(CreateImageDto createImageDto) {
        if(createImageDto.getLocation() == null || createImageDto.getLocation().isEmpty()) {
            return false;
        }
        return true;
    }

    private boolean validImageId(String imageId) {
        return Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
                .matcher(imageId).matches();
    }

    private String getLabel(String label){
        if(label == null || label.isEmpty()){
            return "Cool picture.";
        }
        return label;
    }
}
