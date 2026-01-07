package ch.hearc.ig.guideresto.service;

import ch.hearc.ig.guideresto.business.*;
import ch.hearc.ig.guideresto.persistence.mappers.*;
import ch.hearc.ig.guideresto.persistence.tx.TransactionManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GuideRestoServiceImpl implements GuideRestoService {

    private final RestaurantMapper restaurantMapper = new RestaurantMapper();
    private final BasicEvaluationMapper basicEvalMapper = new BasicEvaluationMapper();
    private final CompleteEvaluationMapper completeEvalMapper = new CompleteEvaluationMapper();
    private final GradeMapper gradeMapper = new GradeMapper();

    private final CityMapper cityMapper = new CityMapper();
    private final RestaurantTypeMapper typeMapper = new RestaurantTypeMapper();
    private final EvaluationCriteriaMapper critMapper = new EvaluationCriteriaMapper();

    // -------- LECTURE --------
    @Override public List<Restaurant> getAllRestaurants() throws SQLException { return restaurantMapper.findAll(); }
    @Override public Restaurant getRestaurantById(int id) throws SQLException { return restaurantMapper.findById(id).orElse(null); }
    @Override public List<BasicEvaluation> getLikesByRestaurant(int restId) throws SQLException { return basicEvalMapper.findByRestaurant(restId); }
    @Override public List<CompleteEvaluation> getCompleteEvaluationsByRestaurant(int restId) throws SQLException { return completeEvalMapper.findByRestaurant(restId); }

    @Override public List<City> getCities() throws SQLException { return cityMapper.findAll(); }
    @Override public List<RestaurantType> getTypes() throws SQLException { return typeMapper.findAll(); }
    @Override public List<EvaluationCriteria> getCriterias() throws SQLException { return critMapper.findAll(); }

    // -------- ECRITURE --------
    @Override
    public void addLike(BasicEvaluation like) throws SQLException {
        basicEvalMapper.insert(like);
    }

    @Override
    public void addCompleteEvaluation(CompleteEvaluation eval) throws SQLException {
        try {
            TransactionManager.begin();

            // 1) insérer le commentaire
            completeEvalMapper.insertCore(eval);

            // 2) insérer les notes si présentes
            if (eval.getGrades() != null && !eval.getGrades().isEmpty()) {
                gradeMapper.insertAllForEvaluation(eval.getId(), new ArrayList<>(eval.getGrades()));
            }

            TransactionManager.commit();
        } catch (SQLException e) {
            TransactionManager.rollback();
            throw e;
        }
    }
    @Override
    public void addRestaurant(Restaurant restaurant) throws SQLException {
        throw new UnsupportedOperationException("Not implemented in JDBC service (use GuideRestoServiceJpaImpl)");
    }
}