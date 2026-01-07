package ch.hearc.ig.guideresto.persistence.jpa.mapper;

import ch.hearc.ig.guideresto.business.Restaurant;

import java.util.List;

public class RestaurantMapperJpa extends AbstractJpaMapper<Restaurant, Integer> {

    @Override
    protected Class<Restaurant> entityClass() {
        return Restaurant.class;
    }

    public List<Restaurant> findAll() {
        return namedList("Restaurant.findAll");
    }

    public Restaurant findByName(String name) {
        return namedSingleOrNull("Restaurant.findByName", "name", name);
    }

    public Restaurant findByNameAndCityId(String name, int cityId) {
        return namedSingleOrNull2("Restaurant.findByNameAndCityId", "name", name, "cityId", cityId);
    }

    public List<Restaurant> findByCityId(int cityId) {
        return namedList("Restaurant.findByCityId", "cityId", cityId);
    }

    public List<Restaurant> findByTypeId(int typeId) {
        return namedList("Restaurant.findByTypeId", "typeId", typeId);
    }

    public List<Restaurant> findByStreet(String street) {
        return namedList("Restaurant.findByStreet", "street", street);
    }

    public List<Restaurant> findByZipCode(int zip) {
        return namedList("Restaurant.findByZipCode", "zip", zip);
    }
}
