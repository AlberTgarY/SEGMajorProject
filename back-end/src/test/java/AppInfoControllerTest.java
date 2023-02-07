package test.java;

import io.micronaut.test.annotation.MicronautTest;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import javax.inject.Inject;
import static org.junit.jupiter.api.Assertions.assertThrows;
import main.java.com.projectBackEnd.Services.AppInfo.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import main.java.com.projectBackEnd.Services.User.Hibernate.UserManager;
import main.java.com.projectBackEnd.HibernateUtility;

/**
 * Testing REST API endpoints for the controller and its interactions with HTTP Requests
 */
@MicronautTest
class AppInfoControllerTest {

    @Inject
    @Client("/")
    private HttpClient client;

    private static String token;

    /**
     * Set up the database to have a test administrator user with a token for restricted requests
     */
    @BeforeAll
    static void setUpDatabase() {
        HibernateUtility.setResource("testhibernate.cfg.xml");
        JSONLocation.setJsonFile("src/test/resources/AppInfoTest.json");

        try {
            UserManager.getUserManager().addUser("appInfoTest@test.com", "123", "name");
            Thread.sleep(100); //A sleep to give the database a chance to update
            token = UserManager.getUserManager().verifyUser("appInfoTest@test.com", "123");
        } catch (Exception e) {
            fail();
        }

    }

    /**
     * Deletes the test user and shuts down the database.
     */
    @AfterAll
    static void closeDatabase() {
        try {
            UserManager.getUserManager().deleteUser("appInfoTest@test.com", "123");
        } catch (Exception e) {
            fail();
        }
        HibernateUtility.shutdown();
    }

    /**
     * Tests updating information and that the getter returns the correct new information
     */
    @Test
    void testUpdatingInfo() {
        updateInformation(new AppInfo("Interesting New Hospital", "Cool Department", "Fancy contact details"));
        assertEquals("Interesting New Hospital", getInfo().getHospitalName());
    }

    /**
     * Test updating information again, to return the new information
     */
    @Test
    void testUpdatingAndGettingInfo() {
        updateInformation(new AppInfo("Cool", "Cool Department", "Cool contact details"));
        assertEquals("Cool", getInfo().getHospitalName());
    }

    /**
     * Test updating information once more, to return new information
     */
    @Test
    void testUpdatingAndGettingInfoAgain() {
        updateInformation(new AppInfo("Fancy update", "Cool Department", "New contact details"));
        assertEquals("Fancy update", getInfo().getHospitalName());
    }

    /**
     * Test unauthorized addition of an image
     */
    @Test
    void testUnauthorizedUpdate() {
        assertThrows(HttpClientResponseException.class, () -> {
            AppInfo updatedInfo = new AppInfo("Wowwee", "Cool", "New info");
            HttpRequest request = HttpRequest.PUT("/appinfo", updatedInfo).header("X-API-Key","lol");
            client.toBlocking().exchange(request);
        });
    }

    /**
     * Creates a PUT request with the updated information supplying API Key and new information
     * @param updatedInfo The new information to be updated
     * @return The HTTP response governing success
     */
    private HttpResponse updateInformation(AppInfo updatedInfo){
        HttpRequest request = HttpRequest.PUT("/appinfo", updatedInfo).header("X-API-Key",token);
        return client.toBlocking().exchange(request);
    }

    /**
     * Runs a GET request to get the information currently stored
     * @return The information AppInfo object stored.
     */
    private AppInfo getInfo(){
        HttpRequest request = HttpRequest.GET("/appinfo/");
        return client.toBlocking().retrieve(request, AppInfo.class);
    }

}