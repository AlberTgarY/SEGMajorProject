package test.java;

import io.micronaut.core.type.Argument;
import io.micronaut.http.*;
import io.micronaut.http.client.multipart.MultipartBody;
import io.micronaut.test.annotation.MicronautTest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;

import main.java.com.projectBackEnd.Services.Image.*;

import javax.inject.Inject;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import static io.micronaut.http.MediaType.MULTIPART_FORM_DATA_TYPE;
import static org.junit.jupiter.api.Assertions.*;


import main.java.com.projectBackEnd.Services.User.Hibernate.UserManager;

import main.java.com.projectBackEnd.HibernateUtility;

import java.io.File;
/**
 * The purpose of this class is to test the REST endpoints associated with the image related executions
 */
@MicronautTest
class ImageControllerTest {
	private static ImageManagerInterface imageManager;
    private static String token;

	private final File file;
	private final File largeFile;

	/**
	*	Constructor gets a new image manager Singleton
	*/
	ImageControllerTest(){
		imageManager = ImageManager.getImageManager();
		file = new File(System.getProperty("user.dir")+"/src/test/resources/TestImages/UploadedImage/"+"testImage.jpg");
		largeFile = new File(System.getProperty("user.dir")+"/src/test/resources/TestImages/UploadedImage/"+"17MB.jpg");
	}
	@Inject
	@Client("/")
	private HttpClient client;
 
	/**
	 * Set up the user table for sessions and set the target directory of generated images to the specified folder
	 */
	@BeforeAll
	static void setUpBefore() {
		DirectoryHolder.getDirectoryHolder().setDir(System.getProperty("user.dir")+"/src/test/resources/TestImages/Generated/");
		HibernateUtility.setResource("testhibernate.cfg.xml");
		try{
			UserManager.getUserManager().addUser("ImageTest@test.com", "123", "name");
			Thread.sleep(100); //A sleep to give the database a chance to update
			token = UserManager.getUserManager().verifyUser("ImageTest@test.com", "123");
		}
		catch(Exception e){
			fail();
		}
	}

	/**
	 * Deletes all images in the database before each test
	 */
	@BeforeEach
	void setUp() {imageManager.deleteAll();}

	/**
	* Delete all images, delete the user, close the factory after.
	*/
	@AfterAll
	static void deleteCreatedImages() {
		imageManager.deleteAll();
		try{
			UserManager.getUserManager().deleteUser("ImageTest@test.com" , "123");
		} catch(Exception e){
			fail();
		}
		HibernateUtility.shutdown();
		DirectoryHolder.getDirectoryHolder().setDefaultDir();
	}


	/**
	 * Tests that legal images can be added to the database via the endpoint, using a POST request
	 */
	@Test
	void testAddLegalImage(){
		HttpResponse response = addImage(file,token);
		assertEquals(HttpStatus.CREATED, response.getStatus());
		String imageName = getEUrl(response);
		assertTrue(imageManager.getImageUrls().contains(imageManager.getDir()+imageName));
	}

