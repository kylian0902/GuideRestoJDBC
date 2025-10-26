package ch.hearc.ig.guideresto.persistence.mappers;

import ch.hearc.ig.guideresto.persistence.Db;
import ch.hearc.ig.guideresto.persistence.tx.TransactionManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public abstract class AbstractMapper<T> {

    private final Map<Integer, T> identityMap = new HashMap<>();

    /** Utilise la connexion de la transaction en cours, sinon une connexion ad hoc. */
    protected Connection connection() throws SQLException {
        Connection cur = TransactionManager.current();
        return (cur != null) ? cur : Db.get();
    }

    protected abstract String tableName();
    protected abstract T mapRow(ResultSet rs) throws SQLException;

    protected Optional<T> fromCache(int id) { return Optional.ofNullable(identityMap.get(id)); }
    protected void putCache(int id, T obj)   { if (id != 0 && obj != null) identityMap.put(id, obj); }
    protected void removeCache(int id)       { identityMap.remove(id); }
    public void clearCache()                 { identityMap.clear(); }
}
