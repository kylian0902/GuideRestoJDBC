package ch.hearc.ig.guideresto.service;

import ch.hearc.ig.guideresto.business.*;
import ch.hearc.ig.guideresto.persistence.jpa.JpaUtils;

import java.sql.SQLException;
import java.util.List;

public class GuideRestoServiceJpaImpl implements GuideRestoService {

    // -------- LECTURE --------
    @Override
    public List<Restaurant> getAllRestaurants() throws SQLException {
        try {
            return JpaUtils.inRead(em ->
                    em.createQuery("""
              select r from Restaurant r
              join fetch r.type
              join fetch r.address.city
              order by r.id
          """, Restaurant.class).getResultList()
            );
        } catch (Exception e) { throw new SQLException(e); }
    }

    @Override
    public Restaurant getRestaurantById(int id) throws SQLException {
        try {
            return JpaUtils.inRead(em ->
                    em.createQuery("""
              select r from Restaurant r
              join fetch r.type
              join fetch r.address.city
              where r.id = :id
          """, Restaurant.class)
                            .setParameter("id", id)
                            .getResultStream()
                            .findFirst()
                            .orElse(null)
            );
        } catch (Exception e) { throw new SQLException(e); }
    }

    @Override
    public List<BasicEvaluation> getLikesByRestaurant(int restId) throws SQLException {
        try {
            return JpaUtils.inRead(em ->
                    em.createQuery("""
              select b from BasicEvaluation b
              where b.restaurant.id = :id
              order by b.visitDate desc
          """, BasicEvaluation.class)
                            .setParameter("id", restId)
                            .getResultList()
            );
        } catch (Exception e) { throw new SQLException(e); }
    }

    @Override
    public List<CompleteEvaluation> getCompleteEvaluationsByRestaurant(int restId) throws SQLException {
        try {
            return JpaUtils.inRead(em ->
                    em.createQuery("""
              select c from CompleteEvaluation c
              where c.restaurant.id = :id
              order by c.visitDate desc
          """, CompleteEvaluation.class)
                            .setParameter("id", restId)
                            .getResultList()
            );
        } catch (Exception e) { throw new SQLException(e); }
    }

    @Override
    public List<City> getCities() throws SQLException {
        try {
            return JpaUtils.inRead(em ->
                    em.createQuery("select c from City c order by c.id", City.class).getResultList()
            );
        } catch (Exception e) { throw new SQLException(e); }
    }

    @Override
    public List<RestaurantType> getTypes() throws SQLException {
        try {
            return JpaUtils.inRead(em ->
                    em.createQuery("select t from RestaurantType t order by t.id", RestaurantType.class).getResultList()
            );
        } catch (Exception e) { throw new SQLException(e); }
    }

    @Override
    public List<EvaluationCriteria> getCriterias() throws SQLException {
        try {
            return JpaUtils.inRead(em ->
                    em.createQuery("select c from EvaluationCriteria c order by c.id", EvaluationCriteria.class).getResultList()
            );
        } catch (Exception e) { throw new SQLException(e); }
    }

    // -------- ECRITURE --------
    @Override
    public void addLike(BasicEvaluation like) throws SQLException {
        try {
            JpaUtils.inTx(em -> {
                // IMPORTANT : rattacher un Restaurant "managed"
                Integer restId = like.getRestaurant().getId();
                like.setRestaurant(em.getReference(Restaurant.class, restId));

                // Si ta DB refuse NULL sur adresse_ip, il faut forcer une valeur
                if (like.getIpAddress() == null || like.getIpAddress().isBlank()) {
                    like.setIpAddress("0.0.0.0"); // simple valeur par défaut
                }

                em.persist(like);
                return null;
            });
        } catch (Exception e) { throw new SQLException(e); }
    }

    @Override
    public void addCompleteEvaluation(CompleteEvaluation eval) throws SQLException {
        try {
            JpaUtils.inTx(em -> {
                // 1) restaurant (managed)
                Integer restId = eval.getRestaurant().getId();
                eval.setRestaurant(em.getReference(Restaurant.class, restId));

                // 2) notes : lier chaque Grade à l'évaluation + rattacher criteria
                if (eval.getGrades() != null) {
                    for (Grade g : eval.getGrades()) {
                        g.setEvaluation(eval); // obligatoire (mappedBy)
                        Integer critId = g.getCriteria().getId();
                        g.setCriteria(em.getReference(EvaluationCriteria.class, critId));
                    }
                }

                // 3) persist (cascade -> grades)
                em.persist(eval);
                return null;
            });
        } catch (Exception e) { throw new SQLException(e); }
    }
}