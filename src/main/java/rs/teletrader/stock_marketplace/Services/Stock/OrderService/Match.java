package rs.teletrader.stock_marketplace.Services.Stock.OrderService;

import java.util.ArrayList;
import java.util.List;

import rs.teletrader.stock_marketplace.Models.Stocks.Order.OrderModel;

class Match {
    OrderModel sellingModel;
    List<OrderModel> buyingModels;

    Match() {
        buyingModels = new ArrayList<>();
    }

    public OrderModel getSellingModel() {
        return sellingModel;
    }

    public void setSellingModel(OrderModel sellingModel) {
        this.sellingModel = sellingModel;
    }

    public List<OrderModel> getBuyingModels() {
        return buyingModels;
    }

    public void setBuyingModels(List<OrderModel> buyingModels) {
        this.buyingModels = buyingModels;
    }

}
