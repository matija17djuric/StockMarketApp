package rs.teletrader.stock_marketplace.Repositories.Stock.Order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import rs.teletrader.stock_marketplace.Models.Stocks.Order.OrderModel;
import rs.teletrader.stock_marketplace.Models.Stocks.Order.OrderModel.OrderOption;
import rs.teletrader.stock_marketplace.Models.Stocks.Order.OrderModel.OrderStatus;

public interface IOrderRepository extends JpaRepository<OrderModel, Integer> {
        public List<OrderModel> findAllByCompany_IdAndStatus(Integer id, OrderStatus status);

        public List<OrderModel> findAllByCompany_IdAndStatusAndOrderOption(Integer id, OrderStatus status,
                        OrderOption option);

        public List<OrderModel> findTop10ByOrderOptionAndStatusOrderBySellingPriceAsc(OrderOption orderOption,
                        OrderStatus orderStatus);

        public List<OrderModel> findTop10ByOrderOptionAndStatusOrderByBuyingPriceDesc(OrderOption orderOption,
                        OrderStatus orderStatus);

        public List<OrderModel> findAllByCompany_idAndStatus(Integer id, OrderStatus staus);
}
