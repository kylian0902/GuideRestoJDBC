package ch.hearc.ig.guideresto.persistence.jpa.mapper;

import ch.hearc.ig.guideresto.business.BasicEvaluation;

import java.util.List;

public class BasicEvaluationMapperJpa extends AbstractJpaMapper<BasicEvaluation, Integer> {

    @Override
    protected Class<BasicEvaluation> entityClass() {
        return BasicEvaluation.class;
    }

    public List<BasicEvaluation> findByRestaurantId(int restId) {
        return namedList("BasicEvaluation.findByRestaurantId", "restId", restId);
    }

    public List<BasicEvaluation> findByRestaurantIdAndValue(int restId, boolean val) {
        return namedList2("BasicEvaluation.findByRestaurantIdAndValue", "restId", restId, "val", val);
    }
}