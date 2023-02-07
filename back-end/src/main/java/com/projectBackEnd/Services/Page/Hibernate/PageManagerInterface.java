package main.java.com.projectBackEnd.Services.Page.Hibernate;

import main.java.com.projectBackEnd.DuplicateKeysException;
import main.java.com.projectBackEnd.InvalidFieldsException;

import java.io.Serializable;
import java.util.List;

/**
 *  Methods used by PageManager for database queries with the Page table.
 */

public interface PageManagerInterface {

    Page addPage(Page newPage) throws DuplicateKeysException, InvalidFieldsException;

    Page update(Page updatedVersion) throws DuplicateKeysException, InvalidFieldsException ;

    Page getByPrimaryKey(Integer pk);

    List<Page> getAllPagesOfSite(String siteSlug);

    Page getPageBySiteAndSlug(String siteSlug, String slug);

    List<Page> getAllPages();

    void delete(Serializable primaryKey);

    void deleteAll();

}