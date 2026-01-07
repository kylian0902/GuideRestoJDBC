package ch.hearc.ig.guideresto.business;

import jakarta.persistence.*;
import java.util.Date;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;

/**
 * @author cedric.baudet
 */
@Entity
@NamedQueries({
        @NamedQuery(
                name = "Evaluation.findAll",
                query = "select e from Evaluation e order by e.visitDate desc"
        ),
        @NamedQuery(
                name = "Evaluation.findById",
                query = "select e from Evaluation e where e.id = :id"
        ),
        @NamedQuery(
                name = "Evaluation.findByRestaurantId",
                query = """
          select e from Evaluation e
          where e.restaurant.id = :restId
          order by e.visitDate desc
      """
        ),
})
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Evaluation implements IBusinessObject {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "eval_seq")
    @SequenceGenerator(name="eval_seq", sequenceName="SEQ_EVAL", allocationSize=1)
    @Column(name="numero")
    private Integer id;
    @Temporal(TemporalType.DATE) // ou TIMESTAMP si tu veux garder l’heure
    @Column(name="date_eval", nullable=false)
    private Date visitDate;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="fk_rest", nullable=false)
    private Restaurant restaurant;

    public Evaluation() {
        this(null, null, null);
    }

    public Evaluation(Integer id, Date visitDate, Restaurant restaurant) {
        this.id = id;
        this.visitDate = visitDate;
        this.restaurant = restaurant;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(Date visitDate) {
        this.visitDate = visitDate;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

}