package main.java.com.projectBackEnd.Services.Page.Micronaut;
import javax.validation.constraints.NotNull;

import io.micronaut.core.annotation.Introspected;


/**
 * PageUpdateCommand is used by its controller to update a page object stored in the database.
 * It creates mock Page objects to reduce memory use.
 */
@Introspected
public class PageUpdateCommand extends PageAddCommand {

    @NotNull
    private int primaryKey;

    /**
     * Default Constructor
     */
    public PageUpdateCommand(){
        super();
    }


    /**
     * Main constructor for PageUpdateCommand
     * @param id        Primary key of the Page
     * @param site      Site the Page belongs to
     * @param slug      Slug of the Page
     * @param index     Index of the Page on its parent Site
     * @param title     Title of the page
     * @param content   Content of the page
     */
    public PageUpdateCommand(Integer id, String site, String slug, Integer index, String title, String content){
        super(site, slug, index, title, content);
        this.primaryKey = id;
    }


    /**
     * Get the primary key 'ID' of the page
     * @return id
     */
    public int getPrimaryKey() { return primaryKey; }

}