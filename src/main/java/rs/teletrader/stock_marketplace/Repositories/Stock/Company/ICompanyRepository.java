package rs.teletrader.stock_marketplace.Repositories.Stock.Company;

import org.springframework.data.jpa.repository.JpaRepository;

import rs.teletrader.stock_marketplace.Models.Stocks.Company.CompanyModel;

public interface ICompanyRepository extends JpaRepository<CompanyModel,Integer>{

}
