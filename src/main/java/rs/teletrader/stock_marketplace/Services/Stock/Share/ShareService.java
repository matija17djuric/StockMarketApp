package rs.teletrader.stock_marketplace.Services.Stock.Share;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.teletrader.stock_marketplace.Models.Stocks.Share.ShareModel;
import rs.teletrader.stock_marketplace.Repositories.Stock.Share.IShareRepository;

@Service
public class ShareService implements IShareService{

    @Autowired
    IShareRepository iShareRepository;

    @Override
    public ShareModel addNewShare(ShareModel shareModel) {
        return iShareRepository.save(shareModel);
    }

    

}
