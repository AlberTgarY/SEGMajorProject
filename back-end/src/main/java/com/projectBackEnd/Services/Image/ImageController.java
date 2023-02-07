package main.java.com.projectBackEnd.Services.Image;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Base64;

import io.micronaut.http.MediaType;

import io.micronaut.http.multipart.CompletedFileUpload;
import main.java.com.projectBackEnd.Services.Session.SessionManager;
import main.java.com.projectBackEnd.Services.Session.SessionManagerInterface;

import javax.validation.constraints.Size;

/**
 * Image Controller is a REST API endpoint.
 * It deals with the image related requests users might need : it provides HTTP requests for each of the queries
 * that carry out the adding, deletion and retrieval of different kinds of media from the server-side storage directories.
 */
@Controller("/images")
public class ImageController {

	private final ImageManagerInterface imageManager;
	protected final SessionManagerInterface sessionManager = SessionManager.getSessionManager();

	/** Main constructor */
	public ImageController(){imageManager = ImageManager.getImageManager();}

	/**
	 * Add a new image to the server via an HTTP Post request
	 * @param session	Current session
	 * @param file		File to add to the server
	 * @return HTTP response with relevant information resulting on the insertion of the file
	 */
	@Post(value = "/", consumes = MediaType.MULTIPART_FORM_DATA)
	public HttpResponse<String> add(@Header("X-API-Key") String session, @Body CompletedFileUpload file) {

		if(!sessionManager.verifySession(session)) return HttpResponse.unauthorized();
		try {
			return saveImage(file);
		} catch (IOException e){
			return HttpResponse.noContent();
		}
	}

	/**
	 * Save an image by passing its encodings to the imageManager
	 * @param file	File to be encoded for saving
	 * @return HTTP response based on success of the operation
	 * @throws IOException may throw IOExceptions
	 */
	private HttpResponse saveImage(CompletedFileUpload file) throws IOException {

		String[] strings = file.getFilename().split("\\.");
		String extension = strings[strings.length-1];
		byte[] encoded = Base64.getEncoder().encode(file.getBytes());
		String msg = imageManager.saveImage(new String(encoded), extension);

		if (msg == null) return HttpResponse.serverError();
		else return HttpResponse
					.created(msg)
					.headers(headers -> headers.location(location(msg)));

	}

	/**
	 * Retrieve the file corresponding to the given name via an HTTP Get method
	 * @param imageName	Name of the fie to retrieve
	 * @return The retrieved file
	 */
	@Get(value = "/{imageName}", produces = MediaType.MULTIPART_FORM_DATA)
	@Size
	public File get(String imageName) {
		return imageManager.getImage(imageName);
	}


	/**
	 * Delete the image corresponding to the given name via an HTTP Delete request
	 * @param session	Current session
	 * @param imageName	Name of the file to remove
	 * @return HTTP response with relevant information resulting on the file removal
	 */
	@Delete("/{imageName}")
	public HttpResponse delete(@Header("X-API-Key") String session, String imageName) {
		if(!sessionManager.verifySession(session)) return HttpResponse.unauthorized();
		if(imageManager.deleteImage(imageName)) return HttpResponse.noContent();
		else return HttpResponse.serverError();
	}

	/**
	 * Create URI with existing image name
	 * @param imageName Add the location URI onto this image name
	 * @return created URI
	 */
	private URI location(String imageName) {
		return URI.create("/images/" + imageName);
	}


}
