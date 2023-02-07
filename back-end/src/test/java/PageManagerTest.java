package test.java;

import main.java.com.projectBackEnd.DuplicateKeysException;
import main.java.com.projectBackEnd.InvalidFieldsException;
import main.java.com.projectBackEnd.Services.Page.Hibernate.Page;
import main.java.com.projectBackEnd.Services.Page.Hibernate.PageManager;
import main.java.com.projectBackEnd.Services.Page.Hibernate.PageManagerInterface;
import main.java.com.projectBackEnd.Services.Site.Hibernate.Site;
import main.java.com.projectBackEnd.Services.Site.Hibernate.SiteManager;
import main.java.com.projectBackEnd.Services.Site.Hibernate.SiteManagerInterface;
import main.java.com.projectBackEnd.HibernateUtility;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;

/**
 * Test class to extensively unit test interactions between the page entity manager and the Pages table in the database.
 */
class PageManagerTest {

    private static ConnectionLeakUtil connectionLeakUtil = null;
    private static PageManagerInterface pageManager = null;
    private static SiteManagerInterface siteManager = null;
    private static Site testSiteA = null;
    private static Site testSiteB = null;

    /**
     * Prior to running, database information is set the singleton managers for page and site are created for testing.
     * Also two sites are added to the database which the pages will use during the tests
     */
    @BeforeAll
    static void setUpDatabase() throws DuplicateKeysException, InvalidFieldsException {
        HibernateUtility.setResource("testhibernate.cfg.xml");
        pageManager = PageManager.getPageManager();
        assignSites();
        connectionLeakUtil = new ConnectionLeakUtil();
    }

    /**
     * Pages require an existing site foreign key to be created so we'll create some sites for use.
     */
    private static void assignSites() throws DuplicateKeysException, InvalidFieldsException {
        siteManager = SiteManager.getSiteManager();
        siteManager.addSite(new Site("Disease1", "name"));
        siteManager.addSite(new Site("Disease2", "name2"));
        testSiteA = siteManager.getSiteBySlug("Disease1");
        testSiteB = siteManager.getSiteBySlug("Disease2");
    }

    /**
     * After the test, the factory is shut down, created sites are deleted
     * and the LeakUtil can tell us whether any connections leaked.
     */
    @AfterAll
    static void assertNoLeaks() {
        siteManager.deleteAll();
        HibernateUtility.shutdown();
        connectionLeakUtil.assertNoLeaks();
    }

    /**
     * Prior to each test, we'll delete all the pages in the pages table.
     */
    @BeforeEach
    void setUp() {
        pageManager.deleteAll();
    }

//======================================================================================================================
    //Testing Page Creation constructors

    /* If a method throws these exceptions, it should fail as they should not be thrown.
     * This would be repeated over all the tests and so has not been added.
     * @throws DuplicateKeysException If addition of this object article will cause a duplicate slug present
     * @throws InvalidFieldsException If the object contains fields which cannot be added to the database e.g. nulls
     */
    /**
     * Test pages are created properly with their foreign key assignments
     */
    @Test
    void testCreateValidPage() {
        Page page = new Page(testSiteB.getSlug(), "cool slug", 1, "Interesting", "Content");
        assertEquals("cool slug", page.getSlug());
        assertEquals(1, page.getIndex());
        assertEquals(testSiteB.getSlug(), page.getSite().getSlug());
    }

    /**
     * Test that pages with a bad site name are given a null Site field
     */
    @Test
    void testSavePageWithNullSite() {
        assertNull(siteManager.getSiteBySlug("this is not a slug of a given site"));
        Page page = new Page("this is not a slug of a given site", "cool slug", 1, "Interesting", "Content");
        assertNull(page.getSite());
        try {
            pageManager.addPage(page);
            fail();
        } catch (InvalidFieldsException|DuplicateKeysException e) {
            assertNull(pageManager.getPageBySiteAndSlug("this is not a slug of a given site", "Slug3"));
        }
    }

    /**
     * Test that a page object with null values can still be created
     */
    @Test
    void testCreateWithNullValuesPage() {
        Page page = new Page(null,null,null,null,null);
        assertNotNull(page);
        assertNull(page.getSlug());
        assertNull(page.getSite());
        assertNull(page.getContent());
        assertNull(page.getTitle());
        assertNull(page.getIndex());
    }

