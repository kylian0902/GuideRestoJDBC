package ch.hearc.ig.guideresto.business;

/**
 * @author cedric.baudet
 */

import jakarta.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@NamedQueries({
        @NamedQuery(
                name = "CompleteEvaluation.findById",
                query = "select c from CompleteEvaluation c where c.id = :id"
        ),
        @NamedQuery(
                name = "CompleteEvaluation.findByRestaurantId",
                query = """
          select c from CompleteEvaluation c
          where c.restaurant.id = :restId
          order by c.visitDate desc
      """
        ),
        @NamedQuery(
                name = "CompleteEvaluation.findByUsername",
                query = """
          select c from CompleteEvaluation c
          where c.username = :username
          order by c.visitDate desc
      """
        )
})
@Table(name="COMMENTAIRES")
public class CompleteEvaluation extends Evaluation {

    @Lob
    @Column(name="commentaire", nullable=false)
    private String comment;
    @Column(name="nom_utilisateur", nullable=false)
    private String username;
    @OneToMany(mappedBy="evaluation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Grade> grades;

    public CompleteEvaluation() {
        this(null, null, null, null);
    }

    public CompleteEvaluation(Date visitDate, Restaurant restaurant, String comment, String username) {
        this(null, visitDate, restaurant, comment, username);
    }

    public CompleteEvaluation(Integer id, Date visitDate, Restaurant restaurant, String comment, String username) {
        super(id, visitDate, restaurant);
        this.comment = comment;
        this.username = username;
        this.grades = new HashSet();
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<Grade> getGrades() {
        return grades;
    }

    public void setGrades(Set<Grade> grades) {
        this.grades = grades;
    }
}