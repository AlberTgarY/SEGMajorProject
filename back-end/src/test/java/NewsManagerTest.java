package test.java;

import main.java.com.projectBackEnd.DuplicateKeysException;
import main.java.com.projectBackEnd.InvalidFieldsException;
import main.java.com.projectBackEnd.Services.News.Hibernate.News;
import main.java.com.projectBackEnd.Services.News.Hibernate.NewsManager;
import main.java.com.projectBackEnd.Services.News.Hibernate.NewsManagerInterface;
import main.java.com.projectBackEnd.HibernateUtility;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;

/**
 * Test class to extensively unit test interactions between software and the News table in the database.
 */
class NewsManagerTest {
    
    private static ConnectionLeakUtil connectionLeakUtil = null;
    private static NewsManagerInterface newsManager = null;

    /**
     * Prior to running, database information location is set and a singleton newsManager is acquired for testing on
     */
    @BeforeAll
    static void setUpDatabase() {
        HibernateUtility.setResource("testhibernate.cfg.xml");
        newsManager = NewsManager.getNewsManager();
        connectionLeakUtil = new ConnectionLeakUtil();
    }

    /**
     * After the tests, the factory is shut down and we can see if any connections were leaked with the Database.
     */
    @AfterAll
    static void assertNoLeaks() {
        newsManager.deleteAll();
        HibernateUtility.shutdown();
        connectionLeakUtil.assertNoLeaks();
    }

    /**
     * Prior to each test, we'll delete all the news tuples from the table.
     */
    @BeforeEach
    void setUp() {
        newsManager.deleteAll();
    }

//======================================================================================================================
    //Testing the News Creation Constructors


    /* If a method throws these exceptions, it should fail as they should not be thrown.
     * This would be repeated over all the tests and so has not been added.
     * @throws DuplicateKeysException If addition of this object article will cause a duplicate slug present
     * @throws InvalidFieldsException If the object contains fields which cannot be added to the database e.g. nulls
    */
    /**
     * Testing that creating a news object correctly assigns all the fields with expected values
     */
    @Test
    void testCreateValidNews() {
        News validNews = new News(new Date(12343212L), false,
                "description", "title", false, "content", "slug1");
        assertEquals("description", validNews.getDescription());
        assertEquals("title", validNews.getTitle());
        assertEquals("slug1", validNews.getSlug());
        assertEquals("content", validNews.getContent());
    }

    /**
     * Testing that News object with null values will still be created
     */
    @Test
    void testCreateNullValuesNews() {
        News news1 = new News(null, false,
                null, null, false, null, null);
        assertNotNull(news1);
        assertNull(news1.getDate());
        assertNull(news1.getTitle());
    }

    /**
     * Testing that news objects with empty string values will still be created
     */
    @Test
    void testCreateEmptyValuesNews() {
        News news1 = new News(new Date(), false,
                "", "", false, "", "");
        assertNotNull(news1);
        assertEquals("", news1.getContent());
        assertEquals("", news1.getTitle());
    }

    /**
     * Testing that news objects with empty string values will still be created
     */
    @Test
    void testEqualNews() {
        News news1 = new News(new Date(12343212L), false,
                "desc213ription1", "ti321tle1", false, "con321tent1", "slug1");
        News news2 = new News(new Date(12343212L), false,
                "desc213ription1", "ti321tle1", false, "con321tent1", "slug1");
        assertThat(news1, samePropertyValuesAs(news2));
    }

    /**
     * Testing that once a news object copies another, they both have the same property values
     */
    @Test
    void testNewsCopying() {
        News news1 = new News(new Date(12343212L), false,
                "description", "title", false, "content", "slug1");
        News news2 = new News(null, false,
                "desc213r321321iption1", "ti321tle1", true, "null", "slug4321");
        news1.copy(news2);
        assertThat(news1, samePropertyValuesAs(news2));
    }

    //Testing NewsManagerInterface: getAllNews


    /**
     * Test the fill database method below, and the getAllNews method to show that all are
     * successfully added.
     * Expected: All the medicines from the list are added successfully.
     */
    @Test
    void testFillingAndGetting() throws DuplicateKeysException, InvalidFieldsException {
        fillDatabase(getListOfNews());
        assertEquals(getListOfNews().size(), newsManager.getAllNews().size());
    }

