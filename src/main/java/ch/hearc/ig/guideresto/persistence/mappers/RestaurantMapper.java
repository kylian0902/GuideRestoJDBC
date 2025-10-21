package ch.hearc.ig.guideresto.persistence.mappers;

import ch.hearc.ig.guideresto.business.City;
import ch.hearc.ig.guideresto.business.Localisation;
import ch.hearc.ig.guideresto.business.Restaurant;
import ch.hearc.ig.guideresto.business.RestaurantType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RestaurantMapper extends AbstractMapper<Restaurant> {

    @Override
    protected String tableName() {
        return "RESTAURANTS";
    }

    @Override
    protected Restaurant mapRow(ResultSet rs) throws SQLException {
        // ----- RestaurantType (eager)
        RestaurantType type = new RestaurantType();
        setFirstAvailable(type, new String[]{"setId","setNumero"}, int.class, rsGetInt(rs,"type_id"));
        setFirstAvailable(type, new String[]{"setLabel","setLibelle","setName","setNom"}, String.class, rsGetString(rs,"libelle"));
        setFirstAvailable(type, new String[]{"setDescription"}, String.class, rsGetString(rs,"type_desc"));

        // ----- City (eager)
        City city = new City();
        setFirstAvailable(city, new String[]{"setId","setNumero"}, int.class, rsGetInt(rs,"ville_id"));
        setFirstAvailable(city, new String[]{"setName","setNom","setCityName","setNomVille"}, String.class, rsGetString(rs,"nom_ville"));
        setFirstAvailable(city, new String[]{"setZip","setPostalCode","setCodePostal"}, String.class, rsGetString(rs,"code_postal"));

        // ----- Localisation (adresse + city) — attendu par Restaurant.setAddress(Localisation)
        String street = rsGetString(rs,"adresse");
        Localisation loc = buildLocalisation(street, city);

        // ----- Restaurant (via setters, robustes)
        Restaurant r = new Restaurant();
        setFirstAvailable(r, new String[]{"setId","setNumero"}, int.class, rsGetInt(rs,"numero"));
        setFirstAvailable(r, new String[]{"setName","setNom"}, String.class, rsGetString(rs,"nom"));
        setFirstAvailable(r, new String[]{"setDescription"}, String.class, rsGetString(rs,"description"));
        setFirstAvailable(r, new String[]{"setWebsite","setSiteWeb","setUrl"}, String.class, rsGetString(rs,"site_web"));
        setFirstAvailable(r, new String[]{"setAddress","setLocalisation","setLocation"}, Localisation.class, loc);
        setFirstAvailable(r, new String[]{"setType","setRestaurantType","setGastronomicType"}, RestaurantType.class, type);
        setFirstAvailable(r, new String[]{"setCity","setVille"}, City.class, city);

        return r;
    }

    public List<Restaurant> findAll() throws SQLException {
        String sql =
            "SELECT r.numero, r.nom, r.description, r.site_web, r.adresse, " +
            "       t.numero AS type_id, t.libelle, t.description AS type_desc, " +
            "       v.numero AS ville_id, v.code_postal, v.nom_ville " +
            "FROM RESTAURANTS r " +
            "JOIN TYPES_GASTRONOMIQUES t ON t.numero = r.fk_type " +
            "JOIN VILLES v               ON v.numero = r.fk_vill " +
            "ORDER BY r.nom";
        try (PreparedStatement ps = connection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Restaurant> out = new ArrayList<>();
            while (rs.next()) out.add(mapRow(rs));
            return out;
        }
    }

    public Optional<Restaurant> findById(int id) throws SQLException {
        String sql =
            "SELECT r.numero, r.nom, r.description, r.site_web, r.adresse, " +
            "       t.numero AS type_id, t.libelle, t.description AS type_desc, " +
            "       v.numero AS ville_id, v.code_postal, v.nom_ville " +
            "FROM RESTAURANTS r " +
            "JOIN TYPES_GASTRONOMIQUES t ON t.numero = r.fk_type " +
            "JOIN VILLES v               ON v.numero = r.fk_vill " +
            "WHERE r.numero = ?";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    public Restaurant insert(Restaurant r) throws SQLException {
        String sql = "INSERT INTO RESTAURANTS(nom, description, site_web, adresse, fk_type, fk_vill) VALUES(?,?,?,?,?,?)";
        try (PreparedStatement ps = connection().prepareStatement(sql, new String[] {"NUMERO"})) {
            ps.setString(1, readString(r, new String[]{"getName","getNom"}));
            ps.setString(2, readString(r, new String[]{"getDescription"}));
            ps.setString(3, readString(r, new String[]{"getWebsite","getSiteWeb","getUrl"}));
            Object address = readObject(r, new String[]{"getAddress","getLocalisation","getLocation"});
            ps.setString(4, readString(address, new String[]{"getStreet","getRue","getAddressLine","getAdresse","getStreetAddress","getLine1"}));
            Object type = readObject(r, new String[]{"getType","getRestaurantType","getGastronomicType"});
            ps.setInt(5, readInt(type, new String[]{"getId","getNumero"}));
            Object city = readObject(address, new String[]{"getCity","getVille"});
            ps.setInt(6, readInt(city, new String[]{"getId","getNumero"}));

            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) {
                if (gk.next()) setFirstAvailable(r, new String[]{"setId","setNumero"}, int.class, gk.getInt(1));
            }
            return r;
        }
    }

    public void update(Restaurant r) throws SQLException {
        String sql = "UPDATE RESTAURANTS SET nom=?, description=?, site_web=?, adresse=?, fk_type=?, fk_vill=? WHERE numero=?";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            ps.setString(1, readString(r, new String[]{"getName","getNom"}));
            ps.setString(2, readString(r, new String[]{"getDescription"}));
            ps.setString(3, readString(r, new String[]{"getWebsite","getSiteWeb","getUrl"}));
            Object address = readObject(r, new String[]{"getAddress","getLocalisation","getLocation"});
            ps.setString(4, readString(address, new String[]{"getStreet","getRue","getAddressLine","getAdresse","getStreetAddress","getLine1"}));
            Object type = readObject(r, new String[]{"getType","getRestaurantType","getGastronomicType"});
            ps.setInt(5, readInt(type, new String[]{"getId","getNumero"}));
            Object city = readObject(address, new String[]{"getCity","getVille"});
            ps.setInt(6, readInt(city, new String[]{"getId","getNumero"}));
            ps.setInt(7, readInt(r, new String[]{"getId","getNumero"}));
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (PreparedStatement ps = connection().prepareStatement("DELETE FROM RESTAURANTS WHERE numero=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // ========== Helpers ==========

    private static Localisation buildLocalisation(String street, City city) {
        try {
            Constructor<Localisation> c = Localisation.class.getConstructor(String.class, City.class);
            return c.newInstance(street, city);
        } catch (NoSuchMethodException ignored) {
            try {
                Localisation loc = Localisation.class.getDeclaredConstructor().newInstance();
                tryMethod(loc, "setCity", new Class[]{City.class}, new Object[]{city});
                String[] setters = {"setStreet","setRue","setAddressLine","setAdresse","setStreetAddress","setLine1"};
                boolean ok = false;
                for (String m : setters) {
                    if (tryMethod(loc, m, new Class[]{String.class}, new Object[]{street})) { ok = true; break; }
                }
                if (!ok) {
                    String[] fields = {"street","rue","address","adresse","streetAddress","line1"};
                    for (String f : fields) { if (tryField(loc, f, street)) break; }
                }
                return loc;
            } catch (Exception e) {
                throw new RuntimeException("Unable to instantiate Localisation", e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to instantiate Localisation", e);
        }
    }

    private static int rsGetInt(ResultSet rs, String col) throws SQLException {
        int v = rs.getInt(col);
        return v;
    }
    private static String rsGetString(ResultSet rs, String col) throws SQLException {
        return rs.getString(col);
    }

    private static void setFirstAvailable(Object target, String[] methodNames, Class<?> paramType, Object value) {
        for (String name : methodNames) {
            if (tryMethod(target, name, new Class[]{paramType}, new Object[]{value})) return;
        }
        if (paramType == String.class) {
            for (String f : new String[]{"name","nom","description","label","libelle","url","siteWeb","address","adresse"}) {
                if (tryField(target, f, value)) return;
            }
        } else if (paramType == int.class || paramType == Integer.class) {
            for (String f : new String[]{"id","numero"}) {
                if (tryField(target, f, value)) return;
            }
        } else {
            for (String f : new String[]{"address","adresse","type","city","ville","localisation","location"}) {
                if (tryField(target, f, value)) return;
            }
        }
    }

    private static boolean tryMethod(Object target, String name, Class<?>[] types, Object[] values) {
        try {
            Method m = target.getClass().getMethod(name, types);
            m.setAccessible(true);
            m.invoke(target, values);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean tryField(Object target, String fieldName, Object value) {
        try {
            Field f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(target, value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static Object readObject(Object target, String[] getterNames) throws SQLException {
        for (String g : getterNames) {
            Object val = tryGet(target, g);
            if (val != null) return val;
        }
        throw new SQLException("Getter not found on " + target.getClass().getSimpleName() + " for any of " + String.join(", ", getterNames));
    }

    private static String readString(Object target, String[] getterNames) throws SQLException {
        Object o = readObject(target, getterNames);
        return (o == null) ? null : String.valueOf(o);
    }

    private static int readInt(Object target, String[] getterNames) throws SQLException {
        Object o = readObject(target, getterNames);
        if (o instanceof Number) return ((Number)o).intValue();
        try { return Integer.parseInt(String.valueOf(o)); }
        catch (Exception e) { throw new SQLException("Expected int from " + target.getClass().getSimpleName()); }
    }

    private static Object tryGet(Object target, String getterName) {
        try {
            Method m = target.getClass().getMethod(getterName);
            m.setAccessible(true);
            return m.invoke(target);
        } catch (Exception e) {
            return null;
        }
    }
}