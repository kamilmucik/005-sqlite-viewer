package pl.estrix.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;


/**
 * http://www.tutorialspoint.com/sqlite/sqlite_java.htm
 * http://javastart.pl/static/zaawansowane-programowanie/bazy-danych-sqlite-w-javie/
 * http://www.javaxt.com/javaxt-core/sql/ConnectionPool
 * http://stackoverflow.com/questions/22303796/set-sqlite-connection-properties-in-c3p0-connection-pool
 * @author Kamil Mucik
 *
 */
public enum SQLiteUtil {

    INSTANCE;

    private Logger LOG = Logger.getLogger(SQLiteUtil.class);

    public static final String DB_NAME = ".baza.db";
    private static final String DRIVER = "org.sqlite.JDBC";
    private static final String DB_URL = "jdbc:sqlite:.baza.db";

    private Connection conn;
    private Statement stat;

    private DataSource pooled ;

    SQLiteUtil() {
        try {
            Class.forName(SQLiteUtil.DRIVER);
        } catch (ClassNotFoundException e) {
            LOG.error("Brak sterownika JDBC", e);
        }

        try {
            //Create the ConnectionPoolDataSource
            SQLiteConnectionPoolDataSource dataSource = new SQLiteConnectionPoolDataSource();
            dataSource.setUrl(DB_URL);

        } catch (Exception e) {
            LOG.error("Problem z otwarciem polaczenia" , e);
        }

    }


    public Connection getConnection() throws SQLException{

        Connection con = null;

        try {
            Class.forName(SQLiteUtil.DRIVER);
        } catch (ClassNotFoundException e) {
            return null;
        }

        try {
            con = DriverManager.getConnection(DB_URL);


            SQLiteConnectionPoolDataSource dataSource = new SQLiteConnectionPoolDataSource();
            dataSource.setUrl(DB_URL);

            //Pass in some additional config options (optional)
            org.sqlite.SQLiteConfig config = new org.sqlite.SQLiteConfig();
            config.enforceForeignKeys(true);
            config.enableLoadExtension(true);
            dataSource.setConfig(config);
            //Instantiate the Connection Pool Manager
            MiniConnectionPoolManager poolMgr = new MiniConnectionPoolManager(dataSource, 40);


            return poolMgr.getConnection();

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

    }

    public void closeConnection() {
        try {
            conn.close();
        } catch (SQLException e) {
            LOG.error("Problem z zamknieciem polaczenia");
            e.printStackTrace();
        }
    }



}
