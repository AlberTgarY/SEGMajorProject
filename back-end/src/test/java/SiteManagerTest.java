
package test.java;

import main.java.com.projectBackEnd.DuplicateKeysException;
import main.java.com.projectBackEnd.InvalidFieldsException;
import main.java.com.projectBackEnd.Services.Site.Hibernate.Site;
import main.java.com.projectBackEnd.Services.Site.Hibernate.SiteManager;
import main.java.com.projectBackEnd.Services.Site.Hibernate.SiteManagerInterface;
import main.java.com.projectBackEnd.HibernateUtility;
import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;

/**
 * Test class to extensively unit test interactions between software and the Sites table in the database.
 */
class SiteManagerTest {
    private static ConnectionLeakUtil connectionLeakUtil = null;
    private static SiteManagerInterface siteManager = null;
    /**
     * Prior to running, database information location is set and a singleton siteManager is acquired for testing on
     */
    @BeforeAll
    static void setUpDatabase() {
        HibernateUtility.setResource("testhibernate.cfg.xml");
        siteManager = SiteManager.getSiteManager();
        connectionLeakUtil = new ConnectionLeakUtil();
    }

    /**
     * After the test, the factory is shut down and we can see if any connections were leaked with the Database.
     */
    @AfterAll
    static void assertNoLeaks() {
        siteManager.deleteAll();
        HibernateUtility.shutdown();
        connectionLeakUtil.assertNoLeaks();
    }
    /**
     * Prior to each test, we'll delete all the site tuples from the table.
     */
    @BeforeEach
     void setUp() {
        siteManager.deleteAll();
    }
//======================================================================================================================
    //Testing the Site Creation Constructors
    /* If a method throws these exceptions, it should fail as they should not be thrown.
     * This would be repeated over all the tests and so has not been added.
     * @throws DuplicateKeysException If addition of this object article will cause a duplicate slug present
     * @throws InvalidFieldsException If the object contains fields which cannot be added to the database e.g. nulls
     */
    /**
     * Testing that creating a site object correctly assigns all the fields with expected values
     */
    @Test
     void testCreateValidSite() {
        Site site = new Site("slug1", "Biliary Atresia");
        assertEquals("Biliary Atresia", site.getName());
        assertEquals("slug1", site.getSlug());
    }

    /**
     * Testing that Site object with null values will still be created
     */
    @Test
     void testCreateNullValuesSite() {
        Site site1 = new Site(null, null);
        assertNotNull(site1);
        assertNull(site1.getSlug());
        assertNull(site1.getName());
    }

    /**
     * Testing that site objects with empty string values will still be created
     */
    @Test
     void testCreateEmptyValuesSite() {
        Site site1 = new Site("", "");
        assertNotNull(site1);
        assertEquals("", site1.getSlug());
        assertEquals("", site1.getName());
    }

    /**
     * Testing that two site objects created in the same way have the same property values
     */
    @Test
     void testEqualSite() {
        Site site1 = new Site("Random", "Assertion");
        Site site2 = new Site("Random", "Assertion");
        assertThat(site1, samePropertyValuesAs(site2));
    }

    /**
     * Testing that once a site object copies another, they both have the same property values
     */
    @Test
     void testSiteCopying() {
        Site site1 = new Site("Random", "Assertion");
        Site site2 = new Site("I'll change slug", "Fancy name!");
        site1.copy(site2);
        assertThat(site1, samePropertyValuesAs(site2));
    }

    //Testing SiteManagerInterface: getAllSites

    /**
     * Test the fill database method below, and the getAllSites method to show that all are successfully added.
     */
    @Test
     void testFillingAndGetting() throws DuplicateKeysException, InvalidFieldsException {
        fillDatabase(getListOfSites());
        assertEquals(getListOfSites().size(), siteManager.getAllSites().size());
    }

