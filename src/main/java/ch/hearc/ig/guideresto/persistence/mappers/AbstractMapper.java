package ch.hearc.ig.guideresto.persistence.mappers;

import ch.hearc.ig.guideresto.persistence.Db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public abstract class AbstractMapper<T> {

    /** Identity Map par classe (instance du mapper) — simple et générique */
    private final Map<Integer, T> identityMap = new HashMap<>();

    protected Connection connection() throws SQLException { return Db.get(); }

    protected abstract String tableName();
    protected abstract T mapRow(ResultSet rs) throws SQLException;

    /** --- Helpers Identity Map --- */
    protected Optional<T> fromCache(int id) { return Optional.ofNullable(identityMap.get(id)); }
    protected void putCache(int id, T obj)   { if (id != 0 && obj != null) identityMap.put(id, obj); }
    protected void removeCache(int id)       { identityMap.remove(id); }
    public void clearCache()                 { identityMap.clear(); }
}