    /**
     * Testing the order returned by the getAllNews
     */
    @Test
    void testOrderOfNews() throws DuplicateKeysException, InvalidFieldsException {
        fillDatabase(getListOfNews());
        List<News> allNews = newsManager.getAllNews();
        assertEquals("title6", allNews.get(0).getTitle());
        assertEquals("title4", allNews.get(1).getTitle());
    }

    /**
     * Testing the order of news with a custom list from getAllNews
     */
    @Test
    void testOrderOfNewsLong() throws DuplicateKeysException, InvalidFieldsException {
        // News(Integer primaryKey, Date date, boolean pinned, String description, String title, boolean urgent, String content, String slug) {
        ArrayList<News> listToBeSorted = new ArrayList<>();
        listToBeSorted.add(new News(new Date(1000000000L), false, "description1", "title1",
                false, "content1", "slug11")); //Oldest
        listToBeSorted.add(new News(new Date(2000000000L), false, "description1", "title1",
                true, "content1", "slug4")); //Urgent
        listToBeSorted.add(new News(new Date(3000000000L), false, "description1", "title1",
                false, "content1", "slug10"));
        listToBeSorted.add(new News(new Date(4000000000L), false, "description1", "title1",
                false, "content1", "slug9"));
        listToBeSorted.add(new News(new Date(5000000000L), false, "description1", "title1",
                false, "content1", "slug8"));
        listToBeSorted.add(new News(new Date(6000000000L), false, "description1", "title1",
                true, "content1", "slug3")); //Youngest urgent
        listToBeSorted.add(new News(new Date(8000000000L), true, "description1", "title1",
                false, "content1", "slug5")); //pinned
        listToBeSorted.add(new News(new Date(9000000000L), false, "description1", "title1",
                false, "content1", "slug7"));
        listToBeSorted.add(new News(new Date(100000000000L), false, "description1", "title1",
                false, "content1", "slug6")); //youngest
        listToBeSorted.add(new News(new Date(98409000000000L), true, "description1", "title1",
                true, "content1", "slug2")); //Urgent pinned
        listToBeSorted.add(new News(new Date(104150000000000L), true, "description1", "title1",
                true, "content1", "slug1")); //Urgent pinned
        fillDatabase(listToBeSorted);
        List<News> sorted = newsManager.getAllNews();
        for (int i = 0; i < sorted.size(); ++i) {
            int j = i+1;
            assertEquals("slug"+j, sorted.get(i).getSlug());
        }
    }

    /**
     * Test that an empty table returns no news
     */
    @Test
    void testGetAllOnEmptyTable() {
        assertEquals(0, newsManager.getAllNews().size());
    }

    //Testing NewsManagerInterface: deleteAll

    /**
     * Testing a database can have deleteAll run on it, even if it is empty
     * Expected: The number of entries in the database remains zero.
     */
    @Test
    void testDeleteAllEmptyDatabase() {
        newsManager.deleteAll();
        assertEquals(0, newsManager.getAllNews().size());
        newsManager.deleteAll();
        assertEquals(0, newsManager.getAllNews().size());
    }

    /**
     * Testing a database will be flushed by the deleteAll method used between tests
     * Expected: The entries will disappear from the database.
     */
    @Test
    void testDeleteAllFilledDatabase() throws DuplicateKeysException, InvalidFieldsException {
        fillDatabase(getListOfNews());
        assertEquals(getListOfNews().size(), newsManager.getAllNews().size());
        newsManager.deleteAll();
        assertEquals(0, newsManager.getAllNews().size());
    }

    //Testing NewsManagerInterface: addNews

    /**
     * Test adding a regular News article to the database.
     * Expected: A new news article is added to the database, regardless of constructor used.
     */
    @Test
    void testAddNews() throws DuplicateKeysException, InvalidFieldsException {
        newsManager.addNews(new News(new Date(12343212L), false,
                "''##DROP TABLE';;'", "sameTitle", false, "con321tent1", "slug1"));
        newsManager.addNews(new News(231, new Date(123432124L), false,
                "desc213ription1", "sameTitle", false, "c2on321tent1", "slug2"));
        assertEquals(2, newsManager.getAllNews().size());
        assertEquals("sameTitle", newsManager.getAllNews().get(0).getTitle());

    }