    /**
     * Test that a page object with empty values will still be created
     */
    @Test
    void testCreatePageWithEmptyValues() {
        Page page = new Page("","",0,"","");
        assertNotNull(page);
        assertEquals("", page.getSlug());
        assertNull(page.getSite());
    }

    /**
     * Tests that the manager is able to copy the attributes of one page onto another, expects success
     */
    @Test
    void testPageCopy() {
        Page page1 = new Page(testSiteA.getSlug(), "sameSlug", 1, "TitleA", "ContentA");
        Page page2 = new Page(testSiteA.getSlug(), "Slug", 5, "newTitle", "newContent");
        page1.copy(page2);
        assertEquals(page2.getSlug(), page1.getSlug());
        assertEquals(page2.getTitle(), page1.getTitle());
        assertEquals(page2.getIndex(), page1.getIndex());
        assertEquals(page2.getContent(), page1.getContent());
    }

    //Testing Foreign Key constraints

    /**
     * Tests that deleting the site of an existing page will also cause the page to be deleted, expects success
     */
    @Test
    void testEffectOfSiteDelete() throws DuplicateKeysException, InvalidFieldsException{
        siteManager.addSite(new Site("toDeleteSite", "siteName"));
        pageManager.addPage(new Page("toDeleteSite", "Slug", 3, "Title", "content"));
        siteManager.delete(siteManager.getSiteBySlug("toDeleteSite").getPrimaryKey());
        assertEquals(0, pageManager.getAllPages().size());
    }

    /**
     * Tests that updating the site of an existing page will cause the page's site attribute to also change,
     * expects success
     */
    @Test
    void testSiteUpdateEffectOnPage() throws DuplicateKeysException, InvalidFieldsException{
        siteManager.addSite(new Site("toUpdateSite", "siteName"));
        pageManager.addPage(new Page("toUpdateSite", "Slug", 3, "title", "content"));
        Site updatedSite = siteManager.getSiteBySlug("toUpdateSite");
        updatedSite.setName("UpdatedSite");
        siteManager.update(updatedSite);
        assertEquals("UpdatedSite", pageManager.getAllPages().get(0).getSite().getName());
    }

    //Testing PageManagerInterface: getAllPages

    /**
     * Test the fill database method below, and the getAllPages method to show that all are successfully added.
     */
    @Test
    void testFillingAndGetting() throws DuplicateKeysException, InvalidFieldsException {
        fillDatabase(getListOfPages());
        assertEquals(getListOfPages().size(), pageManager.getAllPages().size());
    }

    /**
     * Test the fill database method such that all the pages stored have matching values to the ones added.
     */
    @Test
    void testFillingAndGettingValues() throws DuplicateKeysException, InvalidFieldsException {
        ArrayList<Page> addedPages = getListOfPages();
        fillDatabase(addedPages);
        List<Page> foundPages = pageManager.getAllPages();
        for (int i =0; i < foundPages.size() ; ++i) {
            Page foundPage = foundPages.get(i);
            Page addedPage = addedPages.get(i);
            assertEquals(addedPage.getTitle(), foundPage.getTitle());
            assertEquals(addedPage.getSlug(), foundPage.getSlug());
            assertNotNull(foundPage.getPrimaryKey());
        }
    }

    /**
     * Test that an empty table returns no pages
     */
    @Test
    void testGetAllOnEmptyTable() {
        assertEquals(0, pageManager.getAllPages().size());
    }

    //Test PageManagerInterface: getAllPagesBySite
    /**
     * Tests that the manager is able to retrieve existing sites in the correct (increasing) order, expects success
     */
    @Test
    void testGetAllBySiteOrder() throws DuplicateKeysException, InvalidFieldsException{
        pageManager.addPage(new Page(testSiteB.getSlug(), "I'm from a different site!", 3, "TitleA","ContentA"));
        pageManager.addPage(new Page(testSiteA.getSlug(), "Slug3", 3, "TitleA","ContentA"));
        pageManager.addPage(new Page(testSiteA.getSlug(), "Slug0", 0, "TitleB","ContentB"));
        pageManager.addPage(new Page(testSiteA.getSlug(), "Slug2", 2, "TitleC","ContentC"));
        pageManager.addPage(new Page(testSiteA.getSlug(), "Slug1", 1, "TitleD","ContentD"));
        pageManager.addPage(new Page(testSiteA.getSlug(), "Slug4", 4, "TitleE","ContentE"));
        List<Page> allPagesOfSite = pageManager.getAllPagesOfSite(testSiteA.getSlug());
        assertEquals(5, allPagesOfSite.size());
        for(int i = 0; i < allPagesOfSite.size(); ++i) {
            assertEquals(i, allPagesOfSite.get(i).getIndex());
            assertEquals("Slug"+i, allPagesOfSite.get(i).getSlug());
        }
    }

