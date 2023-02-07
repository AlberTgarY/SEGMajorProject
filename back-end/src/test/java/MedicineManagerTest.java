package test.java;

import main.java.com.projectBackEnd.*;
import main.java.com.projectBackEnd.Services.Medicine.Hibernate.Medicine;
import main.java.com.projectBackEnd.Services.Medicine.Hibernate.MedicineManager;

import main.java.com.projectBackEnd.Services.Medicine.Hibernate.MedicineManagerInterface;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;

/**
 * Test class to extensively unit test interactions between software and the Medicines table in the database.
 */
class MedicineManagerTest {

    private static ConnectionLeakUtil connectionLeakUtil = null;
    private static MedicineManagerInterface medicineManager = null;

    /**
     * Prior to running, database information is set and a singleton manager is created for testing.
     */
    @BeforeAll
    static void setUpDatabase() {
        HibernateUtility.setResource("testhibernate.cfg.xml");
        medicineManager = MedicineManager.getMedicineManager();
        connectionLeakUtil = new ConnectionLeakUtil();
    }

    /**
     * After the test, the factory is shut down and the LeakUtil can tell us whether any connections leaked.
     */
    @AfterAll
    static void assertNoLeaks() {
        medicineManager.deleteAll();
        HibernateUtility.shutdown();
        connectionLeakUtil.assertNoLeaks();
    }

    /**
     * Prior to each test, we'll delete all the medicines in the table.
     */
    @BeforeEach
    void setUp() {
        medicineManager.deleteAll();
    }

//======================================================================================================================
    //Testing Medicine Creation Constructors

    /**
     * Test the constructor constraints for naming with valid names
     * Expected: The medicine is created with the given name and type
     */
    @Test
    void testCreateValidMedicine() {
        Medicine validMedicine = new Medicine("Medicine for BA", "Injection");
        assertEquals("Medicine for BA", validMedicine.getName());
        assertEquals("Injection", validMedicine.getType());
    }

    /**
     * Test the constructor constraints for empty names
     * Expected: The empty name is replaced with Unnamed
     */
    @Test
    void testCreateEmptyStringNameMedicine() {
        Medicine emptyNameMedicine = new Medicine("", "Topical");
        assertEquals("Unnamed", emptyNameMedicine.getName());
    }

    /**
     * Test the constructor constraints for empty types
     * Expected: The empty type is replaced with Undefined
     */
    @Test
    void testCreateEmptyStringTypeMedicine() {
        Medicine emptyTypeMedicine = new Medicine("Medicine for BA", "");
        assertEquals("Undefined", emptyTypeMedicine.getType());
    }

    /**
     * Test the constructor constraints for null types
     * Expected: The null type is replaced with Undefined
     */
    @Test
    void testCreateNullTypeMedicine() {
        Medicine nullTypeMedicine = new Medicine("Medicine for BA", null);
        assertEquals("Undefined", nullTypeMedicine.getType());
    }

    /**
     * Test the constructor constraints for null names
     * Expected: The null name is replaced with Unnamed
     */
    @Test
    void testCreateNullNameMedicine() {
        Medicine nullNameMedicine = new Medicine(null, "Topical");
        assertEquals("Unnamed", nullNameMedicine.getName());
    }

    /**
     * Test that two medicines created the same way share the same property values
     * Expected: Medicines are identical
     */
    @Test
    void testTwoEqualMedicines() {
        Medicine med1 = new Medicine("Med1", "type");
        Medicine med2 = new Medicine("Med1", "type");
        assertThat(med1, samePropertyValuesAs(med2));
    }

    /**
     * Test medicine copying correctly changes all the fields
     */
    @Test
    void testMedicineCopy() {
        Medicine med1 = new Medicine("I used to be", "I used to be");
        Medicine med2 = new Medicine("I will become", "I will become");
        med1.copy(med2);
        assertThat(med1, samePropertyValuesAs(med2));
    }
    //Testing MedicineManagerInterface: getAllMedicines
    /**
     * Test the fill database method below, and the getAllMedicines method to show all are
     * successfully added.
     * Expected: All the medicines from the list getListOfMedicines() are added successfully to the database.
     */
    @Test
    void testFillingAndGetting() {
        ArrayList<Medicine> addedMedicines = getListOfMedicines();
        fillDatabase(addedMedicines);
        assertEquals(addedMedicines.size(), medicineManager.getAllMedicines().size());
    }

