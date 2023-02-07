package main.java.com.projectBackEnd.Services.Medicine.Hibernate;
import java.util.List;
import java.io.Serializable;

import main.java.com.projectBackEnd.EntityManager;
import main.java.com.projectBackEnd.HibernateUtility;

/**
 * MedicineManager defines methods to interact with the Medicine table in the database.
 * This class extends the EntityManager - supplying it with the rest of its interface methods.
 *
 * Inspiration : https://examples.javacodegeeks.com/enterprise-java/hibernate/hibernate-annotations-example/
 */
public class MedicineManager extends EntityManager implements MedicineManagerInterface {

    private static MedicineManagerInterface medicineManager;


    /**
     * Private constructor implementing the Singleton design pattern
     */
    private MedicineManager() {
        super();
        setSubclass(Medicine.class);
        HibernateUtility.addAnnotation(Medicine.class);
        medicineManager = this;
    }


    /**
     * Get medicine manager interface
     * @return medicineManager ; if none has been defined, create a new MedicineManager()
     */
    public static MedicineManagerInterface getMedicineManager() {
        return (medicineManager != null) ? medicineManager : new MedicineManager();
    }


    /**
     * Insert a medicine object into database
     * @param med   Medicine object to add to the db
     * @return added object with a replaced ID.
     */
    public Medicine addMedicine(Medicine med) {
        super.insertTuple(med);
        return med;
    }


    /**
     * Update attributes of Medicine object
     * @param med Medicine object with updated attributes
     * @return updated object
     */
    public Medicine update(Medicine med) {
        return (Medicine) super.update(med);
    }


    /**
     * Retrieve Medicine object corresponding to input 'ID' in the database
     * @param id Primary key
     * @return Medicine object with corresponding ID
     */
    @Override
    public Medicine getByPrimaryKey(Serializable id) {
        return (Medicine) super.getByPrimaryKey(id);
    }


    /**
     * Get all the Medicine objects stored in the Medicine table of the database
     * @return a list of all medicines in database
     */
    public List<Medicine> getAllMedicines() {
        return (List<Medicine>) super.getAll();
    }


}
