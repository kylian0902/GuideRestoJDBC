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
                name = "City.findAll",
                query = "select c from City c order by c.cityName"
        ),
        @NamedQuery(
                name = "City.findById",
                query = "select c from City c where c.id = :id"
        ),
        @NamedQuery(
                name = "City.findByZipAndName",
                query = "select c from City c where c.zipCode = :zip and c.cityName = :name"
        ),
        @NamedQuery(
                name = "City.findByZip",
                query = "select c from City c where c.zipCode = :zip order by c.cityName"
        ),
        @NamedQuery(
                name = "City.findByName",
                query = "select c from City c where c.cityName = :name"
        )
})
@Table(name="VILLES")
public class City implements IBusinessObject {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ville_seq")
    @SequenceGenerator(
            name = "ville_seq",
            sequenceName = "SEQ_VILLES",
            allocationSize = 1
    )
    @Column(name="numero")
    private Integer id;
    @Column(name="code_postal")
    private String zipCode;
    @Column(name="nom_ville")
    private String cityName;
    @Transient
    private Set<Restaurant> restaurants;

    public City() {
        this(null, null);
    }

    public City(String zipCode, String cityName) {
        this(null, zipCode, cityName);
    }

    public City(Integer id, String zipCode, String cityName) {
        this.id = id;
        this.zipCode = zipCode;
        this.cityName = cityName;
        this.restaurants = new HashSet();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String city) {
        this.cityName = city;
    }

    public Set<Restaurant> getRestaurants() {
        return restaurants;
    }

    public void setRestaurants(Set<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }

}