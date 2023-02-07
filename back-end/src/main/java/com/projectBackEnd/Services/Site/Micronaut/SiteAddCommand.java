package main.java.com.projectBackEnd.Services.Site.Micronaut;

import javax.validation.constraints.NotNull;
import io.micronaut.core.annotation.Introspected;

/**
 * SiteAddCommand creates mock Site objects to reduce memory use.
 * It is used by the controller to insert a Site object into the database
 */
@Introspected
public class SiteAddCommand {
	
    @NotNull
    private String name;
	
    @NotNull
    private String slug;

    /**
     * Default constructor
     */
    SiteAddCommand(){}

    /**
     * Main constructor for SiteAddCommand objects creation
     * @param slug  Slug of the mock Site
     * @param name  Name of the mock Site
     */
    public SiteAddCommand(String slug, String name){
        this.slug = slug;
        this.name = name;
    }

    /**
     * Get the name of the Site
     * @return name
     */
    public String getName(){
        return name;
    }

    /**
     * Set the name of the site as given name
     * @param name New name value
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the slug of the Site
     * @return slug
     */
    public String getSlug() { return slug; }

    /**
     * Set the slug of the Site as the given slug
     * @param slug  New slug value
     */
    public void setSlug(String slug) { this.slug = slug; }
}