    /**
     * Adding a news object with null values will not be added to the database.
     * Expected: The size remains unchanged.
     */
    @Test
    void testAddNewsWithNullValues() throws DuplicateKeysException, InvalidFieldsException {
        int sizeBefore = newsManager.getAllNews().size();
        try {
            newsManager.addNews(new News(null, true, null, null, false, null, null));
            fail();
        } catch (InvalidFieldsException e) {
            assertEquals(sizeBefore, newsManager.getAllNews().size());
        }

    }

    /**
     * Testing adding news articles with empty values
     * Expected: The article is added.
     */
    @Test
    void testAddNewsWithEmptyStringValues() throws DuplicateKeysException, InvalidFieldsException {
        int sizeBefore = newsManager.getAllNews().size();
        newsManager.addNews(new News(new Date(),true, "   ", "", false, "", ""));
        assertEquals(sizeBefore+1, newsManager.getAllNews().size());
    }
    /**
     * Testing adding news articles which share the same slug. This should not be possible.
     */
    @Test
    void testDuplicateSlugAddition() throws DuplicateKeysException, InvalidFieldsException {
        int sizeBefore = newsManager.getAllNews().size();
        newsManager.addNews(new News(new Date(12343212L), false,
                "desc213ription1", "ti321t      le1", false, "con321tent1", "slug1"));
        try {
            newsManager.addNews(new News(new Date(12343212L), false,
                    "desc213ription1", "ti321tle1", false, "con321tent1", "slug1"));
            fail();
        } catch (DuplicateKeysException e) {
            assertEquals(sizeBefore+1, newsManager.getAllNews().size());
        }

    }

    //Testing NewsManagerInterface: getByPrimaryKey

    /**
     * Testing that news objects can be found and made from their primary key.
     * Expected: The news found shares the same values as the news in the database.
     */
    @Test
    void testGetByPrimaryKey() throws DuplicateKeysException, InvalidFieldsException {
        fillDatabase(getListOfNews());
        News foundNews = newsManager.getAllNews().get(0);
        int newsPK = foundNews.getPrimaryKey();
        News foundNewsFromDB = newsManager.getByPrimaryKey(newsPK);

        assertThat(foundNews, samePropertyValuesAs(foundNewsFromDB));
    }

    /**
     * Testing that attempting to obtain a news article with a primary key that doesn't exist returns null
     */
    @Test
    void testGetUnfoundPrimaryKey() {
        assertNull(newsManager.getByPrimaryKey(-1));
    }

    /**
     * Testing an error is thrown if a primary key searched for is null
     */
    @Test
    void testGetNullPrimaryKey() throws DuplicateKeysException, InvalidFieldsException {
        fillDatabase(getListOfNews());
        int previousSize = newsManager.getAllNews().size();
        try {
            newsManager.getByPrimaryKey(null);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(newsManager.getAllNews().size(), previousSize);
        }
    }

    //Testing NewsManagerInterface: delete

    /**
     * Test that deleting a news article from the database reduces the number of news articles
     * in the database.
     */
    @Test
    void testDelete() throws DuplicateKeysException, InvalidFieldsException {
        fillDatabase(getListOfNews());
        newsManager.delete(newsManager.getAllNews().get(1).getPrimaryKey());
        assertEquals( getListOfNews().size()-1, newsManager.getAllNews().size());
        newsManager.delete(newsManager.getAllNews().get(1).getPrimaryKey());
        assertEquals(getListOfNews().size()-2, newsManager.getAllNews().size());
    }

