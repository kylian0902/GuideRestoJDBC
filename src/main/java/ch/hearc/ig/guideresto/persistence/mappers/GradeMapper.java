package ch.hearc.ig.guideresto.persistence.mappers;

import ch.hearc.ig.guideresto.business.EvaluationCriteria;
import ch.hearc.ig.guideresto.business.Grade;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class GradeMapper extends AbstractMapper<Grade> {

    @Override protected String tableName() { return "NOTES"; }

    private Grade build(ResultSet rs) throws SQLException {
        Grade g = new Grade();
        g.setId(rs.getInt("numero"));
        g.setGrade(rs.getInt("note"));

        EvaluationCriteria c = new EvaluationCriteria();
        c.setId(rs.getInt("fk_crit"));
        g.setCriteria(c);
        return g;
    }

    private Grade addToCache(ResultSet rs) throws SQLException {
        int id = rs.getInt("numero");
        Optional<Grade> cached = fromCache(id);
        if (cached.isPresent()) return cached.get();
        Grade g = build(rs);
        putCache(id, g);
        return g;
    }

    @Override
    protected Grade mapRow(ResultSet rs) throws SQLException { return addToCache(rs); }

    public List<Grade> findByEvaluation(int evalId) throws SQLException {
        String sql = "SELECT numero, note, fk_comm, fk_crit FROM NOTES WHERE fk_comm=? ORDER BY numero";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            ps.setInt(1, evalId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Grade> out = new ArrayList<>();
                while (rs.next()) out.add(addToCache(rs));
                return out;
            }
        }
    }

    /** Batch insert des notes d'une évaluation. (PK non récupérées → pas d’alim cache ici) */
    public void insertAllForEvaluation(Integer evalId, List<Grade> grades) throws SQLException {
        if (evalId == null) throw new SQLException("evalId is null");
        if (grades == null || grades.isEmpty()) return;

        String sql = "INSERT INTO NOTES(note, fk_comm, fk_crit) VALUES(?, ?, ?)";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            for (Grade g : grades) {
                if (g == null) continue;
                Integer critId = g.getCriteria() != null ? g.getCriteria().getId() : null;
                if (critId == null) throw new SQLException("Grade missing criteria id");
                int note = g.getGrade() == null ? 0 : g.getGrade();
                ps.setInt(1, note);
                ps.setInt(2, evalId);
                ps.setInt(3, critId);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
}