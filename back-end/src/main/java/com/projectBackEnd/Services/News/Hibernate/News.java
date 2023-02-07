package main.java.com.projectBackEnd.Services.News.Hibernate;

import main.java.com.projectBackEnd.TableEntity;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * News objects are database entities for the table 'News' defined in this class.
 * Each news article has an ID as a auto-incremented primary key.
 * It can be Pinned, or Urgent, depending on its importance.
 *
 *    Inspiration : https://examples.javacodegeeks.com/enterprise-java/hibernate/hibernate-annotations-example/
 */
@Entity
@Table(name = News.TABLENAME, uniqueConstraints = {@UniqueConstraint(columnNames = {News.SLUG})})
public class News implements TableEntity<News> {

    // 'News' database table name and attributes
    static final String TABLENAME = "News";
    private static final String ID = "ID";
    private static final String DATE = "Date";
    private static final String PINNED = "Pinned";
    private static final String DESCRIPTION = "Description";
    private static final String TITLE = "Title";
    private static final String URGENT = "Urgent";
    private static final String CONTENT = "Content";
    static final String SLUG = "Slug";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID, nullable = false)
    private Integer primaryKey;

    @NotNull
    @Temporal(TemporalType.DATE)
    @Column(name = DATE, nullable = false)
    private Date date;

    @NotNull
    @Type(type="text")
    @Column(name = DESCRIPTION, nullable = false)
    private String description;

    @NotNull
    @Type(type="text")
    @Column(name = CONTENT, nullable = false)
    private String content;

    @NotNull
    @Type(type="text")
    @Column(name = SLUG, nullable = false, unique = true)
    private String slug;

    @NotNull
    @Type(type="text")
    @Column(name = TITLE, nullable = false)
    private String title;

    @NotNull
    @Column(name = URGENT, nullable = false)
    private boolean urgent;

    @NotNull
    @Column(name = PINNED, nullable = false)
    private boolean pinned;


    /**
     * Constructor for News
     */
    public News() {}


    /**
     *  Main constructor for News object creation
     * The primary key is auto-incremented in the database
     * @param date          Date of publication for the News object
     * @param pinned        Pinned status of the News object
     * @param description   Description of the News object
     * @param title         Title of the News object
     * @param urgent        Urgency status of the News object
     * @param content       Content of the News object
     * @param slug          Slug of the News object
     */
    public News(Date date, boolean pinned, String description, String title, boolean urgent, String content, String slug) {
        this.primaryKey = -1;
        this.date = date;
        this.pinned = pinned;
        this.description = description;
        this.title = title;
        this.urgent = urgent;
        this.content = content;
        this.slug = slug;
    }

    /**
     * Constructor taking primary key
     * @param primaryKey    Primary key of the News object
     * @param date          Date of publication for the News object
     * @param pinned        Pinned status of the News object
     * @param description   Description of the News object
     * @param title         Title of the News object
     * @param urgent        Urgency status of the News object
     * @param content       Content of the News object
     * @param slug          Slug of the News object
     */
    public News(Integer primaryKey, Date date, boolean pinned, String description, String title, boolean urgent, String content, String slug) {
        this.date = date;
        this.pinned = pinned;
        this.description = description;
        this.title = title;
        this.urgent = urgent;
        this.content = content;
        this.primaryKey = primaryKey;
        this.slug = slug;
    }



    /**
     * Get primary key of the object
     * @return primary key value
     */
    @Override
    public Integer getPrimaryKey() {
        return primaryKey;
    }


    /**
     * Get date of the object
     * @return date value
     */
    public Date getDate() {
        return date;
    }


    /**
     * Set date as input date
     * @param date New date value
     */
    public void setDate(Date date) {
        this.date = date;
    }


    /**
     * Get description of the object
     * @return description
     */
    public String getDescription() {
        return description;
    }


    /**
     * Set description as input description
     * @param description New description value
     */
    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * Get content of the object
     * @return content value
     */
    public String getContent() {
        return content;
    }


    /**
     * Set content of the object as input content
     * @param content New content value
     */
    public void setContent(String content) {
        this.content = content;
    }


    /**
     * Get slug of the object
     * @return slug value
     */
    public String getSlug() {
        return slug;
    }


    /**
     * Set slug of the object as input slug
     * @param slug New slug value
     */
    public void setSlug(String slug) {
        this.slug = slug;
    }


    /**
     * Get title of the object
     * @return title value
     */
    public String getTitle() {
        return title;
    }


    /**
     * Set title as input title
     * @param title New title value
     */
    public void setTitle(String title) {
        this.title = title;
    }


    /**
     * Get the urgency status of the News object
     * @return true if urgent, else false
     */
    public boolean isUrgent() { //Might need to become getUrgent since Jackson conversion
        return urgent;
    }


    /**
     * Set the urgency status of the object to input urgency status
     * @param urgent New urgency value
     */
    public void setUrgent(boolean urgent) {
        this.urgent = urgent;
    }


    /**
     * Get the pinned status of the News object
     * @return true if pinned, else false
     */
    public boolean isPinned() {
        return pinned;
    }


    /**
     * Set the pinned status of the object to input pinned status
     * @param pinned New pinned value
     */
    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }


    /**
     * Copy the values of the input TableEntity object
     * @param newsToCopy    News object to copy
     * @return this, updated news object
     */
    @Override
    public News copy(News newsToCopy) {
        setDate(newsToCopy.getDate());
        setDescription(newsToCopy.getDescription());
        setContent(newsToCopy.getContent());
        setPinned(newsToCopy.isPinned());
        setTitle(newsToCopy.getTitle());
        setUrgent(newsToCopy.isUrgent());
        setSlug(newsToCopy.getSlug());
        return this;
    }

}
