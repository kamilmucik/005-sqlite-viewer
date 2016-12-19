package pl.estrix.persist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class SQLiteDAO {

    private static Logger LOG = Logger.getLogger(SQLiteDAO.class);

    public static boolean createTable(Connection conn, String query) {
        String createTableSQLQuery = "CREATE TABLE IF NOT EXISTS sql_query (id INTEGER PRIMARY KEY AUTOINCREMENT, name varchar(50), content varchar(1024))";

        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(createTableSQLQuery);
            int up = ps.executeUpdate();
            if (up==0){
                return true;
            }
        } catch (SQLException e) {
            LOG.error("[SQLiteDAO][createTable]", e);
        }
        return false;
    }


}
