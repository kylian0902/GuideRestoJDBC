package ch.hearc.ig.guideresto.persistence.mappers;

import ch.hearc.ig.guideresto.business.RestaurantType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class RestaurantTypeMapper extends AbstractMapper<RestaurantType> {

    @Override protected String tableName() { return "TYPES_GASTRONOMIQUES"; }

    private RestaurantType build(ResultSet rs) throws SQLException {
        RestaurantType t = new RestaurantType();
        t.setId(rs.getInt("numero"));
        t.setLabel(rs.getString("libelle"));
        t.setDescription(rs.getString("description"));
        return t;
    }

    private RestaurantType addToCache(ResultSet rs) throws SQLException {
        int id = rs.getInt("numero");
        Optional<RestaurantType> cached = fromCache(id);
        if (cached.isPresent()) return cached.get();
        RestaurantType t = build(rs);
        putCache(id, t);
        return t;
    }

    @Override
    protected RestaurantType mapRow(ResultSet rs) throws SQLException { return addToCache(rs); }

    public Optional<RestaurantType> findById(int id) throws SQLException {
        Optional<RestaurantType> cached = fromCache(id);
        if (cached.isPresent()) return cached;
        String sql = "SELECT numero, libelle, description FROM TYPES_GASTRONOMIQUES WHERE numero=?";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(addToCache(rs)) : Optional.empty();
            }
        }
    }

    public List<RestaurantType> findAll() throws SQLException {
        String sql = "SELECT numero, libelle, description FROM TYPES_GASTRONOMIQUES ORDER BY libelle";
        try (PreparedStatement ps = connection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<RestaurantType> out = new ArrayList<>();
            while (rs.next()) out.add(addToCache(rs));
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
            putCache(t.getId(), t);
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
        removeCache(id);
    }
}