    /**
     * Test what is returned from a site slug that doesn't exist
     */
    @Test
    void testGetAllWithNonExistentSite() {
        assertEquals(0, pageManager.getAllPagesOfSite("not an existing site slug").size());
    }

    /**
     * Test what is returned from a site that has no pages
     */
    @Test
    void testGetAllBySiteWithNoPages() throws DuplicateKeysException, InvalidFieldsException{
        siteManager.addSite(new Site("this site has no pages", "name"));
        assertEquals(0, pageManager.getAllPagesOfSite("this site has no pages").size());
    }

    /**
     * Test what is returned when a null is given as the site
     */
    @Test
    void testGetAllByNullSite() {
        assertEquals(0, pageManager.getAllPagesOfSite(null).size());
    }

    //Testing PageManagerInterface: deleteAll
    /**
     * Testing a database can have deleteAll run on it, even if it is empty
     * Expected: The number of entries in the database remains zero.
     */
    @Test
    void testDeleteAllEmptyDatabase() {
        pageManager.deleteAll();
        assertEquals(0, pageManager.getAllPages().size());
        pageManager.deleteAll();
        assertEquals(0, pageManager.getAllPages().size());
    }

    /**
     * Testing a database will be flushed by the deleteAll method used between tests
     */
    @Test
    void testDeleteAllFilledDatabase() throws DuplicateKeysException, InvalidFieldsException {
        fillDatabase(getListOfPages());
        assertEquals(getListOfPages().size(), pageManager.getAllPages().size());
        pageManager.deleteAll();
        assertEquals(0, pageManager.getAllPages().size());
    }

    //Testing PageManagerInterface: addPage
    /**
     * Test saving multiple invalid site pages, invalid for different reasons.
     */
    @Test
    void testSaveInvalidPages() {
        ArrayList<Page> invalidPages = new ArrayList<>();
        invalidPages.add(new Page("this is not a slug of a given site", "cool slug", 1, "Interesting", "Content"));
        invalidPages.add(new Page(testSiteA.getSlug(), null, 1, "Interesting", "Content"));
        invalidPages.add(new Page(testSiteA.getSlug(), "valid", null, "Interesting", "content"));
        for (Page p : invalidPages) {
            try {
                pageManager.addPage(p);
                fail();
            } catch (DuplicateKeysException | InvalidFieldsException e) {
                assertEquals(0, pageManager.getAllPages().size());
            }
        }
    }
    /**
     * Tests that the manager is able to add valid pages to the database, expects success
     */
    @Test
    void testAddRegularPagesKey() throws DuplicateKeysException, InvalidFieldsException {
        Page page1 = new Page(testSiteA.getSlug(), "sameSlug", 1, "TitleA", "ContentA");
        Page page2 = new Page(testSiteB.getSlug(), "sameSlug", 1, "TitleB", "ContentB");
        pageManager.addPage(page1);
        pageManager.addPage(page2);

        assertEquals(2, pageManager.getAllPages().size());
    }

    /**
     * Attempts to add two valid pages with the same site and page slug to the database,
     * expects only one page to be added to the database
     */
    @Test
    void testViolateDuplicateCompositeKey() throws DuplicateKeysException, InvalidFieldsException {
        Page page1 = new Page(testSiteA.getSlug(), "sameSlug", 1, "TitleA", "ContentA");
        Page page2 = new Page(testSiteA.getSlug(), "sameSlug", 1, "TitleB", "ContentB");
        pageManager.addPage(page1);
        try {
            pageManager.addPage(page2);
            fail();
        } catch (DuplicateKeysException e) {
            assertEquals(1, pageManager.getAllPages().size());
        }

    }

    /**
     * Test that a page with null values will not be added
     */
    @Test
    void testAddPageWithNullValues() throws DuplicateKeysException, InvalidFieldsException {
        try {
            pageManager.addPage(new Page(testSiteA.getSlug(), null, null, null, null));
            fail();
        } catch (InvalidFieldsException e) {
            assertEquals(0, pageManager.getAllPages().size());
        }
    }

