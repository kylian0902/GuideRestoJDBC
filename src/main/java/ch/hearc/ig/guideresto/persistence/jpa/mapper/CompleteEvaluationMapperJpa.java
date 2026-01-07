package ch.hearc.ig.guideresto.persistence.jpa.mapper;

import ch.hearc.ig.guideresto.business.CompleteEvaluation;

import java.util.List;

public class CompleteEvaluationMapperJpa extends AbstractJpaMapper<CompleteEvaluation, Integer> {

    @Override
    protected Class<CompleteEvaluation> entityClass() {
        return CompleteEvaluation.class;
    }

    public List<CompleteEvaluation> findByRestaurantId(int restId) {
        return namedList("CompleteEvaluation.findByRestaurantId", "restId", restId);
    }

    public List<CompleteEvaluation> findByUsername(String username) {
        return namedList("CompleteEvaluation.findByUsername", "username", username);
    }
}