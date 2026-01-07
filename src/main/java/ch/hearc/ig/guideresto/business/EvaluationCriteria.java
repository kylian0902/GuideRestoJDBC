package ch.hearc.ig.guideresto.business;

import jakarta.persistence.Entity;
import jakarta.persistence.*;

/**
 * @author cedric.baudet
 */
@Entity
@NamedQueries({
        @NamedQuery(
                name = "EvaluationCriteria.findAll",
                query = "select c from EvaluationCriteria c order by c.id"
        ),
        @NamedQuery(
                name = "EvaluationCriteria.findById",
                query = "select c from EvaluationCriteria c where c.id = :id"
        ),
        @NamedQuery(
                name = "EvaluationCriteria.findByName",
                query = "select c from EvaluationCriteria c where c.name = :name"
        )
})
@Table(name = "CRITERES_EVALUATION")
public class EvaluationCriteria implements IBusinessObject {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_crit")
    @SequenceGenerator(name = "seq_crit", sequenceName = "SEQ_CRITERES_EVALUATION", allocationSize = 1)
    @Column(name = "NUMERO")
    private Integer id;
    @Column(name = "NOM")
    private String name;
    @Column(name = "DESCRIPTION")
    private String description;

    public EvaluationCriteria() {
        this(null, null);
    }

    public EvaluationCriteria(String name, String description) {
        this(null, name, description);
    }

    public EvaluationCriteria(Integer id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}