    /**
     * Test the fill database method such that all the sites stored have matching names and types to the ones added.
     */
    @Test
     void testFillingAndGettingValues() throws DuplicateKeysException, InvalidFieldsException {
        ArrayList<Site> addedSites = getListOfSites();
        fillDatabase(addedSites);
        List<Site> foundSites = siteManager.getAllSites();
        for (int i =0; i < foundSites.size() ; ++i) {
            Site foundSite = foundSites.get(i);
            Site addedSite = addedSites.get(i);
            assertEquals(addedSite.getName(), foundSite.getName());
            assertEquals(addedSite.getSlug(), foundSite.getSlug());
            assertNotNull(foundSite.getPrimaryKey());
        }
    }

    /**
     * Test that an empty table returns no sites
     */
    @Test
     void testGetAllOnEmptyTable() {
        assertEquals(0, siteManager.getAllSites().size());
    }

    //Testing SiteManagerInterface: deleteAll
    /**
     * Testing a database can have deleteAll run on it, even if it is empty
     */
    @Test
     void testDeleteAllEmptyDatabase() {
        siteManager.deleteAll();
        assertEquals(0, siteManager.getAllSites().size());
        siteManager.deleteAll();
        assertEquals(0, siteManager.getAllSites().size());
    }

    /**
     * Testing a database will be flushed by the deleteAll method used between tests
     */
    @Test
     void testDeleteAllFilledDatabase() throws DuplicateKeysException, InvalidFieldsException {
        fillDatabase(getListOfSites());
        assertEquals(getListOfSites().size(), siteManager.getAllSites().size());
        siteManager.deleteAll();
        assertEquals(0, siteManager.getAllSites().size());
    }

    //Testing SiteManagerInterface: addSite

    /**
     * Test adding a regular Site article to the database.
     */
    @Test
     void testAddSite() throws DuplicateKeysException, InvalidFieldsException {
        siteManager.addSite(new Site("''DROP TABLE';;';;//#slug", "same"));
        siteManager.addSite(new Site(231, "popslug", "name"));
        assertEquals(2, siteManager.getAllSites().size());
        assertEquals("popslug", siteManager.getAllSites().get(1).getSlug());
        assertEquals("name", siteManager.getAllSites().get(1).getName());

    }

    /**
     * Adding a site object with null values will not be added to the database.
     */
    @Test
     void testAddSiteWithNullValues() throws DuplicateKeysException, InvalidFieldsException {
        int sizeBefore = siteManager.getAllSites().size();
        try {
            siteManager.addSite(new Site(null, null));
        } catch (InvalidFieldsException e) {
            assertEquals(sizeBefore, siteManager.getAllSites().size());
        }
    }

    /**
     * Testing adding sites with empty values
     */
    @Test
     void testAddSiteWithEmptyStringValues() throws DuplicateKeysException, InvalidFieldsException {
        int sizeBefore = siteManager.getAllSites().size();
        siteManager.addSite(new Site("   ", ""));
        assertEquals(sizeBefore+1, siteManager.getAllSites().size());
    }
    /**
     * Testing adding sites which share the same slug. This should not be possible.
     */
    @Test
     void testDuplicateSlugAddition() throws DuplicateKeysException, InvalidFieldsException {
        int sizeBefore = siteManager.getAllSites().size();
        String slug = "identicalSlug!";
        siteManager.addSite(new Site(slug, "differentName"));
        assertNotNull(siteManager.getSiteBySlug(slug));
        try {
            siteManager.addSite(new Site(slug, "sameName"));
            fail();
        } catch (DuplicateKeysException e) {
            assertEquals(sizeBefore+1, siteManager.getAllSites().size());
        }

    }

    //Testing SiteManagerInterface: getByPrimaryKey

    /**
     * Testing that site objects can be found and made from their primary key.
     */
    @Test
     void testGetByPrimaryKey() throws DuplicateKeysException, InvalidFieldsException {
        fillDatabase(getListOfSites());
        Site foundSite = siteManager.getAllSites().get(0);
        int sitePK = foundSite.getPrimaryKey();
        Site foundSiteFromDB = siteManager.getByPrimaryKey(sitePK);

        assertThat(foundSite, samePropertyValuesAs(foundSiteFromDB));
    }

    /**
     * Testing that attempting to obtain a site article with a primary key that doesn't exist returns null
     */
    @Test
     void testGetIllegalPrimaryKey() {
        assertNull(siteManager.getByPrimaryKey(-1));
    }

