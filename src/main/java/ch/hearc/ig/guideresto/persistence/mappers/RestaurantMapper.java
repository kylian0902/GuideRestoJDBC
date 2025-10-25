package ch.hearc.ig.guideresto.persistence.mappers;

import ch.hearc.ig.guideresto.business.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class RestaurantMapper extends AbstractMapper<Restaurant> {

    @Override protected String tableName() { return "RESTAURANTS"; }

    /** Construit un Restaurant (sans gestion de cache) depuis le ResultSet courant */
    private Restaurant build(ResultSet rs) throws SQLException {
        // Type
        RestaurantType type = new RestaurantType();
        type.setId(rs.getInt("type_id"));
        type.setLabel(rs.getString("libelle"));
        type.setDescription(rs.getString("type_desc"));

        // Ville
        City city = new City();
        city.setId(rs.getInt("ville_id"));
        city.setZipCode(rs.getString("code_postal"));
        city.setCityName(rs.getString("nom_ville"));

        // Localisation = (rue + ville)
        Localisation loc = new Localisation();
        loc.setStreet(rs.getString("adresse"));
        loc.setCity(city);

        // Restaurant
        Restaurant r = new Restaurant();
        r.setId(rs.getInt("numero"));
        r.setName(rs.getString("nom"));
        r.setDescription(rs.getString("description"));
        r.setWebsite(rs.getString("site_web"));
        r.setAddress(loc);
        r.setType(type);

        return r;
    }

    /** Ajoute en cache (ou renvoie l’instance existante) */
    private Restaurant addToCache(ResultSet rs) throws SQLException {
        final int id = rs.getInt("numero");
        Optional<Restaurant> cached = fromCache(id);
        if (cached.isPresent()) return cached.get();
        Restaurant fresh = build(rs);
        putCache(id, fresh);
        return fresh;
    }

    @Override
    protected Restaurant mapRow(ResultSet rs) throws SQLException {
        return addToCache(rs);
    }

    /** Liste des restaurants avec type + ville (eager *-1 / 1-1) */
    public List<Restaurant> findAll() throws SQLException {
        String sql = """
            SELECT r.numero, r.nom, r.description, r.site_web, r.adresse,
                   t.numero AS type_id, t.libelle, t.description AS type_desc,
                   v.numero AS ville_id, v.code_postal, v.nom_ville
            FROM RESTAURANTS r
            JOIN TYPES_GASTRONOMIQUES t ON t.numero = r.fk_type
            JOIN VILLES v               ON v.numero = r.fk_vill
            ORDER BY r.nom
        """;
        try (PreparedStatement ps = connection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Restaurant> out = new ArrayList<>();
            while (rs.next()) out.add(addToCache(rs)); // évite les doublons (slides 30–31)
            return out;
        }
    }

    /** findById : regarde d’abord l’Identity Map */
    public Optional<Restaurant> findById(int id) throws SQLException {
        Optional<Restaurant> cached = fromCache(id);
        if (cached.isPresent()) return cached;

        String sql = """
            SELECT r.numero, r.nom, r.description, r.site_web, r.adresse,
                   t.numero AS type_id, t.libelle, t.description AS type_desc,
                   v.numero AS ville_id, v.code_postal, v.nom_ville
            FROM RESTAURANTS r
            JOIN TYPES_GASTRONOMIQUES t ON t.numero = r.fk_type
            JOIN VILLES v               ON v.numero = r.fk_vill
            WHERE r.numero = ?
        """;
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(addToCache(rs)) : Optional.empty();
            }
        }
    }

    public Restaurant insert(Restaurant r) throws SQLException {
        String sql = "INSERT INTO RESTAURANTS(nom, description, site_web, adresse, fk_type, fk_vill) VALUES(?,?,?,?,?,?)";
        try (PreparedStatement ps = connection().prepareStatement(sql, new String[]{"NUMERO"})) {
            ps.setString(1, r.getName());
            ps.setString(2, r.getDescription());
            ps.setString(3, r.getWebsite());
            ps.setString(4, r.getAddress() != null ? r.getAddress().getStreet() : null);
            ps.setInt(5, r.getType().getId());
            ps.setInt(6, r.getAddress().getCity().getId());
            ps.executeUpdate();

            try (ResultSet gk = ps.getGeneratedKeys()) {
                if (gk.next()) r.setId(gk.getInt(1));
            }
            putCache(r.getId(), r); // alimente le cache (slides 26)
            return r;
        }
    }

    public void update(Restaurant r) throws SQLException {
        String sql = "UPDATE RESTAURANTS SET nom=?, description=?, site_web=?, adresse=?, fk_type=?, fk_vill=? WHERE numero=?";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            ps.setString(1, r.getName());
            ps.setString(2, r.getDescription());
            ps.setString(3, r.getWebsite());
            ps.setString(4, r.getAddress() != null ? r.getAddress().getStreet() : null);
            ps.setInt(5, r.getType().getId());
            ps.setInt(6, r.getAddress().getCity().getId());
            ps.setInt(7, r.getId());
            ps.executeUpdate();
        }
        // instance déjà cache → pas besoin de remplacer
    }

    public void delete(int id) throws SQLException {
        try (PreparedStatement ps = connection().prepareStatement("DELETE FROM RESTAURANTS WHERE numero=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
        removeCache(id); // entretient la fraîcheur (slides 31–32)
    }
}