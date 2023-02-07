package test.java;


import io.micronaut.test.annotation.MicronautTest;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;

import main.java.com.projectBackEnd.Services.Page.Hibernate.Page;
import main.java.com.projectBackEnd.Services.Page.Hibernate.PageManager;
import main.java.com.projectBackEnd.Services.Page.Hibernate.PageManagerInterface;
import main.java.com.projectBackEnd.Services.Page.Micronaut.PageAddCommand;
import main.java.com.projectBackEnd.Services.Page.Micronaut.PagePatchCommand;
import main.java.com.projectBackEnd.Services.Page.Micronaut.PageUpdateCommand;

import javax.inject.Inject;

import main.java.com.projectBackEnd.Services.Site.Hibernate.SiteManager;
import main.java.com.projectBackEnd.Services.Site.Hibernate.SiteManagerInterface;
import main.java.com.projectBackEnd.Services.Site.Micronaut.SiteAddCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import main.java.com.projectBackEnd.Services.User.Hibernate.UserManager;

import main.java.com.projectBackEnd.HibernateUtility;
/**
 * The purpose of this class is to test the REST endpoints associated with the page entity through the page controller
 */
@MicronautTest
class PageControllerTest {

    @Inject
    @Client("/")
    private HttpClient client;

    private static SiteManagerInterface siteManager;
    private static PageManagerInterface pageManager;
    private static String token;
    /**
     * Sets the config resource location and the page manager. Also generates the token attribute
     */
    @BeforeAll
    static void setUpDatabase() {
        HibernateUtility.setResource("testhibernate.cfg.xml");
        siteManager = SiteManager.getSiteManager();
        pageManager = PageManager.getPageManager();
        try {
            UserManager.getUserManager().addUser("PageTest@test.com", "123", "name");
            Thread.sleep(100); //A sleep to give the database a chance to update
            token = UserManager.getUserManager().verifyUser("PageTest@test.com", "123");
        } catch(Exception e){
        	fail();
        }    
    }
    /**
     * Closes the session factory and deletes the testing user
     */
    @AfterAll
    static void closeDatabase() {

        try {
        	UserManager.getUserManager().deleteUser("PageTest@test.com" , "123");
        } catch(Exception e){
        	fail();
        }   
        HibernateUtility.shutdown(); 
    }

    /**
     * Ensure that there are no pre-existing site or page entities in the database before each test via the 'deleteAll()' method
     */
    @BeforeEach
    void setUp() {
        siteManager.deleteAll();
        //Automatically deletes all pages too due to cascade, but:
        pageManager.deleteAll();
    }

