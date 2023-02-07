package main.java.com.projectBackEnd.Services.Medicine.Micronaut;

import javax.validation.constraints.NotNull;
import io.micronaut.core.annotation.Introspected;

/**
 * MedicineUpdateCommand creates mock medicine objects and reduces memory use.
 * It is used by the controller to update a medicine object.
 */
@Introspected
public class MedicineUpdateCommand extends MedicineAddCommand {

    @NotNull
    private int primaryKey;


    /**
     * Default constructor
     */
    public MedicineUpdateCommand() {
        super();
    }


    /**
     * Main constructor for MedicineUpdateCommand object creation
     * @param id    Primary key of mock Medicine object
     * @param name  Name of mock Medicine object
     * @param type  Type of mock Medicine object
     */
    public MedicineUpdateCommand(int id, String name, String type) {
        super(name, type);
        this.primaryKey = id;
    }


    /**
     * Get the ID of the object
     * @return primary key 'ID'
     */
    public int getPrimaryKey() { return primaryKey; }


}

