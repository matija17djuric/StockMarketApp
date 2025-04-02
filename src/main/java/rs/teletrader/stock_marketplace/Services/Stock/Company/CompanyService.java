package rs.teletrader.stock_marketplace.Services.Stock.Company;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.teletrader.stock_marketplace.Models.Stocks.Company.CompanyModel;
import rs.teletrader.stock_marketplace.Repositories.Stock.Company.ICompanyRepository;

@Service
public class CompanyService implements ICompanyService{
    
    @Autowired
    ICompanyRepository iCompanyRepository;

    @Override
    public CompanyModel createNewCompany(CompanyModel companyModel) {
        return iCompanyRepository.save(companyModel);
    }

    @Override
    public CompanyModel findCompanyById(CompanyModel company) {
       return iCompanyRepository.findById(company.getId()).get();
    }

    
}