    /**
     * Attempts to add a page with invalid site slug to the database, expects the page to not be added to the database
     */
    @Test
    void testAddPageWithInvalidSite() throws DuplicateKeysException, InvalidFieldsException {
        try {
            pageManager.addPage(new Page("", "",2, "", ""));
            fail();
        } catch (InvalidFieldsException n) {
            assertEquals(0, pageManager.getAllPages().size());
        }

    }

    /**
     * Test that pages with forbidden/strange characters will still be added safely
     */
    @Test
    void testAddPageWithUnsafeValues() throws DuplicateKeysException, InvalidFieldsException {
        pageManager.addPage(new Page(testSiteA.getSlug(),";DROP TABLE Pages", 2, "';'''", "sdafds"));
        assertEquals(pageManager.getAllPages().size(), 1);
    }

    /**
     * Tests that the manager is able to add a page with empty content to the database, expects success
     */
    @Test
    void testEmptyContent() throws DuplicateKeysException, InvalidFieldsException {
        pageManager.addPage(new Page(testSiteB.getSlug(),"", 0, "", ""));
        assertEquals(pageManager.getAllPages().size(), 1);
    }

    /**
     * Test Add pages with null titles or content as this should be allowed
     */
    @Test
    void testNullTitleContent() throws DuplicateKeysException, InvalidFieldsException {
        pageManager.addPage(new Page(testSiteB.getSlug(),"", 0, null, null));
        assertEquals(pageManager.getAllPages().size(), 1);
    }

    //Test PageManagerInterface: getByPrimaryKey
    /**
     * Testing that page objects can be found and made from their primary key.
     */
    @Test
    void testGetByPrimaryKey() throws DuplicateKeysException, InvalidFieldsException {
        fillDatabase(getListOfPages());
        Page foundPage = pageManager.getAllPages().get(0);
        int pagePK = foundPage.getPrimaryKey();
        Page foundPageFromDB = pageManager.getByPrimaryKey(pagePK);
        assertEquals(foundPage.getTitle(), foundPageFromDB.getTitle());
        assertEquals(foundPage.getIndex(), foundPageFromDB.getIndex());
        assertEquals(foundPage.getSlug(), foundPageFromDB.getSlug());
    }

    /**
     * Testing that attempting to obtain a page article with a primary key that doesn't exist returns null
     */
    @Test
    void testGetUnfoundPrimaryKey() {
        assertNull(pageManager.getByPrimaryKey(-1));
    }

    /**
     * Testing an error is thrown if a primary key searched for is null
     */
    @Test
    void testGetNullPrimaryKey() throws DuplicateKeysException, InvalidFieldsException {
        fillDatabase(getListOfPages());
        int previousSize = pageManager.getAllPages().size();
        try {
            pageManager.getByPrimaryKey(null);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(pageManager.getAllPages().size(), previousSize);
        }
    }

    //Testing PageManagerInterface: getBySiteAndSlug

    /**
     * Tests that the manager is able to retrieve a page by it's site and slug value alone, expects success
     */
    @Test
    void testGetPageBySiteAndSlug() throws DuplicateKeysException, InvalidFieldsException {
        Page newPage = new Page(testSiteB.getSlug(),"Slug3", 10, "Title3", "New content!");
        pageManager.addPage(newPage);
        assertNotNull(pageManager.getPageBySiteAndSlug(testSiteB.getSlug(), "Slug3"));
    }

    /**
     * Test how the getBySiteAndSlug reacts to a site that cannot be found
     */
    @Test
    void testGetBySiteSlugUnfoundSite() throws DuplicateKeysException, InvalidFieldsException{
        Page newPage = new Page(testSiteB.getSlug(),"Slug3", 10, "Title3", "New content!");
        pageManager.addPage(newPage);
        assertNull(pageManager.getPageBySiteAndSlug("Not found site sorry", "Slug3"));
    }
    /**
     * Test how the getBySiteAndSlug reacts to a page slug that cannot be found
     */
    @Test
    void testGetBySiteSlugUnfoundPageSlug() {
        assertNull(pageManager.getPageBySiteAndSlug(testSiteB.getSlug(), "not a real page sorry"));
    }
    /**
     * Test how the getBySiteAndSlug reacts to a null site input
     */
    @Test
    void testGetBySiteSlugNullSite() throws DuplicateKeysException, InvalidFieldsException {
        Page newPage = new Page(testSiteB.getSlug(),"Slug3", 10, "Title3", "New content!");
        pageManager.addPage(newPage);
        assertNull(pageManager.getPageBySiteAndSlug(null, "Slug3"));
    }
    /**
     * Test how the getBySiteAndSlug reacts to a null page input
     */
    @Test
    void testGetBySiteSlugNullPageSlug() {
        assertNull(pageManager.getPageBySiteAndSlug(testSiteB.getSlug(), null));
    }

