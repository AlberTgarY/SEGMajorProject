package test.java;


import main.java.com.projectBackEnd.Services.Image.DirectoryHolder;
import main.java.com.projectBackEnd.Services.Image.ImageManager;
import main.java.com.projectBackEnd.Services.Image.ImageManagerInterface;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import java.io.File;
import static org.junit.Assert.*;

/**
 * The purpose of this class is to extensively unit test the image manager's methods and validation checks
 */
class ImageManagerTest {

    private static ImageManagerInterface imageManager;

    ImageManagerTest(){ imageManager = ImageManager.getImageManager(); }

    /**
     * Prior to running, set the directory to the test image folder
     */
    @BeforeAll
    static void setUpBefore() {
        DirectoryHolder.getDirectoryHolder().setDir(System.getProperty("user.dir")+"/src/test/resources/TestImages/");
    }

    /**
     * Prior to each test, clean up the test image folder
     */
    @BeforeEach
    void setUp() { imageManager.deleteAll(); }

    /**
     * After all the tests, clean up the folder and set the directory back to default
     */
    @AfterAll
    static void cleanUp() {
        imageManager.deleteAll();
        DirectoryHolder.getDirectoryHolder().setDefaultDir();
    }
//======================================================================================================================
    /**
     * Test saving an image file
     */
    @Test
    void testSaveImage() {
        String imageBytes = readLineByLine("src/test/resources/ImageBytes.txt");
        String randomName = imageManager.saveImage(imageBytes, "png");
        List<String> urls = imageManager.getImageUrls();
        boolean check = urls.contains(imageManager.getDir()+randomName);
        assertTrue(check);
    }

    /**
     * Test Saving an image with a null extension
     */
    @Test
    void testSaveNullExtensionImage() {
        assertNull(imageManager.saveImage("", null));
    }

    /**
     * Test deleting an image file
     */
    @Test
    void testDeleteImage() {
        String imageBytes = readLineByLine("ImageBytes.txt");
        String randomName = imageManager.saveImage(imageBytes, "png");
        List<String> urls = imageManager.getImageUrls();
        boolean checkSave = urls.contains(imageManager.getDir()+randomName);
        imageManager.deleteImage(randomName);
        urls = imageManager.getImageUrls();
        boolean checkDelete = true;
        for(String url : urls)
        {
            if(url.equals(imageManager.getDir()+randomName)){
                checkDelete = false;
            }
        }
        boolean check = (checkDelete)&&checkSave;
        assertTrue(check);
        assertEquals(0, imageManager.getImageUrls().size());
    }

    /**
     * Deleting an image that doesn't exist should return false
     */
    @Test
    void testDeleteImageThatDoesntExist() {
        assertFalse(imageManager.deleteImage("."));
    }

    /**
     * Getting an image that doesn't exist should return null
     */
    @Test
    void testGetImageThatDoesntExist() {
        assertNull(imageManager.getImage("."));
    }

    /**
     * Getting a null image should return a null image
     */
    @Test
    void testGetNullImage() {
        assertNull(imageManager.getImage(null));
    }

    /**
     * Deleting a null image should return false
     */
    @Test
    void testDeleteNullImage() {
        assertFalse(imageManager.deleteImage(null));
    }

    /**
     * Test that no errors are thrown when attempting to delete all from empty file.
     */
    @Test
    void testDeleteAllWithNothing() {
        imageManager.deleteAll();
        assertEquals(0, imageManager.getImageUrls().size());
        imageManager.deleteAll();
        assertEquals(0, imageManager.getImageUrls().size());
    }

    /**
     * Test saving an image file and deleting it
     */
    @Test
    void testSaveAndDeleteAll() {
        String imageBytes = readLineByLine("ImageBytes.txt");
        int counter = 10;
        List<String> generatedUrls = new ArrayList<>();
        while(counter!=0)
        {
            generatedUrls.add(imageManager.getDir()+imageManager.saveImage(imageBytes, "png"));
            counter--;
        }
        List<String> urls = imageManager.getImageUrls();
        assertEquals(10, urls.size());
        Collections.sort(urls);
        Collections.sort(generatedUrls);
        boolean saveCheck = (urls).equals(generatedUrls);
        assertTrue(saveCheck);

        imageManager.deleteAll();
        assertEquals(0, imageManager.getImageUrls().size());
    }

    @Test
    /**
     * Test that a directory can be created if one isn't found
     */
    void testCreateDirectory() {
        String path = System.getProperty("user.dir")+"/toDisappear/";
        DirectoryHolder.getDirectoryHolder().setDir(path);
        File found = new File(path);
        assertTrue(found.exists());
        found.delete();
    }

    /**
     * Read the text in a file
     * @param filePath The location of the file
     * @return String version of the text
     */
    private static String readLineByLine(String filePath)
    {
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines( Paths.get(filePath), StandardCharsets.UTF_8))
        {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }
        catch (IOException e)
        {
            /*ignore*/
        }
        return contentBuilder.toString();
    }
}
