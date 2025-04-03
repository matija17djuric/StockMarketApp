package rs.teletrader.stock_marketplace.Services.Stock.OrderService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import rs.teletrader.stock_marketplace.Configuration.WebSocket.OrdersWebsocketHandler;
import rs.teletrader.stock_marketplace.Configuration.WebSocket.SessionRoom;
import rs.teletrader.stock_marketplace.Models.Stocks.Company.CompanyModel;
import rs.teletrader.stock_marketplace.Models.Stocks.Order.OrderBookModel;
import rs.teletrader.stock_marketplace.Models.Stocks.Order.OrderModel;
import rs.teletrader.stock_marketplace.Models.Stocks.Order.OrderModel.OrderOption;
import rs.teletrader.stock_marketplace.Models.Stocks.Order.OrderModel.OrderStatus;
import rs.teletrader.stock_marketplace.Models.Stocks.Share.ShareModel;
import rs.teletrader.stock_marketplace.Repositories.Stock.Order.IOrderRepository;
import rs.teletrader.stock_marketplace.Repositories.Stock.Share.IShareRepository;

@Service
public class OrderService implements IOrderService {

    @Autowired
    IOrderRepository iOrderRepository;

    @Autowired
    IShareRepository iShareRepository;

    @Autowired
    OrdersWebsocketHandler ordersWebsocketHandler;

    private final List<Integer> matchingSessions = new ArrayList();
    private final Map<Integer, Semaphore> orderSemaphores = new HashMap<>();

    public void startMatching(SessionRoom sessionRoom, CompanyModel company) {
        if (!matchingSessions.contains(company.getId())) {
            matchingSessions.add(company.getId());
            new Thread(new OrderMatcherRunnable(company)).start();
        }
    }

    public void finishMatching(Integer idCompany) {
        if (matchingSessions.contains(idCompany)) {
            matchingSessions.remove(idCompany);
        }
    }

    public class OrderMatcherRunnable implements Runnable {

        CompanyModel company;
        List<OrderModel> orders;

        OrderMatcherRunnable(CompanyModel company) {
            this.company = company;
            orders = findAllOpenByCompanyId(company.getId());
        }

