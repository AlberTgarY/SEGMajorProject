package test.java;

import io.micronaut.test.annotation.MicronautTest;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;

import javax.inject.Inject;

import main.java.com.projectBackEnd.Services.Medicine.Hibernate.Medicine;
import main.java.com.projectBackEnd.Services.Medicine.Hibernate.MedicineManager;
import main.java.com.projectBackEnd.Services.Medicine.Hibernate.MedicineManagerInterface;
import main.java.com.projectBackEnd.Services.Medicine.Micronaut.MedicineAddCommand;
import main.java.com.projectBackEnd.Services.Medicine.Micronaut.MedicineUpdateCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;


import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import main.java.com.projectBackEnd.Services.User.Hibernate.UserManager;
import main.java.com.projectBackEnd.HibernateUtility;

/**
 * The purpose of this class is to test the REST endpoints associated with the medicine entity through the medicine controller
 */
@MicronautTest
class MedicineControllerTest{

    @Inject
    @Client("/")
    private HttpClient client;

    private static MedicineManagerInterface medicineManager;
    private static String token;

    /**
     * Sets the config resource location and the medicine manager. Also generates the token attribute for auth.
     */
    @BeforeAll
    static void setUpDatabase() {
        HibernateUtility.setResource("testhibernate.cfg.xml");
        medicineManager = MedicineManager.getMedicineManager();
        try{
            UserManager.getUserManager().addUser("MedicineTest@test.com", "123", "name");
            Thread.sleep(100); //A sleep to give the database a chance to update
            token = UserManager.getUserManager().verifyUser("MedicineTest@test.com", "123");
        } catch(Exception e){
        	fail();
        }    
	}
    /**
     * Closes the session factory and deletes the testing user
     */
    @AfterAll
    static void closeDatabase() {
        try{
        	UserManager.getUserManager().deleteUser("MedicineTest@test.com" , "123");
        } catch(Exception e){
        	fail();
        }    
        HibernateUtility.shutdown();
    }
    /**
     * Ensure that there are no pre-existing medicine entities in the database before each test
     */
    @BeforeEach
    void setUp() {
        medicineManager.deleteAll();

    }