    /**
     * Test deleting a primary key which is not in the database.
     * Expected: The database remains unchanged and an error is thrown.
     */
    @Test
    void testWithDeleteUnfoundPrimaryKey() {
        int previousSize = newsManager.getAllNews().size();
        try {
            newsManager.delete(-1);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(newsManager.getAllNews().size(), previousSize);
            // Check that nothing has been removed
        }
    }
    /**
     * Test deleting a primary key which is null.
     */
    @Test
    void testWithDeleteNullPrimaryKey() {
        int previousSize = newsManager.getAllNews().size();
        try {
            newsManager.delete(null);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(newsManager.getAllNews().size(), previousSize);
            // Check that nothing has been removed
        }
    }
    /**
     * Test the correct article was deleted when using delete
     */
    @Test
    void testCorrectNewsDeletedUsingPrimaryKey() throws DuplicateKeysException, InvalidFieldsException  {
        News toBeDeleted = newsManager.addNews(new News(new Date(12343212L), false,
                "getting deleted", "soon won't exist", false, "f", "slug1"));
        News alsoAdded = newsManager.addNews(new News(new Date(12343212L), false,
                "description", "title", false, "content", "slug3"));
        assertEquals(2, newsManager.getAllNews().size());
        newsManager.delete(toBeDeleted.getPrimaryKey());
        assertEquals(1, newsManager.getAllNews().size());
        News leftoverArticle = newsManager.getAllNews().get(0);
        assertEquals(alsoAdded.getSlug(), leftoverArticle.getSlug());
        assertNull(newsManager.getByPrimaryKey(toBeDeleted.getPrimaryKey()));
    }

    //Testing NewsManagerInterface: update

    /**
     * Testing updating one of the existing news articles into another one
     */
    @Test
    void testUpdateNews() throws DuplicateKeysException, InvalidFieldsException {
        fillDatabase(getListOfNews());
        int id = newsManager.getAllNews().get(0).getPrimaryKey();
        News replacementNews = new News(id, new Date(12343212L), true,
                "changedDescription", "newTitle", false, "content1", "slug9");
        newsManager.update(replacementNews);

        News newsInDB = newsManager.getByPrimaryKey(id);
        assertEquals(replacementNews.getDescription(), newsInDB.getDescription());
        assertEquals(replacementNews.getTitle(), newsInDB.getTitle());
    }

    /**
     * Update a news article but not its unique key information
     */
    @Test
    void testUpdateNewsNotSlug() throws DuplicateKeysException, InvalidFieldsException {
        News first = newsManager.addNews(new News(new Date(12343212L), false,
                "changedDescription", "newTitle", false, "content2", "slug9"));
        int id = first.getPrimaryKey();
        News replacementNews = new News(id, new Date(12343212L), true,
                "changedDescription", "newTitle", false, "content1", "slug9");
        newsManager.update(replacementNews);

        News newsInDB = newsManager.getByPrimaryKey(id);
        assertEquals(replacementNews.getDescription(), newsInDB.getDescription());
        assertEquals(replacementNews.getTitle(), newsInDB.getTitle());
    }

    /**
     * Testing updating a news article so it violates the unique - it should throw an error!
     */
    @Test
    void testUpdateNewsWithDupeSlug() throws DuplicateKeysException, InvalidFieldsException {
        fillDatabase(getListOfNews());
        int id = newsManager.getAllNews().get(0).getPrimaryKey();
        News replacementNews = new News(id ,new Date(12343212L), true, "changedDescrption",
                "newTitle", false, "content1", "slug1");
        try {
            newsManager.update(replacementNews);
            fail();
        } catch (DuplicateKeysException e) {
            int count = 0;
            List<News> allFound = newsManager.getAllNews();
            for (int i =0; i <allFound.size(); ++i) {
                if (allFound.get(i).getSlug().equals("slug1")) ++count;
            }
            assertEquals(1, count);
        }

    }

    /**
     * Test update a news article with nulls
     */
    @Test
    void testUpdateNewsWithNullValues() throws DuplicateKeysException, InvalidFieldsException {
        fillDatabase(getListOfNews());
        int previousSize = newsManager.getAllNews().size();
        int id = newsManager.getAllNews().get(0).getPrimaryKey();
        News replacementNews = new News(id ,null, true, null,
                null, false, null, null);
        try {
            newsManager.update(replacementNews);
            fail();
        } catch (InvalidFieldsException e) {
            assertEquals(newsManager.getAllNews().size(), previousSize);
        }
    }

