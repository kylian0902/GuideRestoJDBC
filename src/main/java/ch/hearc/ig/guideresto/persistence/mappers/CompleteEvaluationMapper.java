package ch.hearc.ig.guideresto.persistence.mappers;

import ch.hearc.ig.guideresto.business.CompleteEvaluation;
import ch.hearc.ig.guideresto.business.Grade;
import ch.hearc.ig.guideresto.business.Restaurant;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class CompleteEvaluationMapper extends AbstractMapper<CompleteEvaluation> {

    private final GradeMapper gradeMapper = new GradeMapper();

    @Override protected String tableName() { return "COMMENTAIRES"; }

    private CompleteEvaluation build(ResultSet rs) throws SQLException {
        CompleteEvaluation ev = new CompleteEvaluation();
        ev.setId(rs.getInt("numero"));
        Date d = rs.getDate("date_eval");
        ev.setVisitDate(d != null ? new java.util.Date(d.getTime()) : null);
        ev.setComment(rs.getString("commentaire"));
        ev.setUsername(rs.getString("nom_utilisateur"));

        Restaurant r = new Restaurant();
        r.setId(rs.getInt("fk_rest"));
        ev.setRestaurant(r);

        ev.setGrades(new HashSet<>());
        return ev;
    }

    private CompleteEvaluation addToCache(ResultSet rs) throws SQLException {
        int id = rs.getInt("numero");
        Optional<CompleteEvaluation> cached = fromCache(id);
        if (cached.isPresent()) return cached.get();
        CompleteEvaluation ev = build(rs);
        putCache(id, ev);
        return ev;
    }

    @Override
    protected CompleteEvaluation mapRow(ResultSet rs) throws SQLException { return addToCache(rs); }

    public List<CompleteEvaluation> findByRestaurant(int restaurantId) throws SQLException {
        String sql = """
            SELECT numero, date_eval, commentaire, nom_utilisateur, fk_rest
            FROM COMMENTAIRES
            WHERE fk_rest=? ORDER BY date_eval DESC
        """;
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            ps.setInt(1, restaurantId);
            try (ResultSet rs = ps.executeQuery()) {
                List<CompleteEvaluation> out = new ArrayList<>();
                while (rs.next()) out.add(addToCache(rs));
                return out;
            }
        }
    }

    /** Insert transactionnel : COMMENTAIRES + NOTES */
    public CompleteEvaluation insertWithGrades(CompleteEvaluation ev) throws SQLException {
        try {
            connection().setAutoCommit(false);

            String sql = "INSERT INTO COMMENTAIRES(date_eval, commentaire, nom_utilisateur, fk_rest) VALUES(?,?,?,?)";
            try (PreparedStatement ps = connection().prepareStatement(sql, new String[]{"NUMERO"})) {
                java.util.Date d = ev.getVisitDate();
                ps.setDate(1, d != null ? new Date(d.getTime()) : null);
                ps.setString(2, ev.getComment());
                ps.setString(3, ev.getUsername());
                ps.setInt(4, ev.getRestaurant().getId());
                ps.executeUpdate();
                try (ResultSet gk = ps.getGeneratedKeys()) { if (gk.next()) ev.setId(gk.getInt(1)); }
            }

            if (ev.getGrades() != null && !ev.getGrades().isEmpty()) {
                gradeMapper.insertAllForEvaluation(ev.getId(), new ArrayList<Grade>(ev.getGrades()));
            }

            connection().commit();
            putCache(ev.getId(), ev); // alimente le cache après succès
            return ev;

        } catch (SQLException e) {
            connection().rollback();
            throw e;
        } finally {
            connection().setAutoCommit(true);
        }
    }

    public void delete(int id) throws SQLException {
        try (PreparedStatement ps = connection().prepareStatement("DELETE FROM COMMENTAIRES WHERE numero=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
        removeCache(id);
    }
}