package pl.estrix.persist;

import java.sql.Connection;

import org.apache.log4j.Logger;

import pl.estrix.util.SQLiteUtil;

public class SQLiteBean {

    private static Logger LOG = Logger.getLogger(SQLiteBean.class);

    public static final boolean createTable(String query){
        try {
            Connection conn = null;
            try {
                conn = SQLiteUtil.INSTANCE.getConnection();

                if (conn == null) {
                } else {
                    return SQLiteDAO.createTable(conn, query);
                }
            } finally {
                if (conn != null) {
                    conn.close();
                }
            }
        } catch (Exception e) {
            LOG.error("SQLiteBean", e);
        }
        return false;
    }

}