    /**
     * Test the fill database method such that all the medicines stored have matching names and types
     * to the ones added.
     */
    @Test
    void testFillingAndGettingValues() {
        ArrayList<Medicine> addedMedicines = getListOfMedicines();
        fillDatabase(addedMedicines);
        List<Medicine> foundMedicines = medicineManager.getAllMedicines();
        for (int i =0; i < foundMedicines.size() ; ++i) {
            Medicine foundMedicine = foundMedicines.get(i);
            Medicine addedMedicine = addedMedicines.get(i);
            assertEquals(addedMedicine.getName(), foundMedicine.getName());
            assertEquals(addedMedicine.getType(), foundMedicine.getType());
            assertNotNull(foundMedicine.getPrimaryKey());
        }
    }
    /**
     * Test that an empty table returns no medicine
     */
    @Test
    void testGetAllOnEmptyTable() {
        assertEquals(0, medicineManager.getAllMedicines().size());
    }
    //Testing MedicineManagerInterface: deleteAll

    /**
     * Testing a database can have deleteAll run on it, even if it is empty
     * Expected: The number of entries in the database remains zero.
     */
    @Test
    void testDeleteAllEmptyDatabase() {
        medicineManager.deleteAll();
        assertEquals(0, medicineManager.getAllMedicines().size());
        medicineManager.deleteAll();
        assertEquals(0, medicineManager.getAllMedicines().size());
    }

    /**
     * Testing a database will be flushed by the deleteAll method used between tests
     * Expected: The entries will disappear from the database.
     */
    @Test
    void testDeleteAllFilledDatabase() {
        ArrayList<Medicine> addedMedicines = getListOfMedicines();
        fillDatabase(addedMedicines);
        assertEquals(addedMedicines.size(), medicineManager.getAllMedicines().size());
        medicineManager.deleteAll();
        assertEquals(0, medicineManager.getAllMedicines().size());
    }

    //Testing MedicineManagerInterface: addMedicine
    /**
     * Test adding a regular medicine to the database.
     * Expected: A new medicine with the given type and name is saved to the database, and a fresh ID.
     */
    @Test
    void testAddMedicines() {
        medicineManager.addMedicine(new Medicine("Medicine for BA", "Topical"));
        medicineManager.addMedicine(new Medicine(1, "Medicine for BA", "Topical"));
        assertEquals(2, medicineManager.getAllMedicines().size());

        Medicine foundMedicine = medicineManager.getAllMedicines().get(0);
        assertEquals("Topical", foundMedicine.getType());
        assertNotNull(foundMedicine.getPrimaryKey());
    }

    /**
     * Test adding identical null valued Medicines to the database
     * Expected: The medicines' values are replaced and both added successfully.
     */
    @Test
    void testAddIdenticalMedicinesWithNullValues() {
        medicineManager.addMedicine(new Medicine(null, null));
        medicineManager.addMedicine(new Medicine(null, null, null));
        List<Medicine> addedMedicines = medicineManager.getAllMedicines();
        assertEquals(2, addedMedicines.size());
        assertEquals("Unnamed", addedMedicines.get(0).getName());
        assertEquals("Unnamed", addedMedicines.get(1).getName());
        assertEquals("Undefined", addedMedicines.get(0).getType());
        assertEquals("Undefined", addedMedicines.get(1).getType());
        assertNotNull(addedMedicines.get(0).getPrimaryKey());
        assertNotNull(addedMedicines.get(1).getPrimaryKey());
    }

    /**
     * Test adding empty valued Medicines to the database
     * Expected: The medicines' values are replaced and both added successfully.
     */
    @Test
    void testAddMedicineWithEmptyStringValues() {
        medicineManager.addMedicine(new Medicine("","     "));
        medicineManager.addMedicine(new Medicine("     ", "            "));
        List<Medicine> addedMedicines = medicineManager.getAllMedicines();
        assertEquals(2, addedMedicines.size());
        assertEquals("Unnamed", addedMedicines.get(0).getName());
        assertEquals("Unnamed", addedMedicines.get(1).getName());
        assertEquals("Undefined", addedMedicines.get(0).getType());
        assertEquals("Undefined", addedMedicines.get(1).getType());
        assertNotNull(addedMedicines.get(0).getPrimaryKey());
        assertNotNull(addedMedicines.get(1).getPrimaryKey());
    }

