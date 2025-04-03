package rs.teletrader.stock_marketplace.Services.Stock.OrderService;

import java.util.List;
import java.util.concurrent.Semaphore;

import rs.teletrader.stock_marketplace.Models.Stocks.Order.OrderBookModel;
import rs.teletrader.stock_marketplace.Models.Stocks.Order.OrderModel;

public interface IOrderService {
    OrderModel findById(Integer id);

    OrderModel placeNewOrder(OrderModel orderModel);

    List<OrderModel> findAllOpenByCompanyId(Integer id);

    List<OrderModel> findAllOpenBuyingByCompanyId(Integer id);

    List<OrderModel> findAllOpenSellingByCompanyId(Integer id);

    void orderTransaction(Integer buyingOrderId, Integer sellingOrderId);

    void orderTransaction(Integer buyingOrderId, Integer sellingOrderId, Integer quantity);

    void closeOrder(Integer orderId);

    OrderBookModel getGlobalOrderBook();

    OrderModel cancelOrder(OrderModel order);

    List<OrderModel> getOpenOrdersForCompany(Integer idCompany);
}
