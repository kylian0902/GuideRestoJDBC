package ch.hearc.ig.guideresto.business;

import ch.hearc.ig.guideresto.persistence.jpa.converters.TFBooleanConverter;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Date;

/**
 * @author cedric.baudet
 */

@Entity
@NamedQueries({
        @NamedQuery(
                name = "BasicEvaluation.findById",
                query = "select b from BasicEvaluation b where b.id = :id"
        ),
        @NamedQuery(
                name = "BasicEvaluation.findByRestaurantId",
                query = """
          select b from BasicEvaluation b
          where b.restaurant.id = :restId
          order by b.visitDate desc
      """
        ),
        @NamedQuery(
                name = "BasicEvaluation.findByRestaurantIdAndValue",
                query = """
          select b from BasicEvaluation b
          where b.restaurant.id = :restId and b.likeRestaurant = :val
          order by b.visitDate desc
      """
        )
})
@Table(name="LIKES")
public class BasicEvaluation extends Evaluation {

    @Convert(converter = TFBooleanConverter.class)
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "appreciation", nullable = false, length = 1, columnDefinition = "CHAR(1)")
    private Boolean likeRestaurant;
    @Column(name="adresse_ip")
    private String ipAddress;

    public BasicEvaluation() {
        this(null, null, null, null);
    }

    public BasicEvaluation(Date visitDate, Restaurant restaurant, Boolean likeRestaurant, String ipAddress) {
        this(null, visitDate, restaurant, likeRestaurant, ipAddress);
    }

    public BasicEvaluation(Integer id, Date visitDate, Restaurant restaurant, Boolean likeRestaurant, String ipAddress) {
        super(id, visitDate, restaurant);
        this.likeRestaurant = likeRestaurant;
        this.ipAddress = ipAddress;
    }

    public Boolean getLikeRestaurant() {
        return likeRestaurant;
    }

    public void setLikeRestaurant(Boolean likeRestaurant) {
        this.likeRestaurant = likeRestaurant;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

}