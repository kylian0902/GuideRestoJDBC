package ch.hearc.ig.guideresto.persistence.mappers;


import ch.hearc.ig.guideresto.business.EvaluationCriteria;
import java.sql.*;
import java.util.*;


public class EvaluationCriteriaMapper extends AbstractMapper<EvaluationCriteria> {
@Override protected String tableName() { return "CRITERES_EVALUATION"; }


@Override
protected EvaluationCriteria mapRow(ResultSet rs) throws SQLException {
return new EvaluationCriteria(
rs.getInt("numero"),
rs.getString("nom"),
rs.getString("description")
);
}


public List<EvaluationCriteria> findAll() throws SQLException {
String sql = "SELECT numero, nom, description FROM CRITERES_EVALUATION ORDER BY numero";
try (PreparedStatement ps = connection().prepareStatement(sql);
ResultSet rs = ps.executeQuery()) {
List<EvaluationCriteria> out = new ArrayList<>();
while (rs.next()) out.add(mapRow(rs));
return out;
}
}


public Optional<EvaluationCriteria> findById(int id) throws SQLException {
try (PreparedStatement ps = connection().prepareStatement(
"SELECT numero, nom, description FROM CRITERES_EVALUATION WHERE numero=?")) {
ps.setInt(1, id);
try (ResultSet rs = ps.executeQuery()) {
return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
}
}
}
}