package main.java.com.projectBackEnd.Services.News.Micronaut;

import io.micronaut.core.annotation.Introspected;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * NewsUpdateCommand is used by its controller to update a news object in the database.
 * It creates mock News objects to reduce memory use.
 */
@Introspected
public class NewsUpdateCommand extends NewsAddCommand {

    @NotNull
    private int primaryKey;


    /**
     * Default constructor
     */
    public NewsUpdateCommand() {super();}


    /**
     *  Constructor for NewsUpdateCommand object creation
     * @param id            Primary key of the mock News object
     * @param date          Date of publication for the mock News object
     * @param title         Title of the mock News object
     * @param description   Description of the mock News object
     * @param content       Content of the mock News object
     * @param pinned        Pinned status of the mock News object
     * @param urgent        Urgency status of the mock News object
     * @param slug          Slug of the mock News object
     */
    public NewsUpdateCommand(Integer id, Date date, boolean pinned, String description, String title, boolean urgent,
                             String content, String slug) {
        super(date, pinned, description, title, urgent, content, slug);
        this.primaryKey = id;
    }

    /**
     * Get the id of the object
     * @return primary key ID
     */
    public int getPrimaryKey() { return primaryKey; }

}
