package ch.hearc.ig.guideresto.persistence;


import java.sql.Connection;


public class SmokeDbCheck {
public static void main(String[] args) throws Exception {
try (Connection c = Db.get()) {
System.out.println("Connexion OK: " + c.getMetaData().getURL());
}
}
}