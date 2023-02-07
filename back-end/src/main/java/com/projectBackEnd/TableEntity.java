package main.java.com.projectBackEnd;

import java.io.Serializable;

/**
 * Interface implemented by all database entity table classes
 */
public interface TableEntity<T extends TableEntity<T>> {

    Serializable getPrimaryKey();
    T copy(T newCopy);

}
