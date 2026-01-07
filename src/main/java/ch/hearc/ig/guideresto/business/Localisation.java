package ch.hearc.ig.guideresto.business;

import jakarta.persistence.*;

/**
 * @author cedric.baudet
 */
@Embeddable
public class Localisation {

    @Column(name = "adresse", nullable = false)
    private String street;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_vill", nullable = false)
    private City city;

    public Localisation() {
        this(null, null);
    }

    public Localisation(String street, City city) {
        this.street = street;
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}