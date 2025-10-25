package ch.hearc.ig.guideresto.persistence.mappers;

import ch.hearc.ig.guideresto.business.EvaluationCriteria;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class EvaluationCriteriaMapper extends AbstractMapper<EvaluationCriteria> {

    @Override protected String tableName() { return "CRITERES_EVALUATION"; }

    private EvaluationCriteria build(ResultSet rs) throws SQLException {
        EvaluationCriteria c = new EvaluationCriteria();
        c.setId(rs.getInt("numero"));
        c.setName(rs.getString("nom"));
        c.setDescription(rs.getString("description"));
        return c;
    }

    private EvaluationCriteria addToCache(ResultSet rs) throws SQLException {
        int id = rs.getInt("numero");
        Optional<EvaluationCriteria> cached = fromCache(id);
        if (cached.isPresent()) return cached.get();
        EvaluationCriteria c = build(rs);
        putCache(id, c);
        return c;
    }

    @Override
    protected EvaluationCriteria mapRow(ResultSet rs) throws SQLException { return addToCache(rs); }

    public Optional<EvaluationCriteria> findById(int id) throws SQLException {
        Optional<EvaluationCriteria> cached = fromCache(id);
        if (cached.isPresent()) return cached;
        String sql = "SELECT numero, nom, description FROM CRITERES_EVALUATION WHERE numero=?";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(addToCache(rs)) : Optional.empty();
            }
        }
    }

    public List<EvaluationCriteria> findAll() throws SQLException {
        String sql = "SELECT numero, nom, description FROM CRITERES_EVALUATION ORDER BY numero";
        try (PreparedStatement ps = connection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<EvaluationCriteria> out = new ArrayList<>();
            while (rs.next()) out.add(addToCache(rs));
            return out;
        }
    }

    public EvaluationCriteria insert(EvaluationCriteria c) throws SQLException {
        String sql = "INSERT INTO CRITERES_EVALUATION(nom, description) VALUES(?, ?)";
        try (PreparedStatement ps = connection().prepareStatement(sql, new String[]{"NUMERO"})) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getDescription());
            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) { if (gk.next()) c.setId(gk.getInt(1)); }
            putCache(c.getId(), c);
            return c;
        }
    }

    public void update(EvaluationCriteria c) throws SQLException {
        String sql = "UPDATE CRITERES_EVALUATION SET nom=?, description=? WHERE numero=?";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getDescription());
            ps.setInt(3, c.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (PreparedStatement ps = connection().prepareStatement("DELETE FROM CRITERES_EVALUATION WHERE numero=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
        removeCache(id);
    }
}