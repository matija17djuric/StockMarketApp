package rs.teletrader.stock_marketplace.Models.Stocks.Order;

import java.util.List;

import lombok.Data;

@Data
public class OrderBookModel {

    List<OrderModel> sellingOrders;
    List<OrderModel> buyingOrders;
}