    /**
     * Test how getBySiteAndSlug reacts to double null input
     */
    @Test
    void testGetBySiteSlugNulls() {
        assertNull(pageManager.getPageBySiteAndSlug(null, null));
    }

    //Testing PageManagerInterface: delete

    /**
     * Tests that the manager is able to delete a valid existing page, expects success
     */
    @Test
    void testDelete() throws DuplicateKeysException, InvalidFieldsException {
        pageManager.addPage(new Page(testSiteB.getSlug(),"Slug3", 10, "Title3", "New content!"));
        pageManager.delete(pageManager.getAllPages().get(0).getPrimaryKey());
        assertEquals(pageManager.getAllPages().size(), 0);
    }

    /**
     * Test that an error is thrown if the primary key sent to delete could not be found
     */
    @Test
    void testWithDeleteUnfoundPrimaryKey() {
        int previousSize = pageManager.getAllPages().size();
        try {
            pageManager.delete(-1);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(pageManager.getAllPages().size(), previousSize);
            // Check that nothing has been removed
        }
    }
    /**
     * Test the correct page was infact deleted when using delete
     */
    @Test
    void testCorrectPageDeletedUsingPrimaryKey() throws DuplicateKeysException, InvalidFieldsException {
        Page toBeDeleted = pageManager.addPage(new Page(testSiteB.getSlug(),"Slug3", 10, "Title3", "New content!"));
        Page alsoAdded = pageManager.addPage(new Page(testSiteA.getSlug(),"Slug2", 1, "Title", "New!"));
        assertEquals(2, pageManager.getAllPages().size());
        pageManager.delete(toBeDeleted.getPrimaryKey());
        assertEquals(1, pageManager.getAllPages().size());
        Page leftoverPage = pageManager.getAllPages().get(0);
        assertEquals(alsoAdded.getSlug(), leftoverPage.getSlug());
        assertNull(pageManager.getByPrimaryKey(toBeDeleted.getPrimaryKey()));
    }

    /**
     * Test interaction when a null primary key is sent
     */
    @Test
    void testDeleteWithNullPrimaryKey() {
        int previousSize = pageManager.getAllPages().size();
        try {
            pageManager.delete(null);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(pageManager.getAllPages().size(), previousSize);
            // Check that nothing has been removed
        }
    }

    //Testing PageManagerInterface: update

    /**
     * Tests that the manager is able to update an existing page with valid information, expects success
     */
    @Test
    void testUpdatePage() throws DuplicateKeysException, InvalidFieldsException {
        assertNotNull(siteManager.getSiteBySlug("Disease1"));
        fillDatabase(getListOfPages());
        int assignedID = pageManager.getAllPages().get(0).getPrimaryKey();
        Page updatedPage = new Page(assignedID, testSiteB.getSlug(),"fancy=new=slug", 14, "new Cool title!",
                "New conwishwashchangedtent!");
        pageManager.update(updatedPage);
        Page foundPage = pageManager.getByPrimaryKey(assignedID);

        assertEquals(foundPage.getContent(), updatedPage.getContent());
        assertEquals(foundPage.getSlug(), updatedPage.getSlug());
    }

    /**
     * Test that updating a page works without changing the unique slug
     */
    @Test
    void testUpdatePageNotSlug() throws DuplicateKeysException, InvalidFieldsException {
        assertNotNull(siteManager.getSiteBySlug("Disease1"));
        fillDatabase(getListOfPages());
        Page existingPage = pageManager.getAllPages().get(0);
        Page updatedPage = new Page(existingPage.getPrimaryKey(), existingPage.getSite().getSlug(),existingPage.getSlug(), 14, "new Cool title!",
                "New conwishwashchangedtent!");
        pageManager.update(updatedPage);
        Page foundPage = pageManager.getByPrimaryKey(updatedPage.getPrimaryKey());

        assertEquals(foundPage.getContent(), updatedPage.getContent());
        assertEquals(foundPage.getSlug(), updatedPage.getSlug());
    }