        @Override
        public void run() {
            while (!orders.isEmpty()) {
                try {
                    Thread.sleep(300);
                    List<OrderModel> buyingOrders = findAllOpenBuyingByCompanyId(company.getId());
                    List<OrderModel> sellingOrders = findAllOpenSellingByCompanyId(company.getId());
                    for (int i = 0; i < sellingOrders.size(); i++) {
                        Match match = new Match();
                        match.setSellingModel(sellingOrders.get(i));
                        for (int j = 0; j < buyingOrders.size(); j++) {
                            if (buyingOrders.get(i).getBuyingPrice().equals(match.getSellingModel().getSellingPrice())
                                    && buyingOrders.get(i).getUser().getId() != match.getSellingModel().getUser()
                                            .getId()) {
                                match.getBuyingModels().add(buyingOrders.get(i));
                            }
                        }
                        try {
                            if (match.buyingModels.size() == 1) {
                                Semaphore buyingSemaphore = orderSemaphores.get(match.getBuyingModels().get(0).getId()),
                                        sellingSemaphore = orderSemaphores.get(match.getSellingModel().getId());
                                if (buyingSemaphore.tryAcquire() && sellingSemaphore.tryAcquire()) {
                                    OrderModel buyingOrder = match.getBuyingModels().get(0),
                                            sellingOrder = match.getSellingModel();
                                    orderTransaction(buyingOrder.getId(), sellingOrder.getId());
                                    buyingSemaphore.release();
                                    sellingSemaphore.release();
                                }
                            } else if (match.buyingModels.size() > 1) {
                                Boolean allSemaphoresAcquired = true;
                                List<Semaphore> acquiredSemaphores = new ArrayList<>();
                                for (int s = 0; s < match.buyingModels.size(); s++) {
                                    Semaphore currentSemaphore = orderSemaphores.get(match.buyingModels.get(s).getId());
                                    if (currentSemaphore.tryAcquire()) {
                                        acquiredSemaphores.add(currentSemaphore);
                                    } else {
                                        allSemaphoresAcquired = false;
                                    }
                                }
                                if (allSemaphoresAcquired) {
                                    Integer sumOfAskedShares = 0;
                                    for (OrderModel orderModel : match.getBuyingModels()) {
                                        sumOfAskedShares += orderModel.getQuantity();
                                    }
                                    if (sumOfAskedShares <= match.getSellingModel().getQuantity()) {
                                        for (OrderModel orderModel : match.getBuyingModels()) {
                                            orderTransaction(orderModel.getId(), match.getSellingModel().getId());
                                        }
                                    } else {
                                        Float piece = Float.valueOf(match.getSellingModel().getQuantity())
                                                / Float.valueOf(sumOfAskedShares);
                                        for (OrderModel orderModel : match.getBuyingModels()) {
                                            Integer quantityPerTransaction = (int) Math
                                                    .floor(piece * orderModel.getQuantity());
                                            orderTransaction(orderModel.getId(), match.getSellingModel().getId(),
                                                    quantityPerTransaction);
                                        }
                                    }

                                    for (Semaphore semaphore : acquiredSemaphores) {
                                        semaphore.release();
                                    }
                                } else {
                                    for (Semaphore semaphore : acquiredSemaphores) {
                                        semaphore.release();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    orders = findAllOpenByCompanyId(company.getId());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finishMatching(company.getId());
            }
        }

    }

    @Override
    public OrderModel placeNewOrder(OrderModel orderModel) {

        try {
            if (orderModel.getOrderOption().equals(OrderOption.sell)) {
                ShareModel share = null;
                for (ShareModel shareModel : orderModel.getUser().getShares()) {
                    if (shareModel.getCompany().getId().equals(orderModel.getCompany().getId()))
                        share = shareModel;
                }
                if (share != null && share.getSharesCount() >= orderModel.getQuantity()
                        && orderModel.getSellingPrice() != null) {

                    orderModel.setDatePlaced(new Date());
                    orderModel.setStatus(OrderStatus.open);
                    share.setSharesCount(share.getSharesCount() - orderModel.getQuantity());
                    iShareRepository.save(share);
                    orderModel = iOrderRepository.save(orderModel);
                    Semaphore orderSemaphore = new Semaphore(1);
                    orderSemaphores.put(orderModel.getId(), orderSemaphore);
                    startMatching(ordersWebsocketHandler.getRoom(orderModel.getCompany().getId()),
                            orderModel.getCompany());
                    return orderModel;
                } else
                    return null;
            } else if (orderModel.getOrderOption().equals(OrderOption.buy) && orderModel.getBuyingPrice() != null) {
                orderModel.setDatePlaced(new Date());
                orderModel.setStatus(OrderStatus.open);
                orderModel = iOrderRepository.save(orderModel);
                Semaphore orderSemaphore = new Semaphore(1);
                orderSemaphores.put(orderModel.getId(), orderSemaphore);
                startMatching(ordersWebsocketHandler.getRoom(orderModel.getCompany().getId()),
                        orderModel.getCompany());
                return orderModel;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public OrderModel findById(Integer id) {
        return iOrderRepository.findById(id).get();
    }

    @Override
    public List<OrderModel> findAllOpenByCompanyId(Integer id) {
        return iOrderRepository.findAllByCompany_IdAndStatus(id, OrderStatus.open);
    }

    @Override
    public List<OrderModel> findAllOpenBuyingByCompanyId(Integer id) {
        return iOrderRepository.findAllByCompany_IdAndStatusAndOrderOption(id, OrderStatus.open, OrderOption.buy);
    }

    @Override
    public List<OrderModel> findAllOpenSellingByCompanyId(Integer id) {
        return iOrderRepository.findAllByCompany_IdAndStatusAndOrderOption(id, OrderStatus.open, OrderOption.sell);
    }

    @Override
    @Transactional
    public void orderTransaction(Integer buyingOrderId, Integer sellingOrderId) {

        OrderModel buyingOrder = iOrderRepository.findById(buyingOrderId).get();
        OrderModel sellingOrder = iOrderRepository.findById(sellingOrderId).get();

        Integer availableShares = sellingOrder.getQuantity(), askingShares = buyingOrder.getQuantity(),
                transactionShares = 0;

        if (availableShares >= askingShares) {
            transactionShares = askingShares;
        } else if (availableShares < askingShares && availableShares > 0) {
            transactionShares = availableShares;
        }

        buyingOrder = iOrderRepository.findById(buyingOrder.getId()).get();
        buyingOrder.setQuantity(buyingOrder.getQuantity() - transactionShares);
        buyingOrder = iOrderRepository.save(buyingOrder);

        sellingOrder = iOrderRepository.findById(sellingOrder.getId()).get();
        sellingOrder.setQuantity(sellingOrder.getQuantity() - transactionShares);
        sellingOrder = iOrderRepository.save(sellingOrder);

        ShareModel buyersShares = iShareRepository.findByUser_IdAndCompany_Id(buyingOrder.getUser().getId(),
                buyingOrder.getCompany().getId());

        if (buyersShares == null) {
            buyersShares = new ShareModel();
            buyersShares.setCompany(buyingOrder.getCompany());
            buyersShares.setPrice(buyingOrder.getBuyingPrice());
            buyersShares.setSharesCount(transactionShares);
            buyersShares.setUser(buyingOrder.getUser());
            buyersShares = iShareRepository.save(buyersShares);
        } else {
            buyersShares.setSharesCount(buyersShares.getSharesCount() + transactionShares);
            buyersShares.setPrice(buyingOrder.getBuyingPrice());
            buyersShares = iShareRepository.save(buyersShares);
        }

        iOrderRepository.flush();

        ordersWebsocketHandler.sendMessagetoUser(buyingOrder.getCompany().getId(), buyingOrder.getUser().getId(),
                "Bought " + transactionShares + " shares");
        ordersWebsocketHandler.sendMessagetoUser(sellingOrder.getCompany().getId(), sellingOrder.getUser().getId(),
                "Sold " + transactionShares + " shares");

        if (buyingOrder.getQuantity() == 0)
            closeOrder(buyingOrder.getId());

        if (sellingOrder.getQuantity() == 0)
            closeOrder(sellingOrder.getId());
    }

    @Override
    @Transactional
    public void orderTransaction(Integer buyingOrderId, Integer sellingOrderId, Integer quantity) {

        OrderModel buyingOrder = iOrderRepository.findById(buyingOrderId).get();
        OrderModel sellingOrder = iOrderRepository.findById(sellingOrderId).get();

        buyingOrder = iOrderRepository.findById(buyingOrder.getId()).get();
        buyingOrder.setQuantity(buyingOrder.getQuantity() - quantity);
        buyingOrder = iOrderRepository.save(buyingOrder);

        sellingOrder = iOrderRepository.findById(sellingOrder.getId()).get();
        sellingOrder.setQuantity(sellingOrder.getQuantity() - quantity);
        sellingOrder = iOrderRepository.save(sellingOrder);

        ShareModel buyersShares = iShareRepository.findByUser_IdAndCompany_Id(buyingOrder.getUser().getId(),
                buyingOrder.getCompany().getId());

        if (buyersShares == null) {
            buyersShares = new ShareModel();
            buyersShares.setCompany(buyingOrder.getCompany());
            buyersShares.setPrice(buyingOrder.getBuyingPrice());
            buyersShares.setSharesCount(quantity);
            buyersShares.setUser(buyingOrder.getUser());
            buyersShares = iShareRepository.save(buyersShares);
        } else {
            buyersShares.setSharesCount(buyersShares.getSharesCount() + quantity);
            buyersShares.setPrice(buyingOrder.getBuyingPrice());
            buyersShares = iShareRepository.save(buyersShares);
        }

        iOrderRepository.flush();

        ordersWebsocketHandler.sendMessagetoUser(buyingOrder.getCompany().getId(), buyingOrder.getUser().getId(),
                "Bought " + quantity + " shares");
        ordersWebsocketHandler.sendMessagetoUser(sellingOrder.getCompany().getId(), sellingOrder.getUser().getId(),
                "Sold " + quantity + " shares");

        if (buyingOrder.getQuantity() == 0)
            closeOrder(buyingOrder.getId());

        if (sellingOrder.getQuantity() == 0)
            closeOrder(sellingOrder.getId());
    }

    @Override
    @Transactional
    public void closeOrder(Integer orderId) {
        OrderModel orderModel = iOrderRepository.findById(orderId).get();
        orderModel.setStatus(OrderStatus.closed);
        orderModel = iOrderRepository.save(orderModel);
        orderSemaphores.remove(orderModel.getId());
        ordersWebsocketHandler.sendMessagetoUser(orderModel.getCompany().getId(), orderModel.getUser().getId(),
                "Order closed");
        ordersWebsocketHandler.disconnectUser(orderModel.getCompany().getId(), orderModel.getUser().getId());
    }

    @Override
    public OrderBookModel getGlobalOrderBook() {
        OrderBookModel orderBookModel = new OrderBookModel();
        orderBookModel
                .setSellingOrders(
                        iOrderRepository.findTop10ByOrderOptionAndStatusOrderBySellingPriceAsc(OrderOption.sell,
                                OrderStatus.open));
        orderBookModel
                .setBuyingOrders(
                        iOrderRepository.findTop10ByOrderOptionAndStatusOrderByBuyingPriceDesc(OrderOption.buy,
                                OrderStatus.open));
        return orderBookModel;
    }

    @Override
    public OrderModel cancelOrder(OrderModel order) {
        OrderModel dbOrder = iOrderRepository.findById(order.getId()).get();
        if (dbOrder.getUser().getId().equals(order.getUser().getId())) {
            if (dbOrder.getOrderOption().equals(OrderOption.sell)) {
                ShareModel shareModel = dbOrder.getShare();
                shareModel.setSharesCount(shareModel.getSharesCount() + order.getQuantity());
                iShareRepository.save(shareModel);
            }
            dbOrder.setStatus(OrderStatus.cancelled);
            return iOrderRepository.save(dbOrder);
        } else
            return null;
    }

    @Override
    public List<OrderModel> getOpenOrdersForCompany(Integer idCompany) {
        return iOrderRepository.findAllByCompany_IdAndStatus(idCompany, OrderStatus.open);
    }

}