    /**
     * Test adding medicines with whitespace names to the database
     * Expected: The medicines are added as expected with preserved names.
     */
    @Test
    void testAddMedicineWithWhitespaceInValues() {
        String name = "Me di ci ne";
        String type = "Ty     pe";
        medicineManager.addMedicine(new Medicine(name, type));
        assertEquals(1, medicineManager.getAllMedicines().size());
        Medicine foundMedicine = medicineManager.getAllMedicines().get(0);
        assertEquals(name, foundMedicine.getName());
        assertEquals(type, foundMedicine.getType());
    }

    /**
     * Test adding medicines to the database with forbidden characters
     * Expected: No characters are forbidden - so medicines are added as expected.
     */
    @Test
    void testAddMedicineWithForbiddenCharactersInValues() {
        String forbiddenName = "''#~DROP TABLES';'\"@@";
        String forbiddenType = "''#^7%DROP TABLES;'";
        medicineManager.addMedicine(new Medicine(forbiddenName, forbiddenType));
        assertEquals(1, medicineManager.getAllMedicines().size());
        Medicine foundMedicine = medicineManager.getAllMedicines().get(0);
        assertEquals(forbiddenName, foundMedicine.getName());
        assertEquals(forbiddenType, foundMedicine.getType());
        assertNotNull(foundMedicine.getPrimaryKey());
    }

    //Testing MedicineManagerInterface: getByPrimaryKey

    /**
     * Testing that medicines can be found using their primary key
     * Expected: The medicine found shares the same values as the medicine in the database.
     */
    @Test
    void testGetByPrimaryKey() {
        fillDatabase(getListOfMedicines());
        Medicine foundMedicine = medicineManager.getAllMedicines().get(0);
        int medPK = foundMedicine.getPrimaryKey();
        Medicine foundMedicineUsingPK = medicineManager.getByPrimaryKey(medPK);
        assertThat(foundMedicine, samePropertyValuesAs(foundMedicineUsingPK));
    }

    /**
     * Testing that attempting to obtain a medicine using a primary key that doesn't exist returns null
     */
    @Test
    void testGetUnfoundPrimaryKey() {
        assertNull(medicineManager.getByPrimaryKey(-1));
    }

    /**
     * Testing an error is thrown if a primary key searched for is null
     */
    @Test
    void testGetNullPrimaryKey() {
        fillDatabase(getListOfMedicines());
        int previousSize = medicineManager.getAllMedicines().size();
        try {
            medicineManager.getByPrimaryKey(null);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(medicineManager.getAllMedicines().size(), previousSize);
        }
    }

    //Testing MedicineManagerInterface: delete

    /**
     * Tests that deleting a medicine from the list of them all reduces the number of medicines in the database.
     */
    @Test
    void testDeleteByPrimaryKey() {
        fillDatabase(getListOfMedicines());
        medicineManager.delete(medicineManager.getAllMedicines().get(1).getPrimaryKey());
        assertEquals( getListOfMedicines().size()-1, medicineManager.getAllMedicines().size());
        medicineManager.delete(medicineManager.getAllMedicines().get(1).getPrimaryKey());
        assertEquals(getListOfMedicines().size()-2, medicineManager.getAllMedicines().size());
    }

    /**
     * Test deleting a primary key which is not from the database.
     * Expected: The database remains unchanged and an error is thrown.
     */
    @Test
    void testDeleteUnfoundPrimaryKey() {
        fillDatabase(getListOfMedicines());
        int previousSize = medicineManager.getAllMedicines().size();

        try {
            medicineManager.delete(-1);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(medicineManager.getAllMedicines().size(), previousSize);
            // Check that nothing has been removed
        }
    }
    /**
     * Test deleting a primary key which is null.
     */
    @Test
    void testDeleteNullPrimaryKey() {
        fillDatabase(getListOfMedicines());
        int previousSize = medicineManager.getAllMedicines().size();

        try {
            medicineManager.delete(null);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(previousSize, medicineManager.getAllMedicines().size());
            // Check that nothing has been removed
        }
    }

