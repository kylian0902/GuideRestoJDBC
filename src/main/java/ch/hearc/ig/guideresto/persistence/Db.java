package ch.hearc.ig.guideresto.persistence;


import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public final class Db {
private static Connection SINGLETON;


private Db() { }


public static synchronized Connection get() throws SQLException {
if (SINGLETON == null || SINGLETON.isClosed()) {
Properties p = new Properties();
try (InputStream in = Db.class.getClassLoader().getResourceAsStream("database.properties")) {
if (in == null) throw new IllegalStateException("database.properties introuvable");
p.load(in);
} catch (Exception e) {
throw new IllegalStateException("Impossible de lire database.properties", e);
}
String url = p.getProperty("database.url");
String user = p.getProperty("database.username");
String pwd = p.getProperty("database.password");
SINGLETON = DriverManager.getConnection(url, user, pwd);
}
return SINGLETON;
}


public static synchronized void closeQuietly() {
if (SINGLETON != null) {
try { SINGLETON.close(); } catch (Exception ignored) {}
SINGLETON = null;
}
}
}