package ch.hearc.ig.guideresto.persistence.tx;

import ch.hearc.ig.guideresto.persistence.Db;

import java.sql.Connection;
import java.sql.SQLException;

public final class TransactionManager {
    private static final ThreadLocal<Connection> CTX = new ThreadLocal<>();

    private TransactionManager() {}

    public static void begin() throws SQLException {
        if (CTX.get() != null) throw new IllegalStateException("Transaction déjà ouverte");
        Connection c = Db.get();
        c.setAutoCommit(false);
        CTX.set(c);
    }

    public static void commit() throws SQLException {
        Connection c = CTX.get();
        if (c == null) return;
        try {
            c.commit();
        } finally {
            try { c.setAutoCommit(true); } catch (SQLException ignored) {}
            try { c.close(); } catch (SQLException ignored) {}
            CTX.remove();
        }
    }

    public static void rollback() throws SQLException {
        Connection c = CTX.get();
        if (c == null) return;
        try {
            c.rollback();
        } finally {
            try { c.setAutoCommit(true); } catch (SQLException ignored) {}
            try { c.close(); } catch (SQLException ignored) {}
            CTX.remove();
        }
    }

    public static Connection current() { return CTX.get(); }
}