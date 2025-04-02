package rs.teletrader.stock_marketplace.Models.Stocks.Order;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
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
import rs.teletrader.stock_marketplace.Models.Stocks.Share.ShareModel;
import rs.teletrader.stock_marketplace.Models.User.UserModel;

@Getter
@Setter
@Entity
@Table(name = "\"order\"")
public class OrderModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_order", columnDefinition = "INT UNSIGNED")
    Integer id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_user", columnDefinition = "INT UNSIGNED")
    UserModel user;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_share", columnDefinition = "INT UNSIGNED")
    ShareModel share;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_company", columnDefinition = "INT UNSIGNED")
    CompanyModel company;

    @Column(name = "order_option")
    OrderOption orderOption;

    @Column(name = "selling_price")
    Double sellingPrice;

    @Column(name = "buying_price")
    Double buyingPrice;

    @Column(name = "status")
    OrderStatus status;

    @Column(name = "date_placed")
    Date datePlaced;

    @Column(name = "quantity")
    Integer quantity;

    public enum OrderStatus {
        open,
        closed,
        cancelled
    }

    public enum OrderOption {
        buy,
        sell
    }
}
