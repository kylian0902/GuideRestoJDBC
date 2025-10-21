package ch.hearc.ig.guideresto.persistence.mappers;


import ch.hearc.ig.guideresto.persistence.Db;
import java.sql.*;
import java.util.*;


public abstract class AbstractMapper<T> {
private final Map<Integer, T> identityMap = new HashMap<>();


protected Connection connection() throws SQLException { return Db.get(); }


protected abstract String tableName();
protected abstract T mapRow(ResultSet rs) throws SQLException;


protected Optional<T> fromCache(int id) { return Optional.ofNullable(identityMap.get(id)); }
protected void putCache(int id, T obj) { identityMap.put(id, obj); }
protected void removeCache(int id) { identityMap.remove(id); }
}