package ch.hearc.ig.guideresto.persistence.jpa.mapper;

import ch.hearc.ig.guideresto.business.City;

import java.util.List;

public class CityMapperJpa extends AbstractJpaMapper<City, Integer> {

    @Override
    protected Class<City> entityClass() {
        return City.class;
    }

    public List<City> findAll() {
        return namedList("City.findAll");
    }

    public City findByZipAndName(int zip, String name) {
        return namedSingleOrNull2("City.findByZipAndName", "zip", zip, "name", name);
    }

    public List<City> findByZip(int zip) {
        return namedList("City.findByZip", "zip", zip);
    }

    public List<City> findByName(String name) {
        return namedList("City.findByName", "name", name);
    }
}