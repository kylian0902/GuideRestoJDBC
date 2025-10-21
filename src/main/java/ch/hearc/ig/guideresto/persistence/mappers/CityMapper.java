package ch.hearc.ig.guideresto.persistence.mappers;


import ch.hearc.ig.guideresto.business.City;
import java.sql.*;
import java.util.*;


public class CityMapper extends AbstractMapper<City> {
@Override protected String tableName() { return "VILLES"; }


@Override
protected City mapRow(ResultSet rs) throws SQLException {
City c = new City(
rs.getInt("numero"),
rs.getString("nom_ville"),
rs.getString("code_postal")
);
return c;
}


public Optional<City> findById(int id) throws SQLException {
String sql = "SELECT numero, nom_ville, code_postal FROM VILLES WHERE numero=?";
try (PreparedStatement ps = connection().prepareStatement(sql)) {
ps.setInt(1, id);
try (ResultSet rs = ps.executeQuery()) {
if (rs.next()) {
City c = mapRow(rs); putCache(id, c); return Optional.of(c);
}
return Optional.empty();
}
}
}


public List<City> findAll() throws SQLException {
String sql = "SELECT numero, nom_ville, code_postal FROM VILLES ORDER BY nom_ville";
try (PreparedStatement ps = connection().prepareStatement(sql);
ResultSet rs = ps.executeQuery()) {
List<City> out = new ArrayList<>();
while (rs.next()) out.add(mapRow(rs));
return out;
}
}


public City insert(City c) throws SQLException {
String sql = "INSERT INTO VILLES(nom_ville, code_postal) VALUES(?, ?)";
try (PreparedStatement ps = connection().prepareStatement(sql, new String[]{"NUMERO"})) {
ps.setString(1, c.getCityName());
ps.setString(2, c.getZipCode());
ps.executeUpdate();
try (ResultSet gk = ps.getGeneratedKeys()) { if (gk.next()) c.setId(gk.getInt(1)); }
putCache(c.getId(), c);
return c;
}
}


public void update(City c) throws SQLException {
String sql = "UPDATE VILLES SET nom_ville=?, code_postal=? WHERE numero=?";
try (PreparedStatement ps = connection().prepareStatement(sql)) {
ps.setString(1, c.getCityName());
ps.setString(2, c.getZipCode());
ps.setInt(3, c.getId());
ps.executeUpdate();
}
}


public void delete(int id) throws SQLException {
String sql = "DELETE FROM VILLES WHERE numero=?";
try (PreparedStatement ps = connection().prepareStatement(sql)) {
}
}
}