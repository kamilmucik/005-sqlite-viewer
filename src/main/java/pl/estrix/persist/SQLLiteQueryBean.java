package pl.estrix.persist;

import org.apache.log4j.Logger;
import pl.estrix.model.SQLQuery;
import pl.estrix.util.SQLiteUtil;

import java.sql.Connection;
import java.util.List;

public class SQLLiteQueryBean {

    private static Logger LOG = Logger.getLogger(SQLLiteQueryBean.class);

    public static final SQLQuery getById(Integer id){
        try {
            Connection conn = null;
            try {
                conn = SQLiteUtil.INSTANCE.getConnection();

                if (conn == null) {
                    return null;
                } else {
                    return SQLLiteQueryDAO.getById(conn, id);
                }

            } finally {
                if (conn != null) {
                    conn.close();
                }
            }
        } catch (Exception e) {
            LOG.error("SQLLiteQueryBean:getById", e);
            return null;
        }
    }

    public static final List<SQLQuery> getAll(){
        try {
            Connection conn = null;
            try {
                conn = SQLiteUtil.INSTANCE.getConnection();
                if (conn == null) {
                    return null;
                } else {

                    return SQLLiteQueryDAO.getAll(conn);
                }
            } finally {
                if (conn != null) {
                    conn.close();
                }
            }
        } catch (Exception e) {
            LOG.error("SQLLiteQueryBean:getAll", e);
            return null;
        }
    }

    public static final long save(SQLQuery query){
        try {
            Connection conn = null;
            try {
                conn = SQLiteUtil.INSTANCE.getConnection();

                if (conn == null) {
                    return 0;
                } else {
                    return SQLLiteQueryDAO.save(conn, query);
                }

            } finally {
                if (conn != null) {
                    conn.close();
                }
            }
        } catch (Exception e) {
            LOG.error("SQLLiteQueryBean:save", e);
            return 0;
        }
    }

    public static final boolean delete(SQLQuery query){
        try {
            Connection conn = null;
            try {
                conn = SQLiteUtil.INSTANCE.getConnection();

                if (conn == null) {
                    return false;
                } else {
                    return SQLLiteQueryDAO.delete(conn, query);
                }

            } finally {
                if (conn != null) {
                    conn.close();
                }
            }
        } catch (Exception e) {
            LOG.error("SQLLiteQueryBean:delete", e);
            return false;
        }
    }
}
