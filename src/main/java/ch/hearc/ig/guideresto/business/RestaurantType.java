package ch.hearc.ig.guideresto.business;

import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.*;

/**
 * @author cedric.baudet
 */
@Entity
@NamedQueries({
        @NamedQuery(
                name = "RestaurantType.findAll",
                query = "select t from RestaurantType t order by t.label"
        ),
        @NamedQuery(
                name = "RestaurantType.findById",
                query = "select t from RestaurantType t where t.id = :id"
        ),
        @NamedQuery(
                name = "RestaurantType.findByLabel",
                query = "select t from RestaurantType t where t.label = :label"
        )
})
@Table(name="TYPES_GASTRONOMIQUES")
public class RestaurantType implements IBusinessObject {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "type_gastro_seq")
    @SequenceGenerator(
            name = "type_gastro_seq",
            sequenceName = "SEQ_TYPES_GASTRONOMIQUES",
            allocationSize = 1
    )
    @Column(name="numero")
    private Integer id;
    @Column(name="libelle")
    private String label;
    @Lob
    @Column(name="description")
    private String description;
    @OneToMany(mappedBy = "type", fetch = FetchType.LAZY)
    private Set<Restaurant> restaurants;

    public RestaurantType() {
        this(null, null);
    }

    public RestaurantType(String label, String description) {
        this(null, label, description);
    }

    public RestaurantType(Integer id, String label, String description) {
        this.restaurants = new HashSet();
        this.id = id;
        this.label = label;
        this.description = description;
    }

    @Override
    public String toString() {
        return label;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Restaurant> getRestaurants() {
        return restaurants;
    }

    public void setRestaurants(Set<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }

}