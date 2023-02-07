package main.java.com.projectBackEnd.Services.Medicine.Hibernate;

import java.io.Serializable;
import java.util.List;


/**
 *  Methods used by MedicineManager for database queries.
 */
public interface MedicineManagerInterface {

    Medicine addMedicine(Medicine med);

    Medicine update(Medicine med);

    Medicine getByPrimaryKey(Serializable id);

    List<Medicine> getAllMedicines();

    void delete(Serializable pk);

    void deleteAll();

}
