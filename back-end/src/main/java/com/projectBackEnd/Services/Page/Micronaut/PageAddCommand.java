package main.java.com.projectBackEnd.Services.Page.Micronaut;

import javax.validation.constraints.NotNull;
import main.java.com.projectBackEnd.Services.Site.Hibernate.SiteManager;
import main.java.com.projectBackEnd.Services.Site.Hibernate.SiteManagerInterface;
import io.micronaut.core.annotation.Introspected;


/**
 * PageAddCommand is used by the controller to insert a new page into the database.
 * It creates mock Page objects to reduce memory use.
 */

@Introspected
public class PageAddCommand {

    private String site;

    @NotNull
    private String slug;

    @NotNull
    private String index;

    private String title;

    private String content;


    /**
     * Default constructor
     */
    public PageAddCommand() {}


    /**
     * Main constructor
     * @param site      Parent site to which the page belongs
     * @param slug      Slug of the page
     * @param index     Index of the page on its parent site
     * @param title     Title of the page
     * @param content   Content of the page
     */
    public PageAddCommand(String site, String slug, Integer index, String title, String content) {

        setSite(site);
        this.slug = slug;
        setIndex(index);
        this.title = title;
        this.content = content;

    }


    /**
     * Get Site to which the Page belongs to
     * @return site
     */
    public String getSite() {
        return site;
    }

    /**
     * Set Site to which the Page belongs to input Site
     * @param site Parent site of the Page
     */
    public void setSite(String site) {
        SiteManagerInterface siteManager = SiteManager.getSiteManager();
        if (siteManager.getSiteBySlug(site) != null) this.site = site;
    }
    /**
     * Get the slug of the Page
     * @return slug value
     */
    public String getSlug() {
        return slug;
    }

    /**
     * Set the slug of the Page to the input slug
     * @param slug New slug value
     */
    public void setSlug(String slug) {
        this.slug = slug;
    }

    /**
     * Get the title of the page
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the title of the page as the input title value
     * @param title New title value
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get content of the Page
     * @return content
     */
    public String getContent() {
        return content;
    }

    /**
     * Set content of the Page as the input content value
     * @param content New content value
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Get the index of the Page
     * @return index
     */
    public Integer getIndex() {
        try {
            return Integer.parseInt(index);
        } catch (NumberFormatException n) {
            return null;
        }
    }

    /**
     * Set the index of the Page as the input index value
     * @param index New index value
     */
    public void setIndex(Integer index) {
        this.index = index.toString();

    }

}