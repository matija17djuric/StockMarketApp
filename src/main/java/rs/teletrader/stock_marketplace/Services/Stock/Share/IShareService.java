package rs.teletrader.stock_marketplace.Services.Stock.Share;

import rs.teletrader.stock_marketplace.Models.Stocks.Share.ShareModel;

public interface IShareService {
    ShareModel addNewShare(ShareModel shareModel);
}
