package ch.hearc.ig.guideresto.persistence.mappers;

import ch.hearc.ig.guideresto.business.CompleteEvaluation;
import ch.hearc.ig.guideresto.business.Grade;
import ch.hearc.ig.guideresto.business.Restaurant;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CompleteEvaluationMapper extends AbstractMapper<CompleteEvaluation> {

    private final GradeMapper gradeMapper = new GradeMapper();

    @Override
    protected String tableName() {
        return "COMMENTAIRES";
    }

    @Override
    protected CompleteEvaluation mapRow(ResultSet rs) throws SQLException {
        CompleteEvaluation ev = new CompleteEvaluation();
        ev.setId(rs.getInt("numero"));

        Date sqlDate = rs.getDate("date_eval");
        ev.setVisitDate(sqlDate != null ? new java.util.Date(sqlDate.getTime()) : null);

        ev.setComment(rs.getString("commentaire"));
        ev.setUsername(rs.getString("nom_utilisateur"));

        Restaurant r = new Restaurant();
        r.setId(rs.getInt("fk_rest"));
        ev.setRestaurant(r);

        // ne charge pas les grades ici (lazy)
        ev.setGrades(new HashSet<>());

        return ev;
    }

    public List<CompleteEvaluation> findByRestaurant(int restaurantId) throws SQLException {
        String sql = "SELECT numero, date_eval, commentaire, nom_utilisateur, fk_rest " +
                     "FROM COMMENTAIRES WHERE fk_rest=? ORDER BY date_eval DESC";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            ps.setInt(1, restaurantId);
            try (ResultSet rs = ps.executeQuery()) {
                List<CompleteEvaluation> out = new ArrayList<>();
                while (rs.next()) out.add(mapRow(rs));
                return out;
            }
        }
    }

    public CompleteEvaluation insertWithGrades(CompleteEvaluation ev) throws SQLException {
        try {
            connection().setAutoCommit(false);

            if (ev.getRestaurant() == null || ev.getRestaurant().getId() == null) {
                throw new SQLException("Restaurant id required for CompleteEvaluation");
            }

            String sqlEval = "INSERT INTO COMMENTAIRES(date_eval, commentaire, nom_utilisateur, fk_rest) " +
                             "VALUES(?, ?, ?, ?)";
            try (PreparedStatement ps = connection().prepareStatement(sqlEval, new String[] {"NUMERO"})) {
                java.util.Date d = ev.getVisitDate();
                ps.setDate(1, d != null ? new Date(d.getTime()) : null);
                ps.setString(2, ev.getComment());
                ps.setString(3, ev.getUsername());
                ps.setInt(4, ev.getRestaurant().getId());
                ps.executeUpdate();

                try (ResultSet gk = ps.getGeneratedKeys()) {
                    if (gk.next()) ev.setId(gk.getInt(1));
                }
            }

            if (ev.getGrades() != null && !ev.getGrades().isEmpty()) {
                gradeMapper.insertAllForEvaluation(ev.getId(), new ArrayList<Grade>(ev.getGrades()));
            }

            connection().commit();
            return ev;

        } catch (SQLException e) {
            connection().rollback();
            throw e;
        } finally {
            connection().setAutoCommit(true);
        }
    }
}
