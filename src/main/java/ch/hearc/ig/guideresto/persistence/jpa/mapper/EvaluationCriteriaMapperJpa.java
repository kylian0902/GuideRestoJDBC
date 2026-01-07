package ch.hearc.ig.guideresto.persistence.jpa.mapper;

import ch.hearc.ig.guideresto.business.EvaluationCriteria;

import java.util.List;

public class EvaluationCriteriaMapperJpa extends AbstractJpaMapper<EvaluationCriteria, Integer> {

    @Override
    protected Class<EvaluationCriteria> entityClass() {
        return EvaluationCriteria.class;
    }

    public List<EvaluationCriteria> findAll() {
        return namedList("EvaluationCriteria.findAll");
    }

    public EvaluationCriteria findByName(String name) {
        return namedSingleOrNull("EvaluationCriteria.findByName", "name", name);
    }
}