    /**
     * Attempts to retrieve a medicine that does not exist in the database via the GET request, expects an
     * Http error to be thrown
     */
    @Test
    void testNonExistingMedicineReturns404() {
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.GET("/medicines/3524"));
        });

        assertNotNull(thrown.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    /**
     * Tests the medicine request responsible for retrieving all medicines as a list, via the GET request,
     * expects success
     */
    @Test
    void testAddAndGetAll(){
        ArrayList<Integer> ids = new ArrayList<>();
        HttpResponse response;
        for (int i=0;i<3;i++) {
            response = addMedicine(new MedicineAddCommand("ShouldBeDeleted", "Liquid"));
            ids.add(getEId(response).intValue());
        }

        HttpRequest request = HttpRequest.GET("/medicines");
        List<Medicine> medicineList = client.toBlocking().retrieve(request, Argument.of(List.class, Medicine.class));

        for(int i=0; i<ids.size();i++) assertEquals(ids.get(i), medicineList.get(i).getPrimaryKey());

    }

    /**
     * Adds a medicine with an empty name and tests that validations auto generate a name for the object
     */
    @Test
    void testAddEmptyNameMedicine(){
        HttpResponse response = addMedicine(new MedicineAddCommand("", "Topical"));
        int id =  getEId(response).intValue();
        Medicine testMed = getMedicine(id);
        assertEquals("Unnamed", testMed.getName());
    }

    /**
     * Adds a medicine with an empty type and tests that validations auto generate a type string for the object
     */
    @Test
    void testAddEmptyTypeMedicine(){
        HttpResponse response = addMedicine(new MedicineAddCommand("Med1", ""));
        int id =  getEId(response).intValue();
        Medicine testMed = getMedicine(id);
        assertEquals("Undefined", testMed.getType());
    }

    /**
     * Tests that the endpoint is able to retrieve an existing medicine, expects success
     */
    @Test
    void testAddAndGetMedicine(){
        HttpResponse response = addMedicine(new MedicineAddCommand("Med1", "Liquid"));
        int id =  getEId(response).intValue();

        Medicine testMed = getMedicine(id);

        assertEquals("Med1", testMed.getName());
    }

    /**
     * Tests that the endpoint is able to add a legal medicine to the database, expects success
     */
    @Test
    void testAddLegalMedicine(){
        HttpResponse response= addMedicine(new MedicineAddCommand("Med1", "Liquid"));
        assertEquals(HttpStatus.CREATED, response.getStatus());
    }
	/**
	*	Check if adding medicine without a valid session token returns HTTP unauthorized exception
	*/
	@Test
	void testAddMedicineUnauthorized(){
		HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
			client.toBlocking().exchange(HttpRequest.POST("/medicines", new MedicineAddCommand("name", "type")).header("X-API-Key",""));
        });
        assertEquals(HttpStatus.UNAUTHORIZED, thrown.getStatus());

		HttpClientResponseException thrown1 = assertThrows(HttpClientResponseException.class, () -> {
			client.toBlocking().exchange(HttpRequest.POST("/medicines", new MedicineAddCommand("name", "type")).header("X-API-Key","SOmeVeryCo2rr45ECt231TokEN1"));
        });
        assertEquals(HttpStatus.UNAUTHORIZED, thrown1.getStatus());
	}
	/**
	*	Test if adding a medicine with null fields renames them to correct default values
	*/
	@Test
	void testAddMedicineNull(){
        HttpResponse response = addMedicine(new MedicineAddCommand(null, null));
        int id =  getEId(response).intValue();
        Medicine testMed = getMedicine(id);
        assertEquals("Undefined", testMed.getType());
        assertEquals("Unnamed", testMed.getName());

        HttpResponse response1 = addMedicine(new MedicineAddCommand("Med1", null));
        int id1 =  getEId(response1).intValue();
        Medicine testMed1 = getMedicine(id1);
        assertEquals("Undefined", testMed1.getType());

        HttpResponse response2 = addMedicine(new MedicineAddCommand(null, "type"));
        int id2 =  getEId(response2).intValue();
        Medicine testMed2 = getMedicine(id2);
        assertEquals("Unnamed", testMed2.getName());
	}

    /**
     * Attempts to delete an existing medicine and retrieve is via the GET request, expects an HTTP error to be thrown
     */
    @Test
    void testDeleteAndGetMedicine(){
        HttpResponse response = addMedicine(new MedicineAddCommand("Med1", "Liquid"));
        int id =  getEId(response).intValue();
        assertEquals(HttpStatus.CREATED, response.getStatus());

        HttpRequest request = HttpRequest.DELETE("/medicines/"+id).header("X-API-Key",token);
        client.toBlocking().exchange(request);
        assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.GET("/medicines/"+id));
        });
    }
	/**
	*	Test if deleting medicine without correct session token return HTTP unauthorized exception
	*/
	@Test
	void testDeleteMedicineUnauthorized(){
        addMedicine(new MedicineAddCommand("name", "type"));
		HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
			client.toBlocking().exchange(HttpRequest.DELETE("/medicines/0").header("X-API-Key",""));
        });
        assertEquals(HttpStatus.UNAUTHORIZED, thrown.getStatus());

	HttpClientResponseException thrown1 = assertThrows(HttpClientResponseException.class, () -> {
			client.toBlocking().exchange(HttpRequest.DELETE("/medicines/0").header("X-API-Key","SOmeVeryCo2rr45ECt231TokEN1"));
        });
        assertEquals(HttpStatus.UNAUTHORIZED, thrown1.getStatus());
	}
	/**
	*	Test if deleting a medicine with wrong ID returns a HTTP exception
	*/
	@Test
	void testDeleteMedicineNotExist(){
		HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
			client.toBlocking().exchange(HttpRequest.DELETE("/medicines/-1").header("X-API-Key",token));
        });
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getStatus());
		HttpClientResponseException thrown1 = assertThrows(HttpClientResponseException.class, () -> {
			client.toBlocking().exchange(HttpRequest.DELETE("/medicines/").header("X-API-Key",token));
        });
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, thrown1.getStatus());
	}
    /**
     * Tests that the endpoint is able to update an existing medicine with legal information
     */
    @Test
    void testPutLegalMedicine(){
        HttpResponse response= addMedicine(new MedicineAddCommand("Med1", "Liquid"));
        int id =  getEId(response).intValue();
        response = putMedicine(new MedicineUpdateCommand(id, "NewName", "NewType"));
        assertEquals(HttpStatus.NO_CONTENT, response.getStatus());
    }
    /**
     * Updates an existing medicine with an empty name and tests that validations auto generate a type for the object
     */
    @Test
    void testUpdateMedicineEmptyType() {
        HttpResponse response = addMedicine(new MedicineAddCommand("Med1", "Liquid"));
        int id =  getEId(response).intValue();
        putMedicine(new MedicineUpdateCommand(id, "name", ""));
        Medicine found = getMedicine(id);
        assertEquals("Undefined", found.getType());
    }
    /**
     * Updates an existing medicine with an empty name and tests that validations auto generate a name for the object,
     */
    @Test
    void testUpdateMedicineEmptyName() {
        HttpResponse response = addMedicine(new MedicineAddCommand("Med1", "Liquid"));
        int id =  getEId(response).intValue();
        client.toBlocking().exchange(HttpRequest.PUT("/medicines", new MedicineUpdateCommand(id, "", "type")).header("X-API-Key",token));
        Medicine found = getMedicine(id);
        assertEquals("Unnamed", found.getName());
    }
	/**
	 * Tests if updating medicine to empty values sets the values to default ones
	 */
	@Test
	void testUpdateMedicineNull(){
        HttpResponse response = addMedicine(new MedicineAddCommand("Med1", "Liquid"));
        int id =  getEId(response).intValue();

        client.toBlocking().exchange(HttpRequest.PUT("/medicines", new MedicineUpdateCommand(id, null, "type")).header("X-API-Key",token));
        Medicine found = getMedicine(id);
        assertEquals("Unnamed", found.getName());

        client.toBlocking().exchange(HttpRequest.PUT("/medicines", new MedicineUpdateCommand(id, null, null)).header("X-API-Key",token));
        Medicine found1 = getMedicine(id);
        assertEquals("Unnamed", found1.getName());
        assertEquals("Undefined", found1.getType());

		client.toBlocking().exchange(HttpRequest.PUT("/medicines", new MedicineUpdateCommand(id, "name", null)).header("X-API-Key",token));
        Medicine found2 = getMedicine(id);
        assertEquals("Undefined", found2.getType());

	}
	/**
	*	Test if updating medicine without correct session token returns a HTTP unauthorized exception
	*/
	@Test
	void testUpdateMedicineUnauthorized(){
        addMedicine(new MedicineAddCommand("name", "type"));
		HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
			client.toBlocking().exchange(HttpRequest.PUT("/medicines", new MedicineUpdateCommand(0, "name", "type")).header("X-API-Key",""));
        });
        assertEquals(HttpStatus.UNAUTHORIZED, thrown.getStatus());

		HttpClientResponseException thrown1 = assertThrows(HttpClientResponseException.class, () -> {
			client.toBlocking().exchange(HttpRequest.PUT("/medicines", new MedicineUpdateCommand(0, "name", "type")).header("X-API-Key","SOmeVeryCo2rr45ECt231TokEN1"));
        });
        assertEquals(HttpStatus.UNAUTHORIZED, thrown1.getStatus());
	}
    /**
     * Tests that a medicine can be added and updated
     */
    @Test
    void testAddAndUpdateMedicine(){
        HttpResponse response = addMedicine(new MedicineAddCommand("Med1", "Liquid"));
        int id =  getEId(response).intValue();

        putMedicine(new MedicineUpdateCommand(id, "newName", "newType"));

        Medicine m = getMedicine(id);
        assertEquals("newName", m.getName());
        assertEquals("newType", m.getType());
    }
    /**
     * Quality of life method for updating a medicine via the REST API
     * @param medicineToPut The medicine to be updated
     * @return The HTTP response produced by the operation
     */
    private HttpResponse putMedicine(MedicineUpdateCommand medicineToPut){
        HttpRequest request = HttpRequest.PUT("/medicines", medicineToPut).header("X-API-Key",token);
        return client.toBlocking().exchange(request);
    }
    /**
     * Quality of life method for adding a medicine via the REST API
     * @param medicineToAdd The medicine to add
     * @return The HTTP response produced by the operation
     */
    private HttpResponse addMedicine(MedicineAddCommand medicineToAdd){
        HttpRequest request = HttpRequest.POST("/medicines", medicineToAdd).header("X-API-Key",token);
        return client.toBlocking().exchange(request);
    }
    /**
     * Quality of life method for retrieving a medicine via the REST API
     * @param id The ID/Primary Key of the medicine to get
     * @return The medicine object returned
     */
    private Medicine getMedicine(int id){
        HttpRequest request = HttpRequest.GET("/medicines/" + id);
        return client.toBlocking().retrieve(request, Medicine.class);

    }

    /**
     * Method for returning the ID of an object involved in a HTTP response
     * @param response The response from an object
     * @return The ID of the object involved
     */
    Long getEId(HttpResponse response) {
        String val = response.header(HttpHeaders.LOCATION);
        if (val != null) {
            int index = val.indexOf("/medicines/");
            if (index != -1) return Long.valueOf(val.substring(index + "/medicines/".length()));
            else return null;
        }
        else return null;
    }



}
