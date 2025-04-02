package rs.teletrader.stock_marketplace.Services.Stock.OrderService;

import java.util.List;

import rs.teletrader.stock_marketplace.Models.Stocks.Order.OrderBookModel;
import rs.teletrader.stock_marketplace.Models.Stocks.Order.OrderModel;

public interface IOrderService {
    OrderModel findById(Integer id);

    OrderModel placeNewOrder(OrderModel orderModel);

    List<OrderModel> findAllOpenByCompanyId(Integer id);

    List<OrderModel> findAllOpenBuyingByCompanyId(Integer id);

    List<OrderModel> findAllOpenSellingByCompanyId(Integer id);

    void closeOrders(OrderModel sellingModel, OrderModel orderModel);

    OrderBookModel getGlobalOrderBook();

    OrderModel cancelOrder(OrderModel order);
}