	/**
	 * Test if adding a image without a correct session token returns HTTP unauthorized exception
	 */
	@Test
	void testAddUnauthorized(){
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
			addImage(file,"");
        	});
		assertEquals(HttpStatus.UNAUTHORIZED, thrown.getStatus());


		HttpClientResponseException thrown1 = assertThrows(HttpClientResponseException.class, () -> {
			addImage(file,"Ve2R7y5Co3215r8re7CtTok5En13");
        	});
		assertEquals(HttpStatus.UNAUTHORIZED, thrown1.getStatus());
	}

	/**
	 * Testing adding an image with wrong directory / null image
	 */
	@Test
	void testAddIncorrectDirectory(){
		File badFile = new File(System.getProperty("user.dir")+"/src/");
		assertThrows(java.lang.IllegalArgumentException.class, () -> {
			addImage(badFile, token);
		});
	}

	/**
	 * Testing deleting an image
	 */
	@Test
	void testDeleteImage(){
		HttpResponse response = addImage(file,token);
		assertEquals(HttpStatus.CREATED, response.getStatus());
		String imageName = getEUrl(response);
		HttpRequest request = HttpRequest.DELETE("/images/"+imageName).header("X-API-Key",token);
		HttpResponse response2 = client.toBlocking().exchange(request);
		assertEquals(HttpStatus.NO_CONTENT, response2.getStatus());
	}

	/**
	 * Testing deleting an image that doesn't exist
	 */
	@Test
	void testDeleteIncorrect(){
        	HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
			client.toBlocking().exchange(HttpRequest.DELETE("/images/27182").header("X-API-Key",token));
        	});
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR , thrown.getStatus());
		HttpClientResponseException thrown1 = assertThrows(HttpClientResponseException.class, () -> {
			client.toBlocking().exchange(HttpRequest.DELETE("/images/").header("X-API-Key",token));
        	});
		assertEquals(HttpStatus.METHOD_NOT_ALLOWED , thrown1.getStatus());
		HttpClientResponseException thrown2 = assertThrows(HttpClientResponseException.class, () -> {
			client.toBlocking().exchange(HttpRequest.DELETE("/images/name314").header("X-API-Key",token));
        	});
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR , thrown2.getStatus());
	}

	/**
	 * Test that deleting while unauthorized throws an exception
	 */
	@Test
	void testDeleteUnauthorized(){
		HttpResponse response = addImage(file, token);
		assertEquals(HttpStatus.CREATED, response.getStatus());
		String imageName = getEUrl(response);

        	HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
			client.toBlocking().exchange(HttpRequest.DELETE("/images/"+imageName).header("X-API-Key",""));
        	});
		assertEquals(HttpStatus.UNAUTHORIZED, thrown.getStatus());


        	HttpClientResponseException thrown1 = assertThrows(HttpClientResponseException.class, () -> {
			client.toBlocking().exchange(HttpRequest.DELETE("/images/"+imageName).header("X-API-Key","Ve2R7y5Co3215r8re7CtTok5En13"));
        	});
		assertEquals(HttpStatus.UNAUTHORIZED, thrown1.getStatus());
	
	}

	/**
	 * Testing adding and getting the same image
	 */
	@Test
	void testAddAndGetImage(){
		HttpResponse response = addImage(file,token);
		assertEquals(HttpStatus.CREATED, response.getStatus());
		String imageName = getEUrl(response);
		assertTrue(imageManager.getImageUrls().contains(imageManager.getDir()+imageName));
		HttpRequest request = HttpRequest.GET("/images/"+imageName)
				.contentType(MediaType.MULTIPART_FORM_DATA_TYPE);
		File targetImage = client.toBlocking().retrieve(request, Argument.of(File.class));
		assertNotNull(targetImage);
	}

	/**
	 * Test adding a large image
	 */
	@Test
	void testAddLargeImage(){
		HttpResponse response = addImage(largeFile,token);
		assertEquals(HttpStatus.CREATED, response.getStatus());
		String imageName = getEUrl(response);
		assertTrue(imageManager.getImageUrls().contains(imageManager.getDir()+imageName));
	}

	/**
	 * Test adding invalid extension image
	 */
	@Test
	void testAddInvalidExtension() {
		HttpResponse response = addImage(new File(System.getProperty("user.dir")+"/src/test/resources/TestImages/UploadedImage/"+"noextension"
		), token);
		assertEquals(HttpStatus.CREATED, response.getStatus());
	}

	/**
	 * Get the URL location of an image
	 * @param response The HTTP response which contains the image URL
	 * @return The image string URL
	 */
	private String getEUrl(HttpResponse response) {
		String val = response.header(HttpHeaders.LOCATION);
		if (val != null) {
			int index = val.indexOf("/images/");
			if (index != -1) {
				return (val.substring(index + "/images/".length()));
			}
			return null;
		}
		return null;
	}

	/**
	 * Adds a file by POST Request
	 * @param file The file to be added
	 * @param token The User authorisation token
	 * @return The response from adding the image
	 */
	private HttpResponse addImage(File file, String token){
		MultipartBody body = MultipartBody.builder()
				.addPart("file","testImage.jpg",MULTIPART_FORM_DATA_TYPE,file)
				.build();

			HttpRequest request = HttpRequest.POST("/images", body).header("X-API-Key", token)
					.contentType(MediaType.MULTIPART_FORM_DATA_TYPE);
			return client.toBlocking().exchange(request);
	}
}

