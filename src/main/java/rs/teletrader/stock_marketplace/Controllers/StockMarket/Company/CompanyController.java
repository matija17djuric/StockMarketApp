package rs.teletrader.stock_marketplace.Controllers.StockMarket.Company;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rs.teletrader.stock_marketplace.Models.ResponseMessage.ResponseMessageModel;
import rs.teletrader.stock_marketplace.Models.Stocks.Company.CompanyModel;
import rs.teletrader.stock_marketplace.Services.Stock.Company.ICompanyService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("api/user/company")
public class CompanyController {

    @Autowired
    ICompanyService iCompanyService;

    @PostMapping("createNewCompany")
    public ResponseEntity<?> createCompany(@RequestBody CompanyModel companyModel) {
        ResponseMessageModel responseMessageModel = new ResponseMessageModel();
        try {
            companyModel = iCompanyService.createNewCompany(companyModel);
            if (companyModel != null) {
                responseMessageModel.setCode(200);
                responseMessageModel.setMessage("Succesfully created company!");
                return ResponseEntity.ok().body(responseMessageModel);
            } else {
                responseMessageModel.setCode(500);
                responseMessageModel.setMessage("Something went wrong");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessageModel);
            }
        } catch (DataIntegrityViolationException e) {
            responseMessageModel.setCode(409);
            responseMessageModel.setMessage("Company with this name already exists!");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(responseMessageModel);
        }
    }

    @GetMapping("getAllCompanies")
    public ResponseEntity<?> getAllCompanieResponseEntity() {
        return ResponseEntity.ok().body(iCompanyService.findAllCompanies());
    }

}
