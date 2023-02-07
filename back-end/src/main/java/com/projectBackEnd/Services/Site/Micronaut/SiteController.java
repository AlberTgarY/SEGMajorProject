package main.java.com.projectBackEnd.Services.Site.Micronaut;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import main.java.com.projectBackEnd.DuplicateKeysException;
import main.java.com.projectBackEnd.InvalidFieldsException;
import main.java.com.projectBackEnd.Services.Session.SessionManager;
import main.java.com.projectBackEnd.Services.Session.SessionManagerInterface;
import main.java.com.projectBackEnd.Services.Site.Hibernate.Site;
import main.java.com.projectBackEnd.Services.Site.Hibernate.SiteManager;
import main.java.com.projectBackEnd.Services.Site.Hibernate.SiteManagerInterface;

import java.util.List;

import static main.java.com.projectBackEnd.URLLocation.location;

/**
 * Site Controller is a REST API endpoint.
 * It deals with the interactions between the server and the Site table stored in the database.
 * It provides HTTP requests for each of the queries that need to be made to add, remove, update and retrieve
 * information for sites in the database.
 */
@Controller("/sites")
public class SiteController {

    private final SiteManagerInterface siteManager = SiteManager.getSiteManager();
	private final SessionManagerInterface sessionManager = SessionManager.getSessionManager();

    /**
     * Get a list of all the sites stored in the database via an HTTP GET request
     * @return List of all the sites
     */
    @Get("/")
    public List<Site> getAll(){
        return siteManager.getAllSites();
    }

    /**
     * Insert a new site into the database with a SiteAddCommand and via an HTTP POST request
     * @param session   Current session
     * @param command   Dedicated SiteAddCommand class to add new site
     * @return HTTP response with drelevant information resulting from the insertion of the site
     */
    @Post("/")
    public HttpResponse add(@Header("X-API-Key") String session,@Body SiteAddCommand command) {

		if(!sessionManager.verifySession(session)) return HttpResponse.unauthorized();
		Site site;
		try {
            site = siteManager.addSite(new Site(command.getSlug(), command.getName()));
        } catch (DuplicateKeysException | InvalidFieldsException e) {
            return HttpResponse.badRequest(e.getMessage());
        }

        return HttpResponse
                .created(site)
                .headers(headers -> headers.location(location(site.getSlug(), "/sites/")));
    }

    /**
     * Update a site with SiteUpdateCommand methods via an HTTP Put request
     * @param session               Current session
     * @param updatedSiteCommand    Dedicated SiteUpdateCommand class to update site
     * @return TTP response resulting from the Put request with path
     */
    @Put("/")
    public HttpResponse update(@Header("X-API-Key") String session, @Body SiteUpdateCommand updatedSiteCommand) {

        if(!sessionManager.verifySession(session)) return HttpResponse.unauthorized();
        Site newSite = new Site(updatedSiteCommand.getPrimaryKey(), updatedSiteCommand.getSlug(), updatedSiteCommand.getName());
        try {
            siteManager.update(newSite);
        } catch (DuplicateKeysException | InvalidFieldsException e) {
            return HttpResponse.badRequest(e.getMessage());
        }

        return HttpResponse
                .noContent()
                .header(HttpHeaders.LOCATION, location(updatedSiteCommand.getSlug(), "/sites/").getPath());
    }


    /**
     * Get the specific Site corresponding to the given ID via an HTTP Get request
     * @param id    Primary key of the site to retrieve
     * @return Site with the specified ID
     */
    @Get(value = "/id/{id}", produces = MediaType.TEXT_JSON)
    public Site getByID(int id) {
        return siteManager.getByPrimaryKey(id);
    }

    /**
     * Get the specific Site corresponding to the given site slug via an HTTP Get request
     * @param slug  Slug of the site to retrieve
     * @return Site with the specified slug
     */
    @Get(value = "/{slug}")
    public Site getBySlug(String slug){return siteManager.getSiteBySlug(slug);}

    /**
     * Remove the Site corresponding to the given site slug from the database via an HTTP Delete request
     * @param session   Current Session
     * @param slug      Slug of the site to remove from database
     * @return Http response with relevant information resulting from the deletion of the Site
     */
    @Delete("/{slug}")
    public HttpResponse delete(@Header("X-API-Key") String session,String slug) {

		if(!sessionManager.verifySession(session)) return HttpResponse.unauthorized();
        siteManager.delete(siteManager.getSiteBySlug(slug).getPrimaryKey());

        return HttpResponse.noContent();
    }
}

