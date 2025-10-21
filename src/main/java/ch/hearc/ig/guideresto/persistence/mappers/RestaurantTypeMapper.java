package ch.hearc.ig.guideresto.persistence.mappers;


import ch.hearc.ig.guideresto.business.RestaurantType;
import java.sql.*;
import java.util.*;


public class RestaurantTypeMapper extends AbstractMapper<RestaurantType> {
@Override protected String tableName() { return "TYPES_GASTRONOMIQUES"; }


@Override
protected RestaurantType mapRow(ResultSet rs) throws SQLException {
return new RestaurantType(
rs.getInt("numero"),
rs.getString("libelle"),
rs.getString("description")
);
}


public Optional<RestaurantType> findById(int id) throws SQLException {
String sql = "SELECT numero, libelle, description FROM TYPES_GASTRONOMIQUES WHERE numero=?";
try (PreparedStatement ps = connection().prepareStatement(sql)) {
ps.setInt(1, id);
try (ResultSet rs = ps.executeQuery()) {
return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
}
}
}


public List<RestaurantType> findAll() throws SQLException {
String sql = "SELECT numero, libelle, description FROM TYPES_GASTRONOMIQUES ORDER BY libelle";
try (PreparedStatement ps = connection().prepareStatement(sql);
ResultSet rs = ps.executeQuery()) {
List<RestaurantType> out = new ArrayList<>();
while (rs.next()) out.add(mapRow(rs));
return out;
}
}


public RestaurantType insert(RestaurantType t) throws SQLException {
String sql = "INSERT INTO TYPES_GASTRONOMIQUES(libelle, description) VALUES(?, ?)";
try (PreparedStatement ps = connection().prepareStatement(sql, new String[]{"NUMERO"})) {
ps.setString(1, t.getLabel());
ps.setString(2, t.getDescription());
ps.executeUpdate();
try (ResultSet gk = ps.getGeneratedKeys()) { if (gk.next()) t.setId(gk.getInt(1)); }
return t;
}
}


public void update(RestaurantType t) throws SQLException {
String sql = "UPDATE TYPES_GASTRONOMIQUES SET libelle=?, description=? WHERE numero=?";
try (PreparedStatement ps = connection().prepareStatement(sql)) {
ps.setString(1, t.getLabel());
ps.setString(2, t.getDescription());
ps.setInt(3, t.getId());
ps.executeUpdate();
}
}


public void delete(int id) throws SQLException {
try (PreparedStatement ps = connection().prepareStatement("DELETE FROM TYPES_GASTRONOMIQUES WHERE numero=?")) {
ps.setInt(1, id);
ps.executeUpdate();
}
}
}