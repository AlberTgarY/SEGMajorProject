package test.java;
/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.hibernate.dialect.Dialect;

import org.hibernate.testing.jdbc.JdbcProperties;
import org.hibernate.testing.jdbc.leak.H2IdleConnectionCounter;
import org.hibernate.testing.jdbc.leak.IdleConnectionCounter;
import org.hibernate.testing.jdbc.leak.*;
/**
 * https://github.com/hibernate/hibernate-orm/tree/master/hibernate-testing/src/main/java/org/hibernate/testing/jdbc/leak
 *
 * Class which uses hibernate.properties to latch onto a database and check after each test whether there were any leaks
 * to the databaste - not our class
 * Only used in testing.
 * Original Author:
 * @author Vlad Mihalcea
 *
 */
class ConnectionLeakUtil {

    private final JdbcProperties jdbcProperties = JdbcProperties.INSTANCE;

    private List<IdleConnectionCounter> idleConnectionCounters = Arrays.asList(
            H2IdleConnectionCounter.INSTANCE,
            OracleIdleConnectionCounter.INSTANCE,
            PostgreSQLIdleConnectionCounter.INSTANCE,
            MySQLIdleConnectionCounter.INSTANCE
    );

    private IdleConnectionCounter connectionCounter;

    private int connectionLeakCount;

    /**
     * Constructor for Util to count different kinds of leaks
     */
    ConnectionLeakUtil() {
        for ( IdleConnectionCounter connectionCounter : idleConnectionCounters ) {
            if ( connectionCounter.appliesTo( Dialect.getDialect().getClass() ) ) {
                this.connectionCounter = connectionCounter;
                break;
            }
        }
        if ( connectionCounter != null ) {
            connectionLeakCount = countConnectionLeaks();
        }
    }

    /**
     * Checks the number of leaks before and after
     */
    void assertNoLeaks() {
        if ( connectionCounter != null ) {
            int currentConnectionLeakCount = countConnectionLeaks();
            int diff = currentConnectionLeakCount - connectionLeakCount;
            if ( diff > 0 ) {
                throw new ConnectionLeakException( String.format(
                        "%d connection(s) have been leaked! Previous leak count: %d, Current leak count: %d",
                        diff,
                        connectionLeakCount,
                        currentConnectionLeakCount
                ) );
            }
        }
    }

    /**
     * Counts connection leaks
     * @return The number of leaks
     */
    private int countConnectionLeaks() {
        try ( Connection connection = newConnection() ) {
            return connectionCounter.count( connection );
        }
        catch ( SQLException e ) {
            throw new IllegalStateException( e );
        }
    }

    /**
     * Obtain a new JDBC Connection.
     *
     * @return JDBC Connection
     */
    private Connection newConnection() {
        try {
            return DriverManager.getConnection(
                    jdbcProperties.getUrl(),
                    jdbcProperties.getUser(),
                    jdbcProperties.getPassword()
            );
        }
        catch ( SQLException e ) {
            throw new IllegalStateException( e );
        }
    }
}