    /**
     * Test the correct medicine is deleted by primary key
     */
    @Test
    void testCorrectMedicineDeletedUsingPrimaryKey() {
        Medicine toBeDeleted = medicineManager.addMedicine(new Medicine("I'll be deleted", "Delete me!"));
        Medicine alsoAdded = medicineManager.addMedicine(new Medicine("Another medicine", "Random"));
        assertEquals(2, medicineManager.getAllMedicines().size());
        medicineManager.delete(toBeDeleted.getPrimaryKey());
        assertEquals(1, medicineManager.getAllMedicines().size());
        Medicine leftoverMedicine = medicineManager.getAllMedicines().get(0);
        assertThat(alsoAdded, samePropertyValuesAs(leftoverMedicine));
        assertNull(medicineManager.getByPrimaryKey(toBeDeleted.getPrimaryKey()));
    }

    //Testing MedicineManagerInterface: update

    /**
     * Testing turning one of the existing medicines into a new medicine
     */
    @Test
    void testUpdateMedicine() {
        fillDatabase(getListOfMedicines());
        int id = medicineManager.getAllMedicines().get(0).getPrimaryKey();
        Medicine replacementMed = new Medicine(id, "Ibuprofen", "Pill");
        medicineManager.update(replacementMed);

        Medicine medicineFromDatabase = medicineManager.getByPrimaryKey(id);
        assertEquals(replacementMed.getName(), medicineFromDatabase.getName());
        assertEquals(replacementMed.getType(), medicineFromDatabase.getType());
    }

    /**
     * Testing updating a medicine with "bad" values
     */
    @Test
    void testUpdateMedicineWithEmptyNameAndNullType() {
        fillDatabase(getListOfMedicines());
        int id = medicineManager.getAllMedicines().get(0).getPrimaryKey();
        Medicine replacementMed = new Medicine(id, "", null);
        medicineManager.update(replacementMed);
        Medicine medicineFromDatabase = medicineManager.getByPrimaryKey(id);
        assertEquals("Unnamed", medicineFromDatabase.getName());
        assertEquals("Undefined", medicineFromDatabase.getType());
    }

    /**
     * Test what happens if a null medicine is updated
     */
    @Test
    void testUpdateNullMedicine() {
        fillDatabase(getListOfMedicines());
        int previousSize = medicineManager.getAllMedicines().size();
        try {
            medicineManager.update(new Medicine());
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(previousSize, medicineManager.getAllMedicines().size());
        }
    }

    /**
     * Testing updating a medicine that doesn't exist
     */
    @Test
    void testUpdateNonExistentMedicine() {
        medicineManager.deleteAll();
        Medicine replacementMed = new Medicine(-1, "Hello I don't exist", null);
        medicineManager.update(replacementMed);
        assertEquals(0, medicineManager.getAllMedicines().size());
    }


    /**
     * List to fill in database with example Medicine objects
     * @return example objects
     */
    private static ArrayList<Medicine> getListOfMedicines() {
        ArrayList<Medicine> listOfMedicines = new ArrayList<>();

        // TYPES : Liquid, Tablet, Capsule, Injection, Topical, Suppositories, Drops, Inhalers
        listOfMedicines.add(new Medicine("Med1", "Liquid"));
        listOfMedicines.add(new Medicine("Med2", "Tablet"));
        listOfMedicines.add(new Medicine("Med3", "Injection"));
        listOfMedicines.add(new Medicine("Med4", "Tablet"));
        listOfMedicines.add(new Medicine("Med4", "Liquid"));
        listOfMedicines.add(new Medicine("Med6", "Drops"));
        listOfMedicines.add(new Medicine("Med7", "Inhaler"));
        listOfMedicines.add(new Medicine("Med7", "Inhaler"));
        listOfMedicines.add(new Medicine("Med_8/#", "Liquid"));
        listOfMedicines.add(new Medicine(-1, "Med7", "Topical"));

        return listOfMedicines;
    }

    /**
     * Fill the database with a list of medicines
     * @param listOfMeds The List of medicines to fill the database with.
     */
    private void fillDatabase(ArrayList<Medicine> listOfMeds) {
        for (int i = 0; i<listOfMeds.size(); ++i) medicineManager.addMedicine(listOfMeds.get(i));
    }
}