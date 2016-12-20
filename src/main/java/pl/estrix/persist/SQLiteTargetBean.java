package pl.estrix.persist;

import org.apache.log4j.Logger;
import pl.estrix.model.SQLQuery;
import pl.estrix.util.SQLiteUtil;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class SQLiteTargetBean {

    private static Logger LOG = Logger.getLogger(SQLLiteQueryBean.class);

    public static final List<Map<String, Object>> executeQuery(String query){
        if ("".equals(query)) {
            List<Map<String, Object>> l = new ArrayList<>();
            Map<String, Object> m = new HashMap();
            m.put("Błąd","Pole zapytania nie może być puste.");
            l.add(m);
            return l;
        }
        try {
            Connection conn = null;
            try {
                conn = SQLiteUtil.INSTANCE.getTargetConnection();

                if (conn == null) {
                    return null;
                } else {
                    return SQLiteTargetDAO.executeQuery(conn, query);
                }

            } finally {
                if (conn != null) {
                    conn.close();
                }
            }
        } catch (Exception e) {
            LOG.error("SQLiteTargetBean:executeQuery", e);
            return null;
        }
    }
}
