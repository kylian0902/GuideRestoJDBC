package ch.hearc.ig.guideresto.persistence.jpa.mapper;

import ch.hearc.ig.guideresto.business.Grade;

import java.util.List;

public class GradeMapperJpa extends AbstractJpaMapper<Grade, Integer> {

    @Override
    protected Class<Grade> entityClass() {
        return Grade.class;
    }

    public List<Grade> findByEvaluationId(int evalId) {
        return namedList("Grade.findByEvaluationId", "evalId", evalId);
    }

    public Grade findByEvaluationIdAndCriteriaId(int evalId, int critId) {
        return namedSingleOrNull2("Grade.findByEvaluationIdAndCriteriaId", "evalId", evalId, "critId", critId);
    }
}