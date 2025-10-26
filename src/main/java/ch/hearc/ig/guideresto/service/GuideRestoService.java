package ch.hearc.ig.guideresto.service;

import ch.hearc.ig.guideresto.business.*;

import java.sql.SQLException;
import java.util.List;

public interface GuideRestoService {

    // Lecture
    List<Restaurant> getAllRestaurants() throws SQLException;
    Restaurant getRestaurantById(int id) throws SQLException;
    List<BasicEvaluation> getLikesByRestaurant(int restId) throws SQLException;
    List<CompleteEvaluation> getCompleteEvaluationsByRestaurant(int restId) throws SQLException;

    List<City> getCities() throws SQLException;
    List<RestaurantType> getTypes() throws SQLException;
    List<EvaluationCriteria> getCriterias() throws SQLException;

    // Écriture
    void addLike(BasicEvaluation like) throws SQLException;
    void addCompleteEvaluation(CompleteEvaluation eval) throws SQLException;
}