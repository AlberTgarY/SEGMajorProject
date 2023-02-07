package main.java.com.projectBackEnd.Services.News.Hibernate;

import main.java.com.projectBackEnd.DuplicateKeysException;
import main.java.com.projectBackEnd.InvalidFieldsException;

import java.util.List;


/**
 *  Methods used by NewsManager for database queries related to News objects
 */
public interface NewsManagerInterface {

    News addNews(News news) throws DuplicateKeysException, InvalidFieldsException;

    News update(News news) throws DuplicateKeysException, InvalidFieldsException ;

    News getByPrimaryKey(Integer id);

    News getNewsBySlug(String slug);

    List<News> getAllNews();

    void delete(Integer pk);

    void deleteAll();

}