    /**
     * Testing an error is thrown if a primary key searched for is null
     */
    @Test
     void testGetNullPrimaryKey() throws DuplicateKeysException, InvalidFieldsException {
        fillDatabase(getListOfSites());
        int previousSize = siteManager.getAllSites().size();
        try {
            siteManager.getByPrimaryKey(null);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(siteManager.getAllSites().size(), previousSize);
        }
    }

    //Testing SiteManagerInterface: delete

    /**
     * Test that deleting a site article from the database reduces the number of site articles the database.
     */
    @Test
     void testDelete() throws DuplicateKeysException, InvalidFieldsException {
        fillDatabase(getListOfSites());
        siteManager.delete(siteManager.getAllSites().get(1).getPrimaryKey());
        assertEquals( getListOfSites().size()-1, siteManager.getAllSites().size());
        siteManager.delete(siteManager.getAllSites().get(1).getPrimaryKey());
        assertEquals(getListOfSites().size()-2, siteManager.getAllSites().size());
    }

    /**
     * Test deleting a primary key which is not in the database.
     */
    @Test
     void testWithDeleteUnfoundPrimaryKey() {
        int previousSize = siteManager.getAllSites().size();
        try {
            siteManager.delete(-1);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(siteManager.getAllSites().size(), previousSize);
            // Check that nothing has been removed
        }
    }
    /**
     * Test deleting a primary key which is null.
     */
    @Test
     void testWithDeleteNullPrimaryKey() {
        int previousSize = siteManager.getAllSites().size();
        try {
            siteManager.delete(null);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(siteManager.getAllSites().size(), previousSize);
            // Check that nothing has been removed
        }
    }

    /**
     * Test the correct article was infact deleted when using delete
     */
    @Test
     void testCorrectSiteDeletedUsingPrimaryKey() throws DuplicateKeysException, InvalidFieldsException {
        Site toBeDeleted = siteManager.addSite(new Site("getting deleted", "soon won't exist"));
        Site alsoAdded = siteManager.addSite(new Site("content", "slug3"));
        assertEquals(2, siteManager.getAllSites().size());
        siteManager.delete(toBeDeleted.getPrimaryKey());
        assertEquals(1, siteManager.getAllSites().size());
        Site leftoverArticle = siteManager.getAllSites().get(0);
        assertEquals(alsoAdded.getSlug(), leftoverArticle.getSlug());
        assertNull(siteManager.getByPrimaryKey(toBeDeleted.getPrimaryKey()));
    }

    //Testing SiteManagerInterface: update

    /**
     * Testing updating one of the existing site articles into another one
     */
    @Test
     void testUpdateSite() throws DuplicateKeysException, InvalidFieldsException {
        fillDatabase(getListOfSites());
        int id = siteManager.getAllSites().get(0).getPrimaryKey();
        Site replacementSite = new Site(id, "another unique slug", "name");
        siteManager.update(replacementSite);

        Site siteInDB = siteManager.getByPrimaryKey(id);
        assertEquals(replacementSite.getName(), siteInDB.getName());
        assertEquals(replacementSite.getSlug(), siteInDB.getSlug());
    }

    /**
     * Testing updating a site article so it violates the unique - it should throw an error
     */
    @Test
     void testUpdateSiteWithDupeSlug()throws DuplicateKeysException, InvalidFieldsException {
        Site toBeUpdated = siteManager.addSite(new Site("slug", "Spicy name!"));
        siteManager.addSite(new Site("I should be unique slug", "Spicy unique name!"));
        int previousSize = siteManager.getAllSites().size();

        Site replacementSite = new Site(toBeUpdated.getPrimaryKey() , "I should be unique slug", "Another coooool update!");
        try {
            siteManager.update(replacementSite);
            fail();
        } catch (DuplicateKeysException e) {
            assertEquals(siteManager.getAllSites().size(), previousSize);
        }
    }

