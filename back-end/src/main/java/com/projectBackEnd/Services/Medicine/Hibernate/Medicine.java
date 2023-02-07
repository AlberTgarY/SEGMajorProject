package main.java.com.projectBackEnd.Services.Medicine.Hibernate;

import main.java.com.projectBackEnd.TableEntity;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import javax.validation.constraints.NotNull;
/**
 * Medicine objects are database entities for the table 'Medicines' defined in this class.
 * The have an auto-increment 'ID' from the database.
 * Inspiration : https://examples.javacodegeeks.com/enterprise-java/hibernate/hibernate-annotations-example/
 */
@Entity
@Table(name = Medicine.TABLENAME)
public class Medicine implements TableEntity<Medicine> {

    // 'Medicine' database table name and attributes
    static final String TABLENAME = "Medicines";
    private static final String ID = "ID";
    private static final String NAME = "Name";
    private static final String TYPE = "Type";

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID, nullable = false)
    private Integer primaryKey;

    @NotNull
    @Type(type = "text")
    @Column(name = NAME, nullable = false)
    private String name;

    @NotNull
    @Type(type="text")
    @Column(name = TYPE, nullable = false)
    private String type;


    /**
     * Default constructor
     */
    public Medicine() {}


    /**
     * Main constructor
     * The PK 'ID' is auto-increment in the database.
     * @param name name of the medicine
     * @param type type of the medicine
     */
    public Medicine(String name, String type) {
        this.primaryKey = -1;
        setName(name);
        setType(type);
    }


    /**
     * Constructor taking PK for object re-creation
     * @param id    primary key of the object
     * @param name  name of the medicine
     * @param type  type of the medicine
     */
    public Medicine(Integer id, String name, String type) {
        this.primaryKey = id;
        setName(name);
        setType(type);
    }


    /**
     * Get the ID of the medicine object
     * @return Primary key ID
     */
    public Integer getPrimaryKey() {
        return primaryKey;
    }


    /**
     * Get the name of the medicine object
     * @return Name
     */
    public String getName() {
        return name;
    }


    /**
     * Change the name of the medicine object
     * @param name New name
     */
    public void setName(String name) {
        this.name = (name == null || "".equals(name.trim())) ? "Unnamed" : name;
    }


    /**
     * Get the type of the medicine object
     * @return type
     */
    public String getType() {
        return type;
    }


    /**
     * Change the type of the medicine object
     * @param type New type
     */
    public void setType(String type) {
        this.type = (type == null || "".equals(type.trim())) ? "Undefined" : type;
    }


    /**
     * Copy the values of the input TableEntity object
     * @param medicineToCopy    Medicine object to copy
     * @return this, updated medicine object
     */
    @Override
    public Medicine copy(Medicine medicineToCopy) {

        setName(medicineToCopy.getName());
        setType(medicineToCopy.getType());

        return this;

    }


}



