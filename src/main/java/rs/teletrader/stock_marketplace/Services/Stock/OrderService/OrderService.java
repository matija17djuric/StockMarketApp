package rs.teletrader.stock_marketplace.Services.Stock.OrderService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    List<Integer> matchingSessions = new ArrayList();

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
                            if (buyingOrders.get(i).getBuyingPrice()
                                    .equals(match.getSellingModel().getSellingPrice())) {
                                match.getBuyingModels().add(buyingOrders.get(i));
                            }
                        }
                        if (match.buyingModels.size() == 1) {
                            closeOrders(match.getSellingModel(), match.getBuyingModels().get(0));
                        } else if (match.buyingModels.size() > 1) {
                            List<OrderModel> sortedOrders = match.getBuyingModels().stream()
                                    .sorted((o1, o2) -> o1.getQuantity().compareTo(o2.getQuantity()))
                                    .collect(Collectors.toList());
                            if (sortedOrders.get(0).getBuyingPrice() > sortedOrders.get(1).getBuyingPrice()) {
                                closeOrders(match.getSellingModel(), sortedOrders.get(0));
                            } else if (sortedOrders.get(0).getBuyingPrice() > sortedOrders.get(1).getBuyingPrice()) {
                                closeOrders(match.getSellingModel(), sortedOrders.get(0));
                            } else {
                                if (sortedOrders.get(0).getDatePlaced().before(sortedOrders.get(1).getDatePlaced())) {
                                    closeOrders(match.sellingModel, sortedOrders.get(0));
                                } else {
                                    closeOrders(match.sellingModel, sortedOrders.get(1));
                                }
                            }
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
            ShareModel share = null;
            for (ShareModel shareModel : orderModel.getUser().getShares()) {
                if (shareModel.getCompany().getId().equals(orderModel.getCompany().getId()))
                    share = shareModel;
            }
            if ((share != null && share.getSharesCount() >= orderModel.getQuantity())
                    || orderModel.getOrderOption().equals(OrderOption.buy)) {
                orderModel.setDatePlaced(new Date());
                orderModel.setStatus(OrderStatus.open);
                orderModel = iOrderRepository.save(orderModel);
                startMatching(ordersWebsocketHandler.getRoom(orderModel.getCompany().getId()), orderModel.getCompany());
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
    public void closeOrders(OrderModel sellingModel, OrderModel orderModel) {
        ShareModel sellersShare = sellingModel.getUser().getShares().stream()
                .filter(s -> s.getCompany().getId().equals(sellingModel.getCompany().getId())).findFirst().get();
        sellersShare.setSharesCount(sellersShare.getSharesCount() - sellingModel.getQuantity());
        sellersShare.setPrice(sellingModel.getSellingPrice());
        if (sellersShare.equals(0)) {
            iShareRepository.delete(sellersShare);
        } else {
            iShareRepository.save(sellersShare);
        }
        ShareModel buyersShare;
        try {
            buyersShare = orderModel.getUser().getShares().stream()
                    .filter((s) -> s.getCompany().getId().equals(orderModel.getCompany().getId())).findFirst().get();
            buyersShare.setSharesCount(buyersShare.getSharesCount() + orderModel.getQuantity());
            buyersShare.setPrice(orderModel.getBuyingPrice());
        } catch (Exception e) {
            buyersShare = new ShareModel();
            buyersShare.setCompany(orderModel.getCompany());
            buyersShare.setPrice(orderModel.getBuyingPrice());
            buyersShare.setSharesCount(orderModel.getQuantity());
            buyersShare.setUser(orderModel.getUser());
        }
        iShareRepository.save(buyersShare);

        sellingModel.setStatus(OrderStatus.closed);
        orderModel.setStatus(OrderStatus.closed);

        iOrderRepository.save(sellingModel);
        iOrderRepository.save(orderModel);

        ordersWebsocketHandler.informUsers(orderModel.getCompany().getId(), sellingModel.getUser().getId(),
                orderModel.getUser().getId());
    }

    @Override
    public OrderBookModel getGlobalOrderBook() {
        OrderBookModel orderBookModel = new OrderBookModel();
        orderBookModel
                .setSellingOrders(iOrderRepository.findTop10ByOrderOptionOrderBySellingPriceAsc(OrderOption.sell));
        orderBookModel
                .setBuyingOrders(iOrderRepository.findTop10ByOrderOptionOrderByBuyingPriceDesc(OrderOption.buy));
        return orderBookModel;
    }

}
