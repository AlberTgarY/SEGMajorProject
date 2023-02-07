package main.java.com.projectBackEnd.Services.Image;

import java.util.List;
import java.io.File;
/**
 *  Methods used by ImageManagers for image uploading and deleting
 */

public interface ImageManagerInterface {

    String saveImage(String imageBytes, String extension);

    File getImage(String imageName);

    String getDir();

    List<String> getImageUrls();

    boolean deleteImage(String imageName);

    void deleteAll();

}
