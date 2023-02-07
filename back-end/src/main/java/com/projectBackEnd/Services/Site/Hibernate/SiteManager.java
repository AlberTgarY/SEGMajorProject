package main.java.com.projectBackEnd.Services.Site.Hibernate;

import main.java.com.projectBackEnd.DuplicateKeysException;
import main.java.com.projectBackEnd.EntityManager;
import main.java.com.projectBackEnd.HibernateUtility;
import main.java.com.projectBackEnd.InvalidFieldsException;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.PersistenceException;
/**
 * SiteManager defines methods to interact with the Site table in the database.
 * This class extends the EntityManager - supplying it with the rest of its interface methods.
 *
 * Inspiration : https://examples.javacodegeeks.com/enterprise-java/hibernate/hibernate-annotations-example/
 */
public class SiteManager extends EntityManager implements SiteManagerInterface {

    private static SiteManagerInterface siteManager;

    /**
     * Private constructor (Singleton design pattern)
     */
    private SiteManager() {
        super();
        setSubclass(Site.class);
        HibernateUtility.addAnnotation(Site.class);
        siteManager = this;
    }
	
    /**
     * Get the site manager interface
     * @return siteManager ; if none has been defined, create a new SiteManager()
     */
    public static SiteManagerInterface getSiteManager() {
        if (siteManager==null) return new SiteManager();
        else return siteManager;
    }

    /**
     * Insert a Site object into the database
     * @param newSite Site to add to the database
     * @return added object
     * @throws DuplicateKeysException If addition of this object article will cause a duplicate slug present
     * @throws InvalidFieldsException If the object contains fields which cannot be added to the database e.g. nulls
     */
    public Site addSite(Site newSite) throws DuplicateKeysException, InvalidFieldsException {
        checkAddValidity(newSite);
        return (Site) super.insertTuple(newSite);
    }

    /**
     * Update the Site's attributes
     * @param updatedVersion Site with updated attributed
     * @return updated Site
     * @throws DuplicateKeysException If addition of this object article will cause a duplicate slug present
     * @throws InvalidFieldsException If the object contains fields which cannot be added to the database e.g. nulls
     */
    public Site update(Site updatedVersion) throws DuplicateKeysException, InvalidFieldsException {
        checkUpdateValidity(updatedVersion);
        return (Site) super.update(updatedVersion);
    }


    /**
     * Retrieve Site object corresponding to the given primary key from the database
     * @param pk    Primary key of the Site to find in the database
     * @return  found Site object
     */
    public Site getByPrimaryKey(Integer pk) {
        return (Site) super.getByPrimaryKey(pk);
    }
	
    /**
     * Retrieve Site object corresponding to the given site slug from the database
     * @param siteSlug  Slug of the site to find in the database
     * @return found Site object
     */	
    public Site getSiteBySlug(String siteSlug) {
        List<Site> matches = getAllSites().stream().filter(s -> s.getSlug().equals(siteSlug)).collect(Collectors.toList());
        if (matches.size() >= 1) return matches.get(0);
        else return null;
    }

    /**
     * Get a list of all Sites from the Site table in the database
     * @return List of all Sites in the database
     */
    public List<Site> getAllSites() {
        return (List<Site>) super.getAll();
    }

    /**
     * Checks if a given site has non null valid field attributes
     * @param site The site to be checked
     * @return Whether it is valid for addition or not.
     * @throws InvalidFieldsException if the site object isn't valid i.e. has null fields
     */
    private static void checkFieldValidity(Site site) throws InvalidFieldsException {
        if (!(site.getSlug() != null &&
                site.getName() != null)) throw new InvalidFieldsException("Invalid fields");
    }

    /**
     * Checks whether a site is valid for updating
     * @param site The site to be checked
     * @throws InvalidFieldsException If the site object has null fields
     * @throws DuplicateKeysException If the updating of this site will violate another site's unique keys
     */
    private void checkUpdateValidity(Site site) throws InvalidFieldsException, DuplicateKeysException {
        checkFieldValidity(site);
        Site foundSiteMatch = getSiteBySlug(site.getSlug());
        if (foundSiteMatch != null && !foundSiteMatch.getPrimaryKey().equals(site.getPrimaryKey()))
            throw new DuplicateKeysException("Site with slug: " + site.getSlug() + " already exists.");
    }

    /**
     * Checks whether a site is valid for addition
     * @param site The site to be checked
     * @throws InvalidFieldsException If the site object has null fields
     * @throws DuplicateKeysException If the addition of this site will violate another site's unique keys
     */
    private void checkAddValidity(Site site) throws InvalidFieldsException, DuplicateKeysException {
        checkFieldValidity(site);
        if (getSiteBySlug(site.getSlug()) != null)
            throw new DuplicateKeysException("Site with slug: " + site.getSlug() + " already exists.");
    }
}
