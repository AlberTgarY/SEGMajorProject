package main.java.com.projectBackEnd;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.io.Serializable;
import java.util.List;

/**
* EntityManager is the super class of managers, all responsible for database queries for their respective
* database entity.
*
* The subclass parameter defines which database table the manager is currently interacting with.
* This ensures that one object only is used to coordinate actions and queries across the system
* in accordance with the Singleton design pattern.
*
* EntityManager deals with the opening and the closing of the hibernate session when processing queries.
*
* @param <T> TableEntity class type this manager will represent (required for working, defined by inheriting classes)
*/
public abstract class EntityManager <T extends TableEntity> {

    private Class<T> subclass;



    /** Set input Entity class as current subclass of EntityManager for database interactions
     * @param subclass  Entity class currently used by the manager
     */
    protected void setSubclass(Class<T> subclass) {
        this.subclass = subclass;
    }


    /**
     * Open the session and insert a new object into the subclass entity table
     * @param newObject Entity object to be inserted
     * @return Entity object added to database
     */
    public T insertTuple(T newObject) {

        SessionFactory sf = HibernateUtility.getSessionFactory();
        Session session = sf.openSession();
        try {
            insertTuple(newObject, session);
        } catch(HibernateException ex) {
            if (session.getTransaction() != null) session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return newObject;

    }


    /**
     * Save a new object into the database
     * @param newObject Entity object to be inserted
     * @param session   Current session
     * @throws HibernateException If the tuple cannot be inserted
     */
    private void insertTuple(T newObject, Session session) throws HibernateException {

        session.beginTransaction();
        session.save(newObject);
        session.getTransaction().commit();

    }

    /**
     * Open Session and update input object from subclass entity table in database
     * @param updatedCopy   Entity object with updated attributes
     * @return updated object
     */
    public T update(T updatedCopy) {

        SessionFactory sf = HibernateUtility.getSessionFactory(); //Gets sf
        Session session = sf.openSession();
        T fromDatabase = null;
        try {
            fromDatabase = update(updatedCopy, session);
        } catch(HibernateException ex) {
            if (session.getTransaction() != null) session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return fromDatabase;

    }


    /**
     * Look for object to update in database and replace it with updatedCopy or insert it if not found
     * @param updatedCopy   Entity object with updated attributes
     * @param session       Current session
     * @return updated object
     * @throws HibernateException If the tuple cannot be updated
     */
    private T update(T updatedCopy, Session session) throws HibernateException {

        session.beginTransaction();
        T fromDatabase = (T) session.load(updatedCopy.getClass(), updatedCopy.getPrimaryKey());
        if (fromDatabase != null) fromDatabase.copy(updatedCopy);
        else insertTuple(updatedCopy);
        session.getTransaction().commit();
        return fromDatabase;

    }


    /**
     * Open the session and look for the object corresponding to input primary key in database
     * @param pk Object primary key
     * @return Entity object corresponding to primary key in database (or null if not found)
     */
    public T getByPrimaryKey(Serializable pk) {

        SessionFactory sf = HibernateUtility.getSessionFactory();
        Session session = sf.openSession();
        T found = null;
        try {
            found = getByPrimaryKey(pk, session);
        } catch(HibernateException ex) {
            if (session.getTransaction() != null) session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return found;
    }


    /**
     * Query database and find object corresponding to input primary key on given session
     * @param pk        Object primary key
     * @param session   Current session
     * @return Entity object corresponding to primary key in database (or null if not found)
     * @throws HibernateException If the object cannot be obtained
     */
    private T getByPrimaryKey(Serializable pk, Session session) throws HibernateException {

        session.beginTransaction();
        T found = session.get(subclass, pk);
        session.getTransaction().commit();
        return found;

    }

    /**
     * Open the session to get all the objects of Entity type T from the database
     * Sources : https://stackoverflow.com/questions/43037814/how-to-get-all-data-in-the-table-with-hibernate/43067399
     * @return List of all objects of type T found in database
     */
    public List<T> getAll() {
        SessionFactory sf = HibernateUtility.getSessionFactory();
        Session session = sf.openSession();
        List<T> results = null;
        try {
            results = getAll(session);
        } catch(HibernateException ex) {
            if (session.getTransaction() != null) session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return results;
    }


    /**
     * Query the database on given session to find all objects of subclass T that it stores
     * @param session   Current session
     * @return List of all objects of type T stored in database
     * @throws HibernateException If they cannot be obtained
     */
    private List<T> getAll(Session session) throws HibernateException  {

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T>  criteria = builder.createQuery(subclass);
        criteria.from(subclass);
        return session.createQuery(criteria).getResultList();

    }

    /**
     * Open Session and delete input object from subclass entity table in database
     * @param object    Entity object to remove from database
     */
    public void delete(T object) {

        SessionFactory sf = HibernateUtility.getSessionFactory();
        Session session = sf.openSession();
        try {
            delete(object, session);
        } catch(HibernateException ex) {
            if (session.getTransaction() != null) session.getTransaction().rollback();
        } finally {
            session.close();
        }

    }


    /**
     * Find input Entity object in database and delete it
     * @param object    Entity object to remove from database
     * @param session   Current session
     * @throws HibernateException If the object cannot be deleted
     */
    private void delete(T object, Session session) throws HibernateException {

        session.beginTransaction();
        T entityToDelete = getByPrimaryKey(object.getPrimaryKey());
        session.delete(entityToDelete);
        session.getTransaction().commit();

    }


    /**
     * Open Session and remove object associated to input primary key
     * @param pk    Primary key of entity object to delete
     */
    public void delete(Serializable pk) {

        SessionFactory sf = HibernateUtility.getSessionFactory();
        Session session = sf.openSession();
        try {
            delete(pk, session);
        } catch(HibernateException ex) {
            if (session.getTransaction() != null) session.getTransaction().rollback();
        } finally {
            session.close();
        }

    }


    /**
     * Find Entity object by its given primary key and remove it from database
     * @param pk        Primary key of Entity object to find and remove
     * @param session   Current session
     * @throws HibernateException If the object cannot be deleted
     */
    private void delete(Serializable pk, Session session) throws HibernateException {

        session.beginTransaction();
        T entityToDelete = getByPrimaryKey(pk);
        session.delete(entityToDelete);
        session.getTransaction().commit();

    }


    /**
     * Open Session to delete all objects of current subclass from database
     */
    public void deleteAll() {

        SessionFactory sf = HibernateUtility.getSessionFactory();
        Session session = sf.openSession();
        try {
            deleteAll(session);
        } catch(HibernateException ex) {
            if (session.getTransaction() != null) session.getTransaction().rollback();
        } finally {
            session.close();
        }

    }


    /**
     * Remove all objects from subclass currently stored in database
     * @param session   Current session
     * @throws HibernateException If the table's tuples cannot be deleted
     */
    private void deleteAll(Session session) throws  HibernateException {

        session.beginTransaction();
        for (Object tuple : getAll()) {
            //Deleting one by one is recommended to deal with cascading.
            session.delete(tuple);
        }
        session.getTransaction().commit();

    }


}