package rs.teletrader.stock_marketplace.Controllers.StockMarket.Company;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rs.teletrader.stock_marketplace.Models.Stocks.Company.CompanyModel;
import rs.teletrader.stock_marketplace.Services.Stock.Company.ICompanyService;

@RestController
@RequestMapping("api/admin/company")
public class AdminCompanyController {

    @Autowired
    ICompanyService iCompanyService;

    @PostMapping("createAllCompanies")
    public ResponseEntity<?> createAllCompanies(@RequestBody List<CompanyModel> companies) {

        Integer numberOfCreated = 0;

        for (CompanyModel company : companies) {
            try {
                iCompanyService.createNewCompany(company);
                numberOfCreated++;
            } catch (DataIntegrityViolationException e) {
                e.printStackTrace();
            }
        }

        return ResponseEntity.ok().body(numberOfCreated);
    }

}
