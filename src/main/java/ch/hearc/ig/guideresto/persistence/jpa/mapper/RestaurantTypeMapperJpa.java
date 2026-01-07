package ch.hearc.ig.guideresto.persistence.jpa.mapper;

import ch.hearc.ig.guideresto.business.RestaurantType;

import java.util.List;

public class RestaurantTypeMapperJpa extends AbstractJpaMapper<RestaurantType, Integer> {

    @Override
    protected Class<RestaurantType> entityClass() {
        return RestaurantType.class;
    }

    public List<RestaurantType> findAll() {
        return namedList("RestaurantType.findAll");
    }

    public RestaurantType findByLabel(String label) {
        return namedSingleOrNull("RestaurantType.findByLabel", "label", label);
    }
}