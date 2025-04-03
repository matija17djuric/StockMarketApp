package rs.teletrader.stock_marketplace.Services.Stock.Company;

import java.util.List;
import rs.teletrader.stock_marketplace.Models.Stocks.Company.CompanyModel;

public interface ICompanyService {
    CompanyModel createNewCompany(CompanyModel companyModel);

    CompanyModel findCompanyById(Integer companyId);

    List<CompanyModel> findAllCompanies();
}
