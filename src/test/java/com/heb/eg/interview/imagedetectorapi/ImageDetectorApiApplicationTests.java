package com.heb.eg.interview.imagedetectorapi;

import com.heb.eg.interview.imagedetectorapi.controller.ImageController;
import com.heb.eg.interview.imagedetectorapi.repository.ImageRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase
class ImageDetectorApiApplicationTests {

	@Autowired
	private ImageController imageController;

	@Autowired
	private ImageRepository imageRepository;

	@Test
	void contextLoads() {
		ImageDetectorApiApplication.main(new String[]{});
		assertNotNull(imageController);
	}

}
