package main.java.com.projectBackEnd.Services.Site.Micronaut;

import javax.validation.constraints.NotNull;
import io.micronaut.core.annotation.Introspected;

/**
 * SiteUpdateCommand is used by its controller to update a site object stored in the database.
 * It creates mock Site objects to reduce memory use.
 */
@Introspected
public class SiteUpdateCommand extends SiteAddCommand {

    @NotNull
    private int primaryKey;

    /**
     * Default constructor
     */
    public SiteUpdateCommand(){}
	
    /**
     * Main constructor for SiteUpdateCommand objects
     * @param id    Primary key 'ID' of the site
     * @param slug  Slug of the site
     * @param site  Name of the site
     */
    public SiteUpdateCommand(Integer id, String slug, String site){
        super(slug, site);
        this.primaryKey = id;
    }
	
	/**
	 * get the PrimaryKey
	 * @return  the primaryKey
	 */
    public int getPrimaryKey() { return primaryKey; }

}
