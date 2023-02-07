package main.java.com.projectBackEnd.Services.Image;

import main.java.com.projectBackEnd.TokenGenerator;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * The ImageManager class deals with low level business logic transactions regarding media (images, gifs, videos, etc).
 * These are stored on the server so no database interaction is involved.
 */
public class ImageManager implements ImageManagerInterface {

	private static ImageManager imageManager;

	//Random name related variables
	private final static String lexicon = "ABCDEFGHIJKLMNOPQRSTUVWXYZ12345674890";


	//Directory of the folder where the images are saved
	private final String dir;

	/**
	 * Private singleton constructor for ImageManager
	 */
	private ImageManager() {
		imageManager = this;
		dir = DirectoryHolder.getDirectoryHolder().getDir();
	}

	/**
	 * Get the image manager singleton object
	 * @return The image manager
	 */
	public static ImageManager getImageManager() {
		if (imageManager != null) return imageManager;
		else return new ImageManager();
	}

	/**
	 * Generate a random name using the lexicon
	 * @return generated name
	 */
	private String randomIdentifier() {
		return TokenGenerator.generateToken(8, lexicon);
	}

	/**
	 * Save a file with bytes and its extension
	 * @param fileBytes		File as bytes
	 * @param extension		File type of the media
	 * @return generated random name withe file extension
	 */
	public String saveImage(String fileBytes, String extension) {
		if (extension == null) return null;
		byte[] data = Base64.getDecoder().decode(fileBytes.getBytes(StandardCharsets.UTF_8));
		String fullName;
		do fullName = randomIdentifier() + "." + extension.toLowerCase();
		while (getImage(fullName) != null);
		try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(new File(dir + fullName)))) {
			outputStream.write(data);
		} catch (IOException e) {
			return null;
		}
		return fullName;
	}


	/**
	 * Get the file corresponding to the given name
	 * @param fileName	Name of the file to be searched for
	 * @return The matching file
	 */
	public File getImage(String fileName) {
		File[] fileArray = getFileArray(dir);
		for (File file : fileArray) {
			if (file.isFile() && file.getName().equals(fileName)) return file;
		}
		return null;
	}

	/**
	 * Get a list of all the media URLs in the directory
	 * @return String list of all the media URLs in dir.
	 */
	public List<String> getImageUrls()	{
		File[] fileArray = getFileArray(dir);
		List<String> urls = new ArrayList<>();
		for (int i = 0; i < fileArray.length; i++) {
			if (fileArray[i].isFile()) urls.add(dir+fileArray[i].getName());
		}
		return urls;
	}

	/**
	 * Using a string directory, return an array of the files in the directory.
	 * @param directoryPath The string location of the directory to be searched
	 * @return All the files in the directory
	 */
	private static File[] getFileArray(String directoryPath) {
		File directory = new File(directoryPath);
		try {
			return directory.listFiles();
		} catch (Exception e){
			return null;
		}
	}

	/**
	 * Get the output directory
	 * @return directory
	 */
	public String getDir() {
		return dir;
	}

	/**
	 * Delete an file based on its name
	 * @param fileName The name of the file to be deleted
	 * @return Whether delete was successful
	 */
	public boolean deleteImage(String fileName)
	{
		File foundImage = getImage(fileName);
		return (foundImage == null) ? false : foundImage.delete();
	}

	/**
	 * Delete all the media from the server
	 */
	public void deleteAll() {
		List<String> listOfImageUrls = getImageUrls();
		for (String imageUrl : listOfImageUrls) {
			deleteImage(imageUrl.substring(imageUrl.lastIndexOf("/") + 1));
		}
	}

}