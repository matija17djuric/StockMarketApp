package rs.teletrader.stock_marketplace.Repositories.Stock.Share;

import org.springframework.data.jpa.repository.JpaRepository;

import rs.teletrader.stock_marketplace.Models.Stocks.Share.ShareModel;

public interface IShareRepository extends JpaRepository<ShareModel, Integer> {

    ShareModel findByUser_IdAndCompany_Id(Integer userId, Integer companyId);

}
