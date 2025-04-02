package rs.teletrader.stock_marketplace.Services.Stock.Company;

import rs.teletrader.stock_marketplace.Models.Stocks.Company.CompanyModel;

public interface ICompanyService {
    CompanyModel createNewCompany(CompanyModel companyModel);

    CompanyModel findCompanyById(CompanyModel company);
}