    /**
     * Test update a news article with empty string values
     */
    @Test
    void testUpdateNewsWithEmptyStringValues() throws DuplicateKeysException, InvalidFieldsException {
        fillDatabase(getListOfNews());
        int id = newsManager.getAllNews().get(0).getPrimaryKey();
        News replacementNews = new News(id, new Date(12343212L), true,
                "", "", false, "", "");
        newsManager.update(replacementNews);

        News newsInDB = newsManager.getByPrimaryKey(id);
        assertEquals(replacementNews.getDescription(), newsInDB.getDescription());
        assertEquals(replacementNews.getTitle(), newsInDB.getTitle());
    }

    /**
     * Test what happens if a null article is updated
     */
    @Test
    void testUpdateNullNews() throws DuplicateKeysException, InvalidFieldsException {
        try {
            newsManager.update(new News());
        } catch (InvalidFieldsException e) {
            assertEquals(0, newsManager.getAllNews().size());
        }
    }

    /**
     * Test updating an article that doesn't exist
     */
    @Test
    void testUpdateUnfoundNews() throws DuplicateKeysException, InvalidFieldsException {
        int previousSize = newsManager.getAllNews().size();
        assertNull(newsManager.getByPrimaryKey(-100));
        News fakeNews = new News(-100, new Date(12343212L), true,
                "changedDescription", "newTitle", false, "content1", "slug9");
        newsManager.update(fakeNews);
        assertEquals(previousSize, newsManager.getAllNews().size());
    }

    //Testing NewsManagerInterface: getNewsBySlug

    /**
     * Test that unique slugs can be used to obtain the News article from the database.
     */
    @Test
    void testGetNewsBySlug() throws DuplicateKeysException, InvalidFieldsException {
        fillDatabase(getListOfNews());
        newsManager.addNews(new News(new Date(12343212L), false,
                "getting deleted", "soon won't exist", false, "f", "unique-slug"));
        News found = newsManager.getNewsBySlug("unique-slug");
        assertNotNull(found);
        assertEquals("getting deleted", found.getDescription());
    }

    /**
     * Test searching for a slug that doesn't exist in the table.
     */
    @Test
    void testGetNewsByUnfoundSlug() throws DuplicateKeysException, InvalidFieldsException {
        fillDatabase(getListOfNews());
        News found = newsManager.getNewsBySlug("not a slug in the database sorry");
        assertNull(found);
    }

    /**
     * Testing an error is thrown if a slug searched for is null
     */
    @Test
    void testGetNewsByNullSlug() throws DuplicateKeysException, InvalidFieldsException {
        fillDatabase(getListOfNews());
        assertNull(newsManager.getNewsBySlug(null));
    }

    /**
     * List to fill the database with example News objects
     * @return example objects in a list
     */
    private static ArrayList<News> getListOfNews() {
        ArrayList<News> listOfNews = new ArrayList<>();

        listOfNews.add(new News(new Date(12343212L), false, "description1", "title1",
                false, "content1", "slug1"));
        listOfNews.add(new News(new Date(12343214L), false, "description2", "title2",
                false, "content2", "slug2"));
        listOfNews.add(new News(new Date(12343215L), false, "description3", "title3",
                false, "content3", "slug3"));
        listOfNews.add(new News(new Date(12343218L), true, "description4", "title4",
                false, "content4", "slug4"));
        listOfNews.add(new News(new Date(12343400L), false, "description5", "title5",
                false, "content5", "slug5"));
        listOfNews.add(new News(new Date(12343900L), false, "description6", "title6",
                true, "content6", "slug6"));
        listOfNews.add(new News(new Date(15343212L), false, "description7", "title7",
                false, "content7", "slug7"));

        return listOfNews;
    }

    /**
     * Fill the database with a list of news articles
     * @param listOfNews The list of news articles to fill the database with.
     */
    private void fillDatabase(ArrayList<News> listOfNews) throws DuplicateKeysException, InvalidFieldsException {
        for (int i = 0; i<listOfNews.size(); ++i) newsManager.addNews(listOfNews.get(i));
    }
}
