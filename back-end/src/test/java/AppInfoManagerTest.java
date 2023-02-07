package test.java;

import main.java.com.projectBackEnd.Services.AppInfo.AppInfo;
import main.java.com.projectBackEnd.Services.AppInfo.AppInfoManager;
import main.java.com.projectBackEnd.Services.AppInfo.AppInfoManagerInterface;
import main.java.com.projectBackEnd.Services.AppInfo.JSONLocation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterEach;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Testing the two methods in the AppInfoManagerInterface
 */
class AppInfoManagerTest {

    private static AppInfoManagerInterface infoManager;
    private static String mainTestPath = "src/test/resources/AppInfoTest.json";
    /**
     * Set the JSON's location and initialise a manager for testing
     */
    @BeforeAll
    static void setUp() {
        JSONLocation.setJsonFile(mainTestPath);
        //Setting this will create the file itself before the AppInfoManager is initialised
        infoManager = AppInfoManager.getInfoManager();
    }

    /**
     * Reset the test path
     */
    @AfterEach
    void reset() {
        JSONLocation.setJsonFile(mainTestPath);
    }
//======================================================================================================================

    /**
     * Test setting hospital's information, multiple times to show rewrites
     */
    @Test
    void testUpdateAndGetInformation() {
        infoManager.updateInfo(new AppInfo("Interesting Hospital", "Cool Department", "contact details"));
        assertEquals("Interesting Hospital", infoManager.getInfo().getHospitalName());
    }


    /**
     * Test setting hospital's information, multiple times to show rewrites
     */
    @Test
    void testUpdateAndGetInformationAgain() {
        infoManager.updateInfo(new AppInfo("Interesting New Hospital", "Cool Department", "Contact details new"));
        assertEquals("Interesting New Hospital", infoManager.getInfo().getHospitalName());
    }

    /**
     * Test setting hospital's information, multiple times to show rewrites
     */
    @Test
    void testUpdateAndGetInformationOnceMore() {
        infoManager.updateInfo(new AppInfo("Interesting New Hospital", "Cool Department in a different dep", "Up1"));
        assertEquals("Cool Department in a different dep", infoManager.getInfo().getDepartmentName());
    }

    /**
     * Test that changing the json file once the AppInfoManager has been initialised has no effect.
     * The JSON Location is invalidated.
     */
    @Test
    void testEmptyUnfoundJSONFile() {
        JSONLocation.setJsonFile("");
        infoManager.updateInfo(new AppInfo("Interesting New Hospital", "Unique Update Message ", "Details"));
        assertEquals("Unique Update Message ", infoManager.getInfo().getDepartmentName());
    }


}
