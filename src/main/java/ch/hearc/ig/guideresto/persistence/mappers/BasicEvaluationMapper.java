package ch.hearc.ig.guideresto.persistence.mappers;

import ch.hearc.ig.guideresto.business.BasicEvaluation;
import ch.hearc.ig.guideresto.business.Restaurant;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BasicEvaluationMapper extends AbstractMapper<BasicEvaluation> {

    @Override
    protected String tableName() {
        return "LIKES";
    }

    @Override
    protected BasicEvaluation mapRow(ResultSet rs) throws SQLException {
        BasicEvaluation b = new BasicEvaluation();
        b.setId(rs.getInt("numero"));

        // 'T' / 'F' -> Boolean
        String app = rs.getString("appreciation");
        b.setLikeRestaurant(app != null && app.equalsIgnoreCase("T"));

        Date sqlDate = rs.getDate("date_eval");
        b.setVisitDate(sqlDate != null ? new java.util.Date(sqlDate.getTime()) : null);

        // ip & restaurant
        b.setIpAddress(rs.getString("adresse_ip"));
        Restaurant r = new Restaurant();
        r.setId(rs.getInt("fk_rest"));
        b.setRestaurant(r);

        return b;
    }

    public List<BasicEvaluation> findByRestaurant(int restaurantId) throws SQLException {
        String sql = "SELECT numero, appreciation, date_eval, adresse_ip, fk_rest " +
                     "FROM LIKES WHERE fk_rest=? ORDER BY date_eval DESC";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            ps.setInt(1, restaurantId);
            try (ResultSet rs = ps.executeQuery()) {
                List<BasicEvaluation> out = new ArrayList<>();
                while (rs.next()) {
                    out.add(mapRow(rs));
                }
                return out;
            }
        }
    }

    public BasicEvaluation insert(BasicEvaluation b) throws SQLException {
        String sql = "INSERT INTO LIKES(appreciation, date_eval, adresse_ip, fk_rest) VALUES(?,?,?,?)";
        try (PreparedStatement ps = connection().prepareStatement(sql, new String[] {"NUMERO"})) {
            ps.setString(1, Boolean.TRUE.equals(b.getLikeRestaurant()) ? "T" : "F");
            java.util.Date d = b.getVisitDate();
            ps.setDate(2, d != null ? new Date(d.getTime()) : null);
            ps.setString(3, b.getIpAddress());
            if (b.getRestaurant() == null || b.getRestaurant().getId() == null) {
                throw new SQLException("Restaurant id is required for LIKE");
            }
            ps.setInt(4, b.getRestaurant().getId());

            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) {
                if (gk.next()) b.setId(gk.getInt(1));
            }
            return b;
        }
    }

    public void delete(int id) throws SQLException {
        try (PreparedStatement ps = connection().prepareStatement("DELETE FROM LIKES WHERE numero=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