    /**
     * Test update a site article with nulls - should throw an error
     */
    @Test
     void testUpdateSiteWithNullValues() throws DuplicateKeysException, InvalidFieldsException {
        fillDatabase(getListOfSites());
        int previousSize = siteManager.getAllSites().size();
        int id = siteManager.getAllSites().get(0).getPrimaryKey();
        Site replacementSite = new Site(id, null, null);
        try {
            siteManager.update(replacementSite);
            fail();
        } catch (InvalidFieldsException e) {
            assertEquals(siteManager.getAllSites().size(), previousSize);
        }
    }

    /**
     * Test update a site article with empty string values
     */
    @Test
     void testUpdateSiteWithEmptyStringValues() throws DuplicateKeysException, InvalidFieldsException {
        fillDatabase(getListOfSites());
        int id = siteManager.getAllSites().get(0).getPrimaryKey();
        Site replacementSite = new Site(id, "", "");
        siteManager.update(replacementSite);

        Site siteInDB = siteManager.getByPrimaryKey(id);
        assertEquals(replacementSite.getName(), siteInDB.getName());
        assertEquals(replacementSite.getSlug(), siteInDB.getSlug());
    }
    /**
     * Test what happens if a null site is updated
     */
    @Test
     void testUpdateNullSite() throws DuplicateKeysException, InvalidFieldsException {
        try {
            siteManager.update(new Site());
        } catch (InvalidFieldsException e) {
            assertEquals(0, siteManager.getAllSites().size());
        }
    }

    /**
     * Test updating a site that doesn't exist
     */
    @Test
     void testUpdateUnfoundSite() throws DuplicateKeysException, InvalidFieldsException {
        int previousSize = siteManager.getAllSites().size();
        assertNull(siteManager.getByPrimaryKey(-100));
        Site newSite = new Site("slug", "Spicy name!");
        siteManager.update(newSite);
        assertEquals(previousSize, siteManager.getAllSites().size());
    }
    //Testing SiteManagerInterface: getSiteBySlug

    /**
     * Test that unique slugs can be used to obtain the Site from the database.
     */
    @Test
     void testGetSiteBySlug()throws DuplicateKeysException, InvalidFieldsException  {
        fillDatabase(getListOfSites());
        siteManager.addSite(new Site("unique-slug", "f"));
        Site found = siteManager.getSiteBySlug("unique-slug");
        assertNotNull(found);
        assertEquals("f", found.getName());
    }

    /**
     * Test searching for a slug that doesn't exist in the table.
     */
    @Test
     void testGetNewsByUnfoundSlug() throws DuplicateKeysException, InvalidFieldsException {
        fillDatabase(getListOfSites());
        Site found = siteManager.getSiteBySlug("not a slug in the database sorry");
        assertNull(found);
    }

    /**
     * Testing an error is thrown if a slug searched for is null
     */
    @Test
     void testGetNewsByNullSlug() throws DuplicateKeysException, InvalidFieldsException {
        fillDatabase(getListOfSites());
        Site found = siteManager.getSiteBySlug(null);
        assertNull(found);

    }
    /**
     * Get a prewritten list of sites.
     * @return array list of example site objects for database filling
     */
    private static ArrayList<Site> getListOfSites() {

        ArrayList<Site> listOfSites = new ArrayList<>();

        listOfSites.add(new Site("Slug1", "Disease1"));
        listOfSites.add(new Site("Slug2", "Disease2"));
        listOfSites.add(new Site("Slug3", "Disease3"));
        listOfSites.add(new Site("Slug4", "Disease4"));
        listOfSites.add(new Site("Slug5", "Disease5"));
        listOfSites.add(new Site("Slug6", "Disease6"));
        listOfSites.add(new Site("Slug7", "Disease7"));
        listOfSites.add(new Site("Slug8", "Disease8"));

        return listOfSites;
    }

    /**
     * Fill the database with a given list of sites
     * @param listOfSites The list of sites to go into the database
     */
    private void fillDatabase(ArrayList<Site> listOfSites) throws DuplicateKeysException, InvalidFieldsException {
        for (int i = 0; i<listOfSites.size(); ++i) siteManager.addSite(listOfSites.get(i));
    }

}
