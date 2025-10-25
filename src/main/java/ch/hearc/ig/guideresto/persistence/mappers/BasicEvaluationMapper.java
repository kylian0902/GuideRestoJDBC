package ch.hearc.ig.guideresto.persistence.mappers;

import ch.hearc.ig.guideresto.business.BasicEvaluation;
import ch.hearc.ig.guideresto.business.Restaurant;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class BasicEvaluationMapper extends AbstractMapper<BasicEvaluation> {

    @Override protected String tableName() { return "LIKES"; }

    private BasicEvaluation build(ResultSet rs) throws SQLException {
        BasicEvaluation b = new BasicEvaluation();
        b.setId(rs.getInt("numero"));
        b.setLikeRestaurant("T".equalsIgnoreCase(rs.getString("appreciation")));
        Date d = rs.getDate("date_eval");
        b.setVisitDate(d != null ? new java.util.Date(d.getTime()) : null);
        b.setIpAddress(rs.getString("adresse_ip"));

        Restaurant r = new Restaurant();
        r.setId(rs.getInt("fk_rest"));
        b.setRestaurant(r);
        return b;
    }

    private BasicEvaluation addToCache(ResultSet rs) throws SQLException {
        int id = rs.getInt("numero");
        Optional<BasicEvaluation> cached = fromCache(id);
        if (cached.isPresent()) return cached.get();
        BasicEvaluation b = build(rs);
        putCache(id, b);
        return b;
    }

    @Override
    protected BasicEvaluation mapRow(ResultSet rs) throws SQLException { return addToCache(rs); }

    public List<BasicEvaluation> findByRestaurant(int restaurantId) throws SQLException {
        String sql = "SELECT numero, appreciation, date_eval, adresse_ip, fk_rest FROM LIKES WHERE fk_rest=? ORDER BY date_eval DESC";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            ps.setInt(1, restaurantId);
            try (ResultSet rs = ps.executeQuery()) {
                List<BasicEvaluation> out = new ArrayList<>();
                while (rs.next()) out.add(addToCache(rs));
                return out;
            }
        }
    }

    public BasicEvaluation insert(BasicEvaluation b) throws SQLException {
        String sql = "INSERT INTO LIKES(appreciation, date_eval, adresse_ip, fk_rest) VALUES(?,?,?,?)";
        try (PreparedStatement ps = connection().prepareStatement(sql, new String[]{"NUMERO"})) {
            ps.setString(1, Boolean.TRUE.equals(b.getLikeRestaurant()) ? "T" : "F");
            java.util.Date d = b.getVisitDate();
            ps.setDate(2, d != null ? new Date(d.getTime()) : null);
            ps.setString(3, b.getIpAddress());
            ps.setInt(4, b.getRestaurant().getId());
            ps.executeUpdate();

            try (ResultSet gk = ps.getGeneratedKeys()) { if (gk.next()) b.setId(gk.getInt(1)); }
            putCache(b.getId(), b);
            return b;
        }
    }

    public void delete(int id) throws SQLException {
        try (PreparedStatement ps = connection().prepareStatement("DELETE FROM LIKES WHERE numero=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
        removeCache(id);
    }
}