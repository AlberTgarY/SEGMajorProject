package main.java.com.projectBackEnd.Services.Page.Micronaut;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;

import main.java.com.projectBackEnd.DuplicateKeysException;
import main.java.com.projectBackEnd.InvalidFieldsException;
import main.java.com.projectBackEnd.Services.Page.Hibernate.Page;
import main.java.com.projectBackEnd.Services.Page.Hibernate.PageManager;
import main.java.com.projectBackEnd.Services.Page.Hibernate.PageManagerInterface;
import main.java.com.projectBackEnd.Services.Session.SessionManager;
import main.java.com.projectBackEnd.Services.Session.SessionManagerInterface;

import java.net.URI;
import java.util.List;

import static main.java.com.projectBackEnd.URLLocation.location;


/**
 * Page Controller is a REST API endpoint.
 * It deals with the interactions between the server and the Page table in the database.
 * It provides HTTP requests for each of the queries that need to be made to add, remove, update and retrieve
 * pages from the database.
 */
@Controller("/sites")
public class PageController {

    private final PageManagerInterface pageManager = PageManager.getPageManager();

    private final SessionManagerInterface sessionManager = SessionManager.getSessionManager();



    /**
     * Insert a new page into the database using PageAddCommand methods via an HTTP Post request
     * @param session   Current session
     * @param name      Name of the page
     * @param pageToAdd Dedicated PageAddCommand class to add new page to the database
     * @return HTTP response with relevant information resulting from the insertion of the page
     */
    @Post("/{name}/pages")
    public HttpResponse addPage(@Header("X-API-Key") String session, String name, @Body PageAddCommand pageToAdd) {

        if(!sessionManager.verifySession(session)) return HttpResponse.unauthorized();
        Page page;
        try {
        page = pageManager.addPage(new Page(pageToAdd.getSite(), pageToAdd.getSlug(),
                pageToAdd.getIndex(), pageToAdd.getTitle(), pageToAdd.getContent()));
        } catch (DuplicateKeysException | InvalidFieldsException e) {
            return HttpResponse.badRequest(e.getMessage());
        }


        return HttpResponse
                .created(page)
                .headers(headers -> headers.location(pageLocation(name, page.getSlug())));
    }

    /**
     * Update a page with PageUpdateCommand methods via an HTTP Put request
     * @param session               Current session
     * @param name                  Name of the Page
     * @param updatedPageCommand    Dedicated PageUpdateCommand for updating the page
     * @return HTTP response resulting from the Put request with path
     */
    @Put("{name}/pages/")
    public HttpResponse updatePage(@Header("X-API-Key") String session, String name, @Body PageUpdateCommand updatedPageCommand) {

        if(!sessionManager.verifySession(session)) return HttpResponse.unauthorized();
        Page updatedPage = new Page(updatedPageCommand.getPrimaryKey(), updatedPageCommand.getSite(), updatedPageCommand.getSlug(),
                updatedPageCommand.getIndex(), updatedPageCommand.getTitle(), updatedPageCommand.getContent());
        try {
            pageManager.update(updatedPage);
        } catch (DuplicateKeysException|InvalidFieldsException e) {
            return HttpResponse.badRequest(e.getMessage());
        }

        return HttpResponse
                .noContent()
                .header(HttpHeaders.LOCATION, pageLocation(name, updatedPage.getSlug()).getPath());
    }


    /**
     * Get the specific Page corresponding to the given site name and page name via an HTTP Get request
     * @param name  Site name
     * @param page  Page name
     * @return      Page found
     */
    @Get("/{name}/pages/{page}")
    public Page getPage(String name, String page) {
        return pageManager.getPageBySiteAndSlug(name, page);
    }


    /**
     * Get a list of all the pages under the same site via an HTTP GET request
     * @param name  Name of the parent site
     * @return      List of all pages belonging to specified site
     */
    @Get("/{name}/pages")
    public List<Page> getAllPages(String name) {
        return pageManager.getAllPagesOfSite(name);
    }


    /**
     * Update multiple pages via an HTTP Patch request
     * @param session           Current session
     * @param name              Name of the parent site
     * @param patchCommandList  List of patch command
     * @return HTTP response with no content
     */
    @Patch("/{name}/page-indices")
    public HttpResponse patchPage(@Header("X-API-Key") String session, String name, @Body List<PagePatchCommand> patchCommandList) {

        if(!sessionManager.verifySession(session)) return HttpResponse.unauthorized();

        for (PagePatchCommand p : patchCommandList) {
            String slug = p.getSlug();
            Page page = pageManager.getPageBySiteAndSlug(name, slug);
            page.setIndex(p.getIndex());
            try {
                pageManager.update(page);
            } catch (DuplicateKeysException|InvalidFieldsException e) {
                return HttpResponse.badRequest(e.getMessage());
            }
        }

        return HttpResponse.noContent();
    }


    /**
     * Remove the Page corresponding to the given Page name and Site name from the database via an HTTP Delete request
     * @param session   Current session
     * @param name      Name of the parent site
     * @param page      Name of the page to remove
     * @return Http response with relevant information resulting from the deletion of the page
     */
    @Delete("/{name}/pages/{page}")
    public HttpResponse deletePage(@Header("X-API-Key") String session, String name, String page) {

        if(!sessionManager.verifySession(session)) return HttpResponse.unauthorized();
        Page p = pageManager.getPageBySiteAndSlug(name, page);
        pageManager.delete(p.getPrimaryKey());

        return HttpResponse.noContent();
    }


    /**
     * Get the URI of a specific page
     * @param siteName  Name of the site
     * @param pageName  Name of the page to locate
     * @return URI of the page
     */
    private URI pageLocation(String siteName, String pageName) {
        return URI.create(location(siteName, "/sites/").toString() + location(pageName, "/pages/").toString());
    }

}