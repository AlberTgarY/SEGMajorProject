package main.java.com.projectBackEnd.Services.Medicine.Micronaut;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;

import java.net.URI;
import java.util.List;

import main.java.com.projectBackEnd.Services.Medicine.Hibernate.Medicine;
import main.java.com.projectBackEnd.Services.Medicine.Hibernate.MedicineManager;
import main.java.com.projectBackEnd.Services.Medicine.Hibernate.MedicineManagerInterface;
import main.java.com.projectBackEnd.Services.Session.SessionManager;
import main.java.com.projectBackEnd.Services.Session.SessionManagerInterface;

/**
 * Medicine Controller is a REST API endpoint.
 * It deals with the interactions between the server and the Medicine table in the database.
 * It provides HTTP requests for each of the queries that need to be made to add, remove, update and retrieve
 * medicines from the database.
 */
@Controller("/medicines")
public class MedicineController {

    private final MedicineManagerInterface medicineManager = MedicineManager.getMedicineManager();

    protected final SessionManagerInterface sessionManager = SessionManager.getSessionManager();

    /**
     * Insert a new medicine into the database using MedicineAddCommand methods via an HTTP Post request
     * @param session   Current session
     * @param command   Dedicated MedicineAddCommand class to add new medicine to the database
     * @return HTTP response with relevant information resulting from the insertion of the medicine
     */
    @Post("/")
    public HttpResponse<Medicine> add(@Header("X-API-Key") String session, @Body MedicineAddCommand command) {

        if(!sessionManager.verifySession(session)) return HttpResponse.unauthorized();
        Medicine med = medicineManager.addMedicine(new Medicine(command.getName(), command.getType()));

        return HttpResponse
                .created(med)
                .headers(headers -> headers.location(location(med.getPrimaryKey())));
    }


    /**
     * Remove the medicine corresponding to the given ID from database via an HTTP Delete request
     * @param session   Current session
     * @param id        Primary key of the medicine to delete
     * @return Http response with relevant information resulting from the deletion of the medicine
     */
    @Delete("/{id}")
    public HttpResponse delete(@Header("X-API-Key") String session, int id) {

        if(!sessionManager.verifySession(session)) return HttpResponse.unauthorized();
        medicineManager.delete(id);

        return HttpResponse.noContent();
    }


    /**
     * Update a medicine with MedicineUpdateCommand methods via an HTTP Put request
     * @param session   Current session
     * @param command   Dedicated MedicineUpdateCommand class to update the medicine
     * @return HTTP response resulting from the Put request with path
     */
    @Put("/")
    public HttpResponse update(@Header("X-API-Key") String session,@Body MedicineUpdateCommand command) {
		if(!sessionManager.verifySession(session))
			return HttpResponse.unauthorized();
        Medicine medObject = new Medicine(command.getPrimaryKey(), command.getName(), command.getType());
        medicineManager.update(medObject);

        return HttpResponse
                .noContent()
                .header(HttpHeaders.LOCATION, location(command.getPrimaryKey()).getPath());
    }


    /**
     * Get the specific medicine by HTTP GET method
     * @param id   Primary key of the medicine to retrieve
     * @return get the Medicine with the specified id
     */
    @Get(value = "/{id}", produces = MediaType.TEXT_JSON)
    public Medicine getMedicine(int id) {
        return medicineManager.getByPrimaryKey(id);
    }


    /**
     * Get all the medicines from the database by HTTP GET method
     * @return list of all the Medicines
     */
    @Get("/")
    public List<Medicine> getAllMedicines(){
        return medicineManager.getAllMedicines();
    }


    /**
     * Create URI of the medicine with its specified id
     * @param id Primary key of the medicine to locate
     * @return created URI
     */
    private URI location(int id) {
        return URI.create("/medicines/" + id);
    }

}
