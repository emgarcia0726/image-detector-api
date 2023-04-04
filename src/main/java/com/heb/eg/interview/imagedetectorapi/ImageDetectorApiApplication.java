package com.heb.eg.interview.imagedetectorapi;

import com.heb.eg.interview.imagedetectorapi.entity.Image;
import com.heb.eg.interview.imagedetectorapi.repository.ImageRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ImageDetectorApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImageDetectorApiApplication.class, args);
	}

	/*@Bean
	public ApplicationRunner dataLoader(ImageRepository repo) {
		return args -> {
			repo.save(new Image(null, "C:/Users/emgar/Downloads/single5x7instructionsjpg.jpg", 'F', "Picture for wedding event", ""));
			repo.save(new Image(null, "C:/Users/emgar/OneDrive/Desktop/background.jpg", 'F', "Picture for background", "dog,cat"));
		};
	}*/

}