    /**
     * Test what happens if a null page is updated
     */
    @Test
    void testUpdateNullPage() throws DuplicateKeysException, InvalidFieldsException {
        try {
            pageManager.update(new Page());
            fail();
        } catch (InvalidFieldsException e) {
            assertEquals(0, pageManager.getAllPages().size());
        }
    }

    /**
     * Test updating a page that doesn't exist
     */
    @Test
    void testUpdateUnfoundPage() throws DuplicateKeysException, InvalidFieldsException {
        int previousSize = pageManager.getAllPages().size();
        assertNull(pageManager.getByPrimaryKey(-100));
        Page newPage = new Page(-100, "Disease1","Slug3", 10, "Title3", "New content!");
        pageManager.update(newPage);
        assertEquals(pageManager.getAllPages().size(), previousSize);
    }

    /**
     * Attempts to update an existing page with null attribute values (excluding primary key and site slug), expects an
     * exception to be thrown
     */
    @Test
    void testUpdateWithNullData() throws DuplicateKeysException, InvalidFieldsException {
        fillDatabase(getListOfPages());
        int previousSize = pageManager.getAllPages().size();
        int pkOfObjectToUpdate =pageManager.getAllPages().get(0).getPrimaryKey();
        Page badPage = new Page(pkOfObjectToUpdate,"Disease1",null, null, null, null);
        try {
            pageManager.update(badPage);
            fail();
        } catch(InvalidFieldsException e) {
            assertEquals(pageManager.getAllPages().size(), previousSize);
        }

    }

    /**
     * Test updating a page to be the same as another page is not allowed
     */
    @Test
    void testUpdateToViolateDuplicateSlugs() throws DuplicateKeysException, InvalidFieldsException {
        pageManager.addPage(new Page(testSiteA.getSlug(), "sameSlug", 1, "TitleA", "ContentA"));
        Page page2 = pageManager.addPage(new Page(testSiteB.getSlug(), "sameSlug", 1, "TitleB", "ContentB"));
        Page replacement = new Page(page2.getPrimaryKey(), testSiteA.getSlug(), "sameSlug", 1, "TitleB", "ContentB");
        try {
            pageManager.update(replacement);
            fail();
        } catch (DuplicateKeysException e) {
            int count = 0;
            List<Page> allFound = pageManager.getAllPages();
            for (int i =0; i <allFound.size(); ++i) {
                if (allFound.get(i).getSite().getSlug().equals(testSiteA.getSlug()) && allFound.get(i).getSlug().equals("sameSlug")) ++count;
            }
            assertEquals(1, count);
        }

    }

    /**
     * List to fill in database with example Page objects
     * @return example objects
     */
    private static ArrayList<Page> getListOfPages() {
        ArrayList<Page> listOfPages = new ArrayList<>();

        listOfPages.add(new Page(testSiteA.getSlug(),"Slug1", 0, "Title1", "Content1"));
        listOfPages.add(new Page(testSiteA.getSlug(),"Slug2", 8, "Title2", "Content2"));
        listOfPages.add(new Page(testSiteA.getSlug(),"Slug3", 7, "Title3", "Content3"));
        listOfPages.add(new Page(testSiteA.getSlug(),"Slug4", 5, "Title4", "Content4"));
        listOfPages.add(new Page(testSiteA.getSlug(),"Slug5", 4, "Title5", "Content5"));
        listOfPages.add(new Page(testSiteA.getSlug(),"Slug8", 4, "Title5", "Content5"));
        listOfPages.add(new Page(testSiteA.getSlug(),"Slug9", 4, "Title5", "Content5"));
        listOfPages.add(new Page(testSiteA.getSlug(),"Slug12", 4, "Title5", "Content5"));
        listOfPages.add(new Page(testSiteA.getSlug(),"Slug17", 4, "Title5", "Content5"));

        return listOfPages;
    }

    /**
     * Quality of life method to fill database with the pages created from the 'getListOfPages()' method
     */
    private void fillDatabase(ArrayList<Page> pagesToAdd)throws DuplicateKeysException, InvalidFieldsException {
        for (int i = 0; i<pagesToAdd.size(); ++i) pageManager.addPage(pagesToAdd.get(i));
    }

}
