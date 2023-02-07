package main.java.com.projectBackEnd.Services.Site.Hibernate;

import main.java.com.projectBackEnd.DuplicateKeysException;
import main.java.com.projectBackEnd.InvalidFieldsException;

import java.io.Serializable;
import java.util.List;

/**
 *  Methods used by SiteManager for database queries with the 'Site' table
 */
public interface SiteManagerInterface {

    Site addSite(Site newSite) throws DuplicateKeysException, InvalidFieldsException;

    Site update(Site updatedVersion) throws DuplicateKeysException, InvalidFieldsException;

    Site getByPrimaryKey(Integer pk);

    Site getSiteBySlug(String slug);

    List<Site> getAllSites();

    void delete(Serializable primaryKey);

    void deleteAll();

}
