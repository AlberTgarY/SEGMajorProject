package main.java.com.projectBackEnd.Services.News.Micronaut;

import javax.validation.constraints.NotNull;
import java.util.Date;
import io.micronaut.core.annotation.Introspected;

/**
 * NewsAddCommand is used by the controller to insert a News object into the database.
 * It creates mock News objects to reduce memory use.
 */
@Introspected
public class NewsAddCommand {

    @NotNull
    private Date date;

    @NotNull
    private String description;

    @NotNull
    private String content;

    @NotNull
    private String slug;

    @NotNull
    private String title;

    @NotNull
    private boolean urgent;

    @NotNull
    private boolean pinned;


    /**
     * Default NewsAddCommand constructor
     */
    public NewsAddCommand() {}


    /**
     * Constructor for NewsAddCommand object creation
     * @param date          Date of publication for the mock News object
     * @param pinned        Pinned status of the mock News object
     * @param description   Description of the mock News object
     * @param title         Title of the mock News object
     * @param urgent        Urgency status of the mock News object
     * @param content       Content of the mock News object
     * @param slug          Slug of the mock News object
     */
    public NewsAddCommand(Date date, boolean pinned, String description, String title, boolean urgent, String content, String slug) {
        this.date = date;
        this.pinned = pinned;
        this.description = description;
        this.title = title;
        this.urgent = urgent;
        this.content = content;
        this.slug = slug;
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
    public boolean isUrgent() {
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


}
