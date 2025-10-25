package ch.hearc.ig.guideresto.persistence.cache;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public final class IdentityMap<T> {
    private final ConcurrentHashMap<Integer, T> map = new ConcurrentHashMap<>();

    public T get(Integer id) { return id == null ? null : map.get(id); }

    /** Retourne l'objet en cache, ou le crée via factory et le met en cache. */
    public T getOrPut(Integer id, Supplier<T> factory) {
        if (id == null) return factory.get(); // sans ID ⇒ pas de cache
        return map.computeIfAbsent(id, k -> factory.get());
    }

    public void put(Integer id, T obj) { if (id != null && obj != null) map.put(id, obj); }
    public void remove(Integer id) { if (id != null) map.remove(id); }
    public void clear() { map.clear(); }
    public Collection<T> values() { return map.values(); }
}