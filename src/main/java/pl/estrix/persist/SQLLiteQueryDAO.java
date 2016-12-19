package pl.estrix.persist;

import org.apache.log4j.Logger;
import pl.estrix.model.SQLQuery;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLLiteQueryDAO {


    private static Logger LOG = Logger.getLogger(SQLLiteQueryDAO.class);


    public static List<SQLQuery> getAll(Connection conn) {
        List<SQLQuery> items = new ArrayList<>();

        try {
            Statement stat = conn.createStatement();
            ResultSet result = stat.executeQuery("SELECT * FROM sql_query order by id asc");

            while(result.next()) {
                items.add(getItem(result));
            }
        } catch (SQLException e) {
            LOG.error("[SQLQuery][getAll]" , e);
            return null;
        }
        return items;
    }
    public static SQLQuery getById(Connection conn, Integer id) {
        List<SQLQuery> templates = new ArrayList<>();

        try {
            PreparedStatement ps = null;
            ResultSet result = null;
            ps = conn.prepareStatement("SELECT * FROM sql_query WHERE id = ? order by id asc limit 1");
            ps.setInt(1,id);

            result = ps.executeQuery();
            while(result.next()) {
                templates.add(getItem(result));
            }
            if (templates.size() > 0)
                return templates.get(0);
        } catch (SQLException e) {
            LOG.error("[SQLQuery][get]" , e);
            return null;
        }
        return null;
    }


    private static SQLQuery getItem(ResultSet rs) {
        LOG.debug(rs + " <<");
        if (rs == null) {
            return null;
        }
        SQLQuery item = new SQLQuery();
        try {
            item.setId(rs.getInt("id"));
            item.setName(rs.getString("name"));
            item.setContent(rs.getString("content"));

            return item;
        } catch (SQLException e) {
            LOG.error("[SQLQuery][get]", e);
            return null;
        }
    }

    public static long save(Connection conn, SQLQuery query) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            if (query.getId() > 0) {
                ps = conn.prepareStatement("UPDATE sql_query SET " +
                                                /*01*/"name=?, " +
                                                /*02*/"content=? " +
                                                /*03*/"WHERE id = ?");
                ps.setInt(3, query.getId());
            } else {
                ps = conn.prepareStatement("INSERT INTO sql_query (" +
	                                                /*01*/"name, " +
	                                                /*02*/"content " +
                        " ) VALUES"
                        + "(?,?" +
                        "   )",PreparedStatement.RETURN_GENERATED_KEYS);
            }

            ps.setString(1, query.getName() );
            ps.setString(2, query.getContent());

            ps.executeUpdate();


            if (query.getId() > 0) {
                return query.getId();
            } else {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    query.setId(rs.getInt(1));
                    return query.getId();
                } else {
                    return -1;
                }
            }
        } catch (SQLException e) {
            LOG.error("[SQLQuery][save]", e);
            return -1;
        } finally {

        }
    }

    public static boolean delete(Connection conn, SQLQuery query) {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("DELETE FROM sql_query WHERE id = ?");
            ps.setLong(1, query.getId());
            int up = ps.executeUpdate();
            return up==1;
        } catch (SQLException e) {
            LOG.error("[SQLQuery][delete]", e);
            return false;
        }
    }
}
