package rs.teletrader.stock_marketplace.Models.Stocks.Share;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import rs.teletrader.stock_marketplace.Models.Stocks.Company.CompanyModel;
import rs.teletrader.stock_marketplace.Models.User.UserModel;

@Getter
@Setter
@Entity
@Table(name = "share")
public class ShareModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_share", columnDefinition = "INT UNSIGNED")
    Integer id;

    @ManyToOne
    @JoinColumn(name = "id_company", columnDefinition = "INT UNSIGNED")
    @JsonBackReference("company-share")
    CompanyModel company;

    @ManyToOne
    @JoinColumn(name = "id_user", columnDefinition = "INT UNSIGNED")
    @JsonBackReference(value = "share-user")
    UserModel user;

    @Column(name = "shares_count")
    Integer sharesCount;

    @Column(name = "price")
    Double price;
}
