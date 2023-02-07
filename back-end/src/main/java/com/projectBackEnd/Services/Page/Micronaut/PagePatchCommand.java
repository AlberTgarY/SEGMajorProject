package main.java.com.projectBackEnd.Services.Page.Micronaut;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import io.micronaut.core.annotation.Introspected;


/**
 * PagePatchCommand is used by its controller to patch a page object in the database
 * It creates mock Page objects to reduce memory use.
 */
@Introspected
public class PagePatchCommand {

    @NotNull
    private int id;

    @NotBlank
    private String slug;

    @NotNull
    private int index;


    /**
     * Default constructor
     */
    public PagePatchCommand() {}


    /**
     * Main constructor
     * @param id    Primary key 'ID' of the Page
     * @param slug  Slug of the Page
     * @param index Index of the Page
     */
    public PagePatchCommand(int id, String slug, int index) {

        this.id = id;
        this.slug = slug;
        this.index = index;

    }


    /**
     * Set the ID of that page as the input ID
     * @param id    Primary key 'ID'
     */
    public void setId(int id) {
        this.id = id;
    }


    /**
     * Get primary key ID for this object
     * @return primary key
     */
    public int getId() {
        return id;
    }


    /**
     * Set the index of the Page as the input index value
     * @param index New index value
     */
    public void setIndex(int index) {
        this.index = index;
    }


    /**
     * Get the index of the Page
     * @return index
     */
    public Integer getIndex() {
        return index;
    }


    /**
     * Set the slug of the Page to the input slug
     * @param slug New slug value
     */
    public void setSlug(String slug) {
        this.slug = slug;
    }


    /**
     * Get the slug of the Page
     * @return slug value
     */
    public String getSlug() {
        return slug;
    }


}