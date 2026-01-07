package ch.hearc.ig.guideresto.persistence.jpa;

import jakarta.persistence.*;
import java.util.function.Function;

public final class JpaUtils {

    private static final EntityManagerFactory EMF =
            Persistence.createEntityManagerFactory("guideRestoJPA");

    private JpaUtils() {}

    public static <T> T inRead(Function<EntityManager, T> work) {
        EntityManager em = EMF.createEntityManager();
        try { return work.apply(em); }
        finally { em.close(); }
    }

    public static <T> T inTx(Function<EntityManager, T> work) {
        EntityManager em = EMF.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T out = work.apply(em);
            tx.commit();
            return out;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}