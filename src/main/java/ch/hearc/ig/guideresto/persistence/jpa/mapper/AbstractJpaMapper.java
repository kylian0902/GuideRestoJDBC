package ch.hearc.ig.guideresto.persistence.jpa.mapper;

import ch.hearc.ig.guideresto.persistence.jpa.JpaUtils;
import jakarta.persistence.EntityManager;

import java.util.List;

public abstract class AbstractJpaMapper<T, K> {

    protected abstract Class<T> entityClass();

    public T findById(K id) {
        return JpaUtils.inRead(em -> em.find(entityClass(), id));
    }

    public void delete(T entity) {
        JpaUtils.inTx(em -> {
            T managed = em.contains(entity) ? entity : em.merge(entity);
            em.remove(managed);
            return null;
        });
    }

    public void deleteById(K id) {
        JpaUtils.inTx(em -> {
            T ref = em.getReference(entityClass(), id);
            em.remove(ref);
            return null;
        });
    }

    protected List<T> namedList(String queryName) {
        return JpaUtils.inRead(em ->
                em.createNamedQuery(queryName, entityClass()).getResultList()
        );
    }

    protected T namedSingleOrNull(String queryName, String param, Object value) {
        return JpaUtils.inRead(em ->
                em.createNamedQuery(queryName, entityClass())
                        .setParameter(param, value)
                        .getResultStream()
                        .findFirst()
                        .orElse(null)
        );
    }

    protected List<T> namedList(String queryName, String param, Object value) {
        return JpaUtils.inRead(em ->
                em.createNamedQuery(queryName, entityClass())
                        .setParameter(param, value)
                        .getResultList()
        );
    }

    protected List<T> namedList2(String queryName,
                                 String p1, Object v1,
                                 String p2, Object v2) {
        return JpaUtils.inRead(em ->
                em.createNamedQuery(queryName, entityClass())
                        .setParameter(p1, v1)
                        .setParameter(p2, v2)
                        .getResultList()
        );
    }

    protected T namedSingleOrNull2(String queryName,
                                   String p1, Object v1,
                                   String p2, Object v2) {
        return JpaUtils.inRead(em ->
                em.createNamedQuery(queryName, entityClass())
                        .setParameter(p1, v1)
                        .setParameter(p2, v2)
                        .getResultStream()
                        .findFirst()
                        .orElse(null)
        );
    }
}