    /**
     * Attempts to retrieve a page that does not exist in the database via the GET request
     */
    @Test
    void testNonExistingPageReturns404() {
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.GET("nothing"));
        });
        assertNotNull(thrown.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }
    /**
     * Tests that the endpoint is able to patch the index of 5 existing site
     */
    @Test
    void testPatchingPageIndex() {
        addSite("testSiteA", "name1",token);
        addPage(new PageAddCommand("testSiteA", "nutrition/slu!#g", 9, "Title", "nutri!tion/information"),token);
        addPage(new PageAddCommand("testSiteA", "anotherPage", 12, "Title", "nutri!tion/information"),token);
        addPage(new PageAddCommand("testSiteA", "coolPage", 20, "Title", "nutri!tion/information"),token);
        addPage(new PageAddCommand("testSiteA", "Paaage", 13, "Title", "nutri!tion/information"),token);

        List<Page> allPagesWithID = pageManager.getAllPages();
        List<PagePatchCommand> input = new ArrayList<>();

        for(int i = 0; i < allPagesWithID.size(); ++i) {
            Page currentPage = allPagesWithID.get(i);
            input.add(new PagePatchCommand(currentPage.getPrimaryKey(), currentPage.getSlug(), i));
        }
        HttpRequest request = HttpRequest.PATCH("/sites/"+ "testSiteA" +"/page-indices", input).header("X-API-Key",token);
        client.toBlocking().exchange(request);
        allPagesWithID = pageManager.getAllPages();
        for(int i = 0; i < allPagesWithID.size(); ++i) assertEquals(i, allPagesWithID.get(i).getIndex());

    }

    /**
     * Test what happens when a page is patched but it doesn't exist
     */
    @Test
    void testPatchingUnfoundPages() {
        List<PagePatchCommand> input = new ArrayList<>();
        input.add(new PagePatchCommand(-1, "unfound_slug", 2));
        HttpRequest request = HttpRequest.PATCH("/sites/"+ "testSiteA" +"/page-indices", input).header("X-API-Key",token);
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(request);
        });
    }

	/**
	* Test if patching with invalid token returns unauthorized HTTP response
	*/
	@Test
	void testPatchingUnauthorized(){
        addSite("testSiteA", "name1",token);
        addPage(new PageAddCommand("testSiteA", "nutrition/slu!#g", 9, "Title", "nutri!tion/information"),token);
        addPage(new PageAddCommand("testSiteA", "anotherPage", 12, "Title", "nutri!tion/information"),token);
        addPage(new PageAddCommand("testSiteA", "coolPage", 20, "Title", "nutri!tion/information"),token);
        addPage(new PageAddCommand("testSiteA", "Paaage", 13, "Title", "nutri!tion/information"),token);

        List<Page> allPagesWithID = pageManager.getAllPages();
        List<PagePatchCommand> input = new ArrayList<>();

        for(int i = 0; i < allPagesWithID.size(); ++i) {
            Page currentPage = allPagesWithID.get(i);
            input.add(new PagePatchCommand(currentPage.getPrimaryKey(), currentPage.getSlug(), i));
        }

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.PATCH("/sites/"+ "testSiteA" +"/page-indices", input).header("X-API-Key","SOmeVeryCo2rr45ECt231TokEN1"));
        });
		assertEquals(HttpStatus.UNAUTHORIZED,thrown.getStatus());
		HttpClientResponseException thrown1 = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.PATCH("/sites/"+ "testSiteA" +"/page-indices", input).header("X-API-Key",""));
        });
		assertEquals(HttpStatus.UNAUTHORIZED,thrown1.getStatus());
	}

    /**
     * Tests that the endpoint is able to add a legal page
     */
    @Test
    void testAddingRegularPage() {
        addSite("testSiteA", "name1",token);
        HttpResponse response = addPage(new PageAddCommand("testSiteA", "nutrition/slu!#g", 1, "Title", "nutri!tion/information"),token);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertNotNull(pageManager.getPageBySiteAndSlug("testSiteA", "nutrition/slu!#g"));

        Page testPage = getPage("testSiteA","nutrition/slu!#g");
        assertEquals("Title", testPage.getTitle());
    }
    /**
     * Attempts to create a page with null information
     */
    @Test
    void testAddingPageWithNulls() {
        assertThrows(NullPointerException.class, () -> {
            addPage(new PageAddCommand(null, null, null, null, null),token);
        });
    }
    /**
     * Attempts to create a page with a null site attribute
     */
    @Test
    void testAddingNullSitePage() {
        addSite("TestSite", "name1",token);
        HttpRequest request = HttpRequest.POST(("/sites/"+ "TestSite" +"/pages"), new PageAddCommand(null, "slug", 3, "", ""));
        assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(request);
        });

    }

    /**
     * Attempts to create a page with a null index attribute
     */
    @Test
    void testAddingNullIndexPage() {
        addSite("testSiteA", "name1",token);
        assertThrows(NullPointerException.class, () -> {
            HttpRequest.POST(("/sites/"+ "testSiteA" +"/pages"), new PageAddCommand("testSiteA", "slug", null, "", ""));
        });
        HttpRequest request = HttpRequest.POST(("/sites/"+ "testSiteA" +"/pages"), new Page("testSiteA", "slug", null, "", ""));
        assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(request);
        });
    }

	/**
	*	Test if adding a page with an invalid session token returns HTTP unauthorized
	*/
	@Test
	void testAddUnauthorized(){
        addSite("testSiteA", "name1",token);
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            addPage(new PageAddCommand( "testSiteA", "nutrition/slu!#g", 1, "newTitle", "nutri!tion/information"),"");
        });
		assertEquals(HttpStatus.UNAUTHORIZED, thrown.getStatus());

 		HttpClientResponseException thrown1 = assertThrows(HttpClientResponseException.class, () -> {
            addPage(new PageAddCommand( "testSiteA", "nutrition/slu!#g", 1, "newTitle", "nutri!tion/information"),"SOmeVeryCo2rr45ECt231TokEN1");
        });
		assertEquals(HttpStatus.UNAUTHORIZED, thrown1.getStatus());

	}

    /**
     * Attempts to create a page with a pre existing page's slug value - shouldn't be allowed
     */
    @Test
    void testCreateSameKeys() {
        addSite("testSiteA", "name1",token);
        addPage(new PageAddCommand("testSiteA", "nutrition/slu!#g", 1, "Title", "nutri!tion/information"),token);
        assertThrows(HttpClientResponseException.class, () -> {
        	addPage(new PageAddCommand("testSiteA", "nutrition/slu!#g", 1, "Title", "nutri!tion/information"),token);
        });
    }

    /**
     * Attempts to delete a page that does not exist in the database
     */
    @Test
    void testDeleteNonExistentPage() {
        assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.DELETE("nothing").header("X-API-Key",token));
        });
    }

    /**
     * Tests that the endpoint is able to delete a pre-existing legal page
     */
    @Test
    void testDeletePage() {
        addSite("testSiteA", "name1",token);
        addPage(new PageAddCommand("testSiteA", "nutrition/slu!#g", 1, "Title", "nutri!tion/information"),token);
        URI pLoc = pageLocation("testSiteA", "nutrition/slu!#g");
        HttpRequest request = HttpRequest.DELETE(pLoc.toString()).header("X-API-Key",token);
        client.toBlocking().exchange(request);
        assertNull(pageManager.getPageBySiteAndSlug("testSiteA", "nutrition/slu!#g"));
    }

	/**
	*	Test if trying to delete a page without a valid session token returns unauthorized
	*/
	@Test
	void testDeleteUnauthorized(){

        addSite("testSiteA", "name1",token);
        addPage(new PageAddCommand("testSiteA", "nutrition/slu!#g", 1, "Title", "nutri!tion/information"),token);
        URI pLoc = pageLocation("testSiteA", "nutrition/slu!#g");

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.DELETE(pLoc.toString()).header("X-API-Key",""));
        });
		assertEquals(HttpStatus.UNAUTHORIZED, thrown.getStatus());

        HttpClientResponseException thrown1 = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.DELETE(pLoc.toString()).header("X-API-Key","SOmeVeryCo2rr45ECt231TokEN1"));
        });

		assertEquals(HttpStatus.UNAUTHORIZED, thrown1.getStatus());

	}

    /**
     * Attempts to update a page's slug value to the slug of a pre existing page, expects a Null pointer exception to be thrown
     */
    @Test
    void updateToDuplicateKeysPage() {
        addSite("testSiteA", "name1",token);
        addPage(new PageAddCommand("testSiteA", "nutrition/slu!#g", 1, "Title", "nutri!tion/information"),token);
        addPage(new PageAddCommand("testSiteA", "sameKey", 1, "Title", "nutri!tion/information"),token);
        int idOfMadePage = pageManager.getPageBySiteAndSlug("testSiteA", "nutrition/slu!#g").getPrimaryKey();
        try {
            putPage(new PageUpdateCommand(idOfMadePage, "notvalid", "sameKey", 1, "newTitle", "nutri!tion/information"),token);
            fail();
        } catch (NullPointerException e) {
            assertNotNull(pageManager.getPageBySiteAndSlug("testSiteA", "nutrition/slu!#g"));
        }
    }

    /**
     * Attemps to update a page to null index value
     */
    @Test
    void updateToNullIndexValues() {
        addSite("testSiteA", "name1",token);
        addPage(new PageAddCommand("testSiteA", "nutrition/slu!#g", 1, "Title", "nutri!tion/information"),token);
        addPage(new PageAddCommand("testSiteA", "sameKey", 1, "Title", "nutri!tion/information"),token);
        int idOfMadePage = pageManager.getPageBySiteAndSlug("testSiteA", "nutrition/slu!#g").getPrimaryKey();
        try {
            putPage(new PageUpdateCommand(idOfMadePage, "notvalid", "sameKey", null, "test222", "nutri!tion/information"),token);
            fail();
        } catch (NullPointerException e) {
            assertNotNull(pageManager.getPageBySiteAndSlug("testSiteA", "nutrition/slu!#g"));
        }
    }
    /**
     * Attemps to update a page to null other non-index values - keeping a valid site and index to
     * not produce null pointer exceptions
     */
    @Test
    void updateToNullValues() {
        addSite("testSiteA", "name1",token);
        addPage(new PageAddCommand("testSiteA", "nutrition/slu!#g", 1, "Title", "nutri!tion/information"),token);
        addPage(new PageAddCommand("testSiteA", "sameKey", 1, "Title", "nutri!tion/information"),token);
        int idOfMadePage = pageManager.getPageBySiteAndSlug("testSiteA", "nutrition/slu!#g").getPrimaryKey();
        HttpClientResponseException thrown1 = assertThrows(HttpClientResponseException.class, () -> {
            putPage(new PageUpdateCommand(idOfMadePage, "testSiteA", null, 1, null, null), token);
        });

        assertNotNull(pageManager.getPageBySiteAndSlug("testSiteA", "nutrition/slu!#g"));
    }

    /**
     * Attemps to update a page with a null primary key
     */
    @Test
    void updateWithNullPrimaryKey() {
        addSite("testSiteA", "name1",token);
        try {
            putPage(new PageUpdateCommand(null, "testSiteA", "set", 1, "set", "notnull"),token);
        } catch (NullPointerException e) {
            assertThrows(HttpClientResponseException.class, () -> {
                getPage("testSiteA", "set");
            });
        }
    }

    /**
     * Tests that the endpoint is able to update an existing legal page's title to a legal value
     */
    @Test
    void testUpdatePageTitle() {
        addSite("testSiteA", "name1",token);
        addPage(new PageAddCommand("testSiteA", "nutrition/slu!#g", 1, "Title", "nutri!tion/information"),token);
        assertNotNull(pageManager.getAllPages().get(0));
        int idOfMadePage = pageManager.getPageBySiteAndSlug("testSiteA", "nutrition/slu!#g").getPrimaryKey();

        putPage(new PageUpdateCommand(idOfMadePage, "testSiteA", "nutrition/slu!#g", 1, "newTitle", "nutri!tion/information"),token);
        assertEquals("newTitle",pageManager.getPageBySiteAndSlug("testSiteA", "nutrition/slu!#g").getTitle());
        Page testPage = getPage("testSiteA", "nutrition/slu!#g");
        assertEquals("newTitle", testPage.getTitle());
    }

    /**
     * Tests that the endpoint is able to update the slug value of an existing page to a different legal slug
     */
    @Test
    void testUpdatePageSiteToValid() {
        addSite("testSiteA", "name!",token);
        addSite("testSiteB", "name!",token);
        addPage(new PageAddCommand("testSiteA", "nutrition/slu!#g", 1, "Title", "nutri!tion/information"),token);

        int idOfMadePage = pageManager.getPageBySiteAndSlug("testSiteA", "nutrition/slu!#g").getPrimaryKey();

        putPage(new PageUpdateCommand(idOfMadePage, "testSiteA", "nutrition/slu!#g", 1, "newTitle", "nutri!tion/information"),token);

        Page testPage = getPage("testSiteA", "nutrition/slu!#g");
        assertEquals("newTitle", testPage.getTitle());
    }

    /**
     * Tests that the endpoint is able to update the slug value of an existing page to a different legal slug
     */
    @Test
    void testUpdatePageSlugToValid() {
        addSite("testSiteA", "name1",token);
        addSite("testSiteB", "name1",token);
        addPage(new PageAddCommand("testSiteA", "nutrition/slu!#g", 1, "Title", "nutri!tion/information"),token);

        int idOfMadePage = pageManager.getPageBySiteAndSlug("testSiteA", "nutrition/slu!#g").getPrimaryKey();

        putPage(new PageUpdateCommand(idOfMadePage, "testSiteA", "nutrition/sl123u!#g", 1, "newTitle", "nutri!tion/information"),token);

        Page testPage = getPage("testSiteA", "nutrition/sl123u!#g");
        assertEquals("newTitle", testPage.getTitle());
        assertThrows(HttpClientResponseException.class, () -> {
            getPage("testSiteA", "nutrition/slu!#g");
        });
        assertNull(pageManager.getPageBySiteAndSlug("testSiteA", "nutrition/slu!#g"));
    }

    /**
     * Attempts to update an existing page's site to an invalid value, expects a Null pointer exception to be thrown
     */
    @Test
    void testUpdatePageToInvalid() {
        addSite("testSiteA", "name1",token);
        addPage(new PageAddCommand("testSiteA", "nutrition/slu!#g", 1, "Title", "nutri!tion/information"),token);
        int idOfMadePage = pageManager.getPageBySiteAndSlug("testSiteA", "nutrition/slu!#g").getPrimaryKey();
        try {
            putPage(new PageUpdateCommand(idOfMadePage, "notvalid", "nutrition/slu!#g", 1, "newTitle", "nutri!tion/information"),token);
            fail();
        } catch (NullPointerException e) {
            assertNotNull(pageManager.getPageBySiteAndSlug("testSiteA", "nutrition/slu!#g"));
        }
    }

	/**
	*	Test if updating page without a correct session token returns HTTP unauthorized
	*/
	@Test
	void testUpdateUnauthorized(){
        addSite("testSiteA", "name1",token);
        addPage(new PageAddCommand("testSiteA", "nutrition/slu!#g", 1, "Title", "nutri!tion/information"),token);
        int idOfMadePage = pageManager.getPageBySiteAndSlug("testSiteA", "nutrition/slu!#g").getPrimaryKey();

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            putPage(new PageUpdateCommand(idOfMadePage, "testSiteA", "nutrition/slu!#g", 1, "newTitle", "nutri!tion/information"),"");
        });
		assertEquals(HttpStatus.UNAUTHORIZED, thrown.getStatus());

 		HttpClientResponseException thrown1 = assertThrows(HttpClientResponseException.class, () -> {
            putPage(new PageUpdateCommand(idOfMadePage, "testSiteA", "nutrition/slu!#g", 1, "newTitle", "nutri!tion/information"),"SOmeVeryCo2rr45ECt231TokEN1");
        });
		assertEquals(HttpStatus.UNAUTHORIZED, thrown1.getStatus());

	}

    /**
     * Tests getting all the pages
     */
    @Test
    void testGetAllPages() {
        addSite("testSiteA", "name1",token);
        HttpRequest request = HttpRequest.GET(location("testSiteA") + "/pages/");
        List<Page> allFound = client.toBlocking().retrieve(request, List.class);
        assertEquals(pageManager.getAllPagesOfSite("testSiteA").size(), allFound.size());
        addPage(new PageAddCommand("testSiteA", "nutr#g", 9, "Title", "nutri!tion/information"),token);

        allFound = client.toBlocking().retrieve(request, List.class);
        assertEquals(pageManager.getAllPagesOfSite("testSiteA").size(), allFound.size());
    }

    /**
     * Quality of life method for updating a page via the REST API
     * @param pageToUpdate The page to be updated
     * @param token The authorization token
     * @return The HTTP response produced by the operation
     */
    private HttpResponse putPage(PageUpdateCommand pageToUpdate,String token) {
        URI pLoc = location(pageToUpdate.getSite());
        HttpRequest request = HttpRequest.PUT(pLoc+"/pages", pageToUpdate).header("X-API-Key",token);
        return client.toBlocking().exchange(request);
    }

    /**
     * Quality of life method for adding a page via the REST API
     * @param pageToAdd The page to be added
     * @param token The authorization token
     * @return The HTTP response produced by the operation
     */
    private HttpResponse addPage(PageAddCommand pageToAdd,String token) {
        URI sLoc = location(pageToAdd.getSite());
        HttpRequest request = HttpRequest.POST((sLoc +"/pages"), pageToAdd).header("X-API-Key",token);
        return client.toBlocking().exchange(request);
    }

    /**
     * Quality of life method for retrieving a page via the REST API
     * @param siteSlug The site the page belongs to
     * @param pageName The page name
     * @return The page item retrieved
     */
    private Page getPage(String siteSlug, String pageName) {
        URI loc = pageLocation(siteSlug, pageName);

        HttpRequest request = HttpRequest.GET(loc);
        return client.toBlocking().retrieve(request, Page.class);
    }

    /**
     * Method for producing a urlencoded url for a page
     * @param siteSlug The site the page belongs to
     * @param pageName The page's slug
     * @return The URI produced
     */
    private URI pageLocation(String siteSlug, String pageName) {
        String encodedSlug;
        String encodedPage;
        try {
            encodedSlug = URLEncoder.encode(siteSlug, java.nio.charset.StandardCharsets.UTF_8.toString());
            encodedPage = URLEncoder.encode(pageName, java.nio.charset.StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            return null;
        }
        return URI.create("/sites/" + encodedSlug + "/pages/" + encodedPage);
    }

    //Adding and locating Site methods
    /**
     * Quality of life method for adding a site via the REST API
     * @param slug The page slug
     * @param name The page name
     * @return The HTTP response produced by the operation
     */
    private HttpResponse addSite(String slug, String name,String token) {
        HttpRequest request = HttpRequest.POST("/sites", new SiteAddCommand(slug,name)).header("X-API-Key",token);
        return client.toBlocking().exchange(request);
    }
    /**
     * Method for producing a URI encoded URI for a site
     * @param siteName the site name to be encoded
     * @return Encoded URI of the site name
     */
    private URI location(String siteName) {
        String encodedSlug;
        try {
            encodedSlug = URLEncoder.encode(siteName, java.nio.charset.StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            return null;
        }
        return URI.create("/sites/" + encodedSlug);
    }


}

