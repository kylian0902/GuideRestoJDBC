package ch.hearc.ig.guideresto.persistence.mappers;

import ch.hearc.ig.guideresto.business.City;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class CityMapper extends AbstractMapper<City> {

    @Override protected String tableName() { return "VILLES"; }

    private City build(ResultSet rs) throws SQLException {
        City c = new City();
        c.setId(rs.getInt("numero"));
        c.setCityName(rs.getString("nom_ville"));
        c.setZipCode(rs.getString("code_postal"));
        return c;
    }

    private City addToCache(ResultSet rs) throws SQLException {
        int id = rs.getInt("numero");
        Optional<City> cached = fromCache(id);
        if (cached.isPresent()) return cached.get();
        City c = build(rs);
        putCache(id, c);
        return c;
    }

    @Override
    protected City mapRow(ResultSet rs) throws SQLException { return addToCache(rs); }

    public Optional<City> findById(int id) throws SQLException {
        Optional<City> cached = fromCache(id);
        if (cached.isPresent()) return cached;
        String sql = "SELECT numero, nom_ville, code_postal FROM VILLES WHERE numero=?";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(addToCache(rs)) : Optional.empty();
            }
        }
    }

    public List<City> findAll() throws SQLException {
        String sql = "SELECT numero, nom_ville, code_postal FROM VILLES ORDER BY nom_ville";
        try (PreparedStatement ps = connection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<City> out = new ArrayList<>();
            while (rs.next()) out.add(addToCache(rs));
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
        try (PreparedStatement ps = connection().prepareStatement("DELETE FROM VILLES WHERE numero=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
        removeCache(id);
    }
}