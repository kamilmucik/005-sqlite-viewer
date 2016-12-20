package pl.estrix.persist;


import org.apache.log4j.Logger;

import javax.swing.text.html.HTMLDocument;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLiteTargetDAO {

    private static Logger LOG = Logger.getLogger(SQLLiteQueryDAO.class);


    public static List<Map<String, Object>> executeQuery(Connection conn, String query) {
        Statement stat = null;
        StringBuffer resultQuery = new StringBuffer();
        List<Map<String, Object>> resultList = new ArrayList<>();
        Map<String, Object> row = null;
        try {
            stat = conn.createStatement();
            ResultSet result = stat.executeQuery(query);
            ResultSetMetaData resultSetMetaData = result.getMetaData();
            int columnCount = resultSetMetaData.getColumnCount();
            while (result.next()) {
                row = new HashMap<String, Object>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(resultSetMetaData.getColumnName(i), result.getObject(i));
                }
                resultList.add(row);
            }

            return resultList;
        } catch (SQLException e) {
            LOG.error("[SQLQuery][getAll]" , e);
            return null;
        } finally {
            if (stat != null) {
                try {
                    stat.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
