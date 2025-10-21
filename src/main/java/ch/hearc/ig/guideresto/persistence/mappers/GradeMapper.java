package ch.hearc.ig.guideresto.persistence.mappers;

import ch.hearc.ig.guideresto.business.EvaluationCriteria;
import ch.hearc.ig.guideresto.business.Grade;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GradeMapper extends AbstractMapper<Grade> {

    @Override
    protected String tableName() {
        return "NOTES";
    }

    @Override
    protected Grade mapRow(ResultSet rs) throws SQLException {
        Grade g = new Grade();
        g.setId(rs.getInt("numero"));
        g.setGrade(rs.getInt("note"));

        EvaluationCriteria c = new EvaluationCriteria();
        c.setId(rs.getInt("fk_crit"));
        g.setCriteria(c);

        return g;
    }

    public List<Grade> findByEvaluation(int evalId) throws SQLException {
        String sql = "SELECT numero, note, fk_comm, fk_crit FROM NOTES WHERE fk_comm=? ORDER BY numero";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            ps.setInt(1, evalId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Grade> out = new ArrayList<>();
                while (rs.next()) out.add(mapRow(rs));
                return out;
            }
        }
    }

    public void insertAllForEvaluation(int evalId, List<Grade> grades) throws SQLException {
        String sql = "INSERT INTO NOTES(note, fk_comm, fk_crit) VALUES(?, ?, ?)";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            for (Grade g : grades) {
                if (g.getCriteria() == null || g.getCriteria().getId() == null) {
                    throw new SQLException("Each Grade requires a Criteria with an id");
                }
                ps.setInt(1, g.getGrade());
                ps.setInt(2, evalId);
                ps.setInt(3, g.getCriteria().getId());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
}
