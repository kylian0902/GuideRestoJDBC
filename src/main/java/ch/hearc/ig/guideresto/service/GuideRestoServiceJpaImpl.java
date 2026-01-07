package ch.hearc.ig.guideresto.service;

import ch.hearc.ig.guideresto.business.*;
import ch.hearc.ig.guideresto.persistence.jpa.JpaUtils;
import ch.hearc.ig.guideresto.persistence.jpa.mapper.*;

import java.sql.SQLException;
import java.util.List;

public class GuideRestoServiceJpaImpl implements GuideRestoService {

    private final RestaurantMapperJpa restaurantMapper = new RestaurantMapperJpa();
    private final CityMapperJpa cityMapper = new CityMapperJpa();
    private final RestaurantTypeMapperJpa typeMapper = new RestaurantTypeMapperJpa();
    private final EvaluationCriteriaMapperJpa criteriaMapper = new EvaluationCriteriaMapperJpa();
    private final BasicEvaluationMapperJpa basicEvalMapper = new BasicEvaluationMapperJpa();
    private final CompleteEvaluationMapperJpa completeEvalMapper = new CompleteEvaluationMapperJpa();
    private final GradeMapperJpa gradeMapper = new GradeMapperJpa(); // pas forcément utilisé partout

    // -------- LECTURE --------
    @Override
    public List<Restaurant> getAllRestaurants() throws SQLException {
        try {
            // Ex5: passer par le Mapper + NamedQuery Restaurant.findAll
            return restaurantMapper.findAll();
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public Restaurant getRestaurantById(int id) throws SQLException {
        try {
            // Ex5: passer par NamedQuery Restaurant.findById (avec fetch)
            // On utilise JpaUtils ici car ton RestaurantMapperJpa actuel n'expose pas findById "fetch".
            // Si tu veux, tu peux ajouter restaurantMapper.findByIdFetch(id).
            return JpaUtils.inRead(em ->
                    em.createNamedQuery("Restaurant.findById", Restaurant.class)
                            .setParameter("id", id)
                            .getResultStream()
                            .findFirst()
                            .orElse(null)
            );
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public List<BasicEvaluation> getLikesByRestaurant(int restId) throws SQLException {
        try {
            // Ex5: Mapper + NamedQuery BasicEvaluation.findByRestaurantId
            return basicEvalMapper.findByRestaurantId(restId);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public List<CompleteEvaluation> getCompleteEvaluationsByRestaurant(int restId) throws SQLException {
        try {
            // Ex5: Mapper + NamedQuery CompleteEvaluation.findByRestaurantIdWithGrades
            // (si tu n'as pas encore la NamedQuery "WithGrades", remplace par findByRestaurantId)
            return completeEvalMapper.findByRestaurantId(restId);

        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public List<City> getCities() throws SQLException {
        try {
            return cityMapper.findAll();
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public List<RestaurantType> getTypes() throws SQLException {
        try {
            return typeMapper.findAll();
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public List<EvaluationCriteria> getCriterias() throws SQLException {
        try {
            return criteriaMapper.findAll();
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    // -------- ECRITURE --------
    @Override
    public void addLike(BasicEvaluation like) throws SQLException {
        try {
            JpaUtils.inTx(em -> {
                // 1) Rattacher le restaurant (managed)
                Integer restId = like.getRestaurant().getId();
                like.setRestaurant(em.getReference(Restaurant.class, restId));

                // 2) DB : si adresse_ip NOT NULL
                if (like.getIpAddress() == null || like.getIpAddress().isBlank()) {
                    like.setIpAddress("0.0.0.0");
                }

                // 3) Persist
                em.persist(like);
                return null;
            });
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public void addCompleteEvaluation(CompleteEvaluation eval) throws SQLException {
        try {
            JpaUtils.inTx(em -> {
                // 1) Rattacher le restaurant (managed)
                Integer restId = eval.getRestaurant().getId();
                eval.setRestaurant(em.getReference(Restaurant.class, restId));

                // 2) Lier les grades + rattacher les criteria (managed)
                if (eval.getGrades() != null) {
                    for (Grade g : eval.getGrades()) {
                        g.setEvaluation(eval); // obligatoire (mappedBy)

                        if (g.getCriteria() != null && g.getCriteria().getId() != null) {
                            Integer critId = g.getCriteria().getId();
                            g.setCriteria(em.getReference(EvaluationCriteria.class, critId));
                        }
                    }
                }

                // 3) Persist (cascade ALL sur grades recommandé)
                em.persist(eval);
                return null;
            });
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public void addRestaurant(Restaurant restaurant) throws SQLException {
        try {
            JpaUtils.inTx(em -> {

                if (restaurant == null) throw new IllegalArgumentException("restaurant null");
                if (restaurant.getType() == null || restaurant.getType().getId() == null)
                    throw new IllegalArgumentException("RestaurantType manquant (id)");
                if (restaurant.getAddress() == null)
                    throw new IllegalArgumentException("Localisation manquante");
                if (restaurant.getAddress().getCity() == null)
                    throw new IllegalArgumentException("City manquante");
                if (restaurant.getAddress().getStreet() == null || restaurant.getAddress().getStreet().isBlank())
                    throw new IllegalArgumentException("Adresse (street) manquante");

                City city = restaurant.getAddress().getCity();

                City managedCity;
                if (city.getId() != null) {
                    managedCity = em.getReference(City.class, city.getId());
                } else {
                    City existing = em.createNamedQuery("City.findByZipAndName", City.class)
                            .setParameter("zip", city.getZipCode())
                            .setParameter("name", city.getCityName())
                            .getResultStream()
                            .findFirst()
                            .orElse(null);

                    if (existing != null) {
                        managedCity = existing;
                    } else {
                        em.persist(city);      // crée la ville
                        managedCity = city;    // maintenant managed
                    }
                }

                RestaurantType managedType = em.getReference(RestaurantType.class, restaurant.getType().getId());

                restaurant.setType(managedType);
                restaurant.getAddress().setCity(managedCity);

                em.persist(restaurant);

                return null;
            });
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }
}