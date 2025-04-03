package rs.teletrader.stock_marketplace.Controllers.StockMarket.Share;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rs.teletrader.stock_marketplace.Configuration.AppStartup.SharesGenerator;
import rs.teletrader.stock_marketplace.Models.ResponseMessage.ResponseMessageModel;
import rs.teletrader.stock_marketplace.Models.Stocks.Share.ShareModel;
import rs.teletrader.stock_marketplace.Services.Stock.Company.ICompanyService;
import rs.teletrader.stock_marketplace.Services.Stock.Share.IShareService;
import rs.teletrader.stock_marketplace.Services.User.IUserService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("api/admin/share")
public class AdminShareController {

    @Autowired
    IUserService iUserService;

    @Autowired
    ICompanyService iCompanyService;

    @Autowired
    IShareService iShareService;

    @GetMapping("generate")
    public void generate() {
        SharesGenerator.generate();
    }

    @PostMapping("addNewShare")
    public ResponseEntity addNewShare(@RequestBody ShareModel shareModel) {

        ResponseMessageModel responseMessageModel = new ResponseMessageModel();

        try {
            shareModel.setCompany(iCompanyService.findCompanyById(shareModel.getCompany().getId()));
            if (shareModel.getCompany() == null) {
                responseMessageModel.setCode(403);
                responseMessageModel.setMessage("Company not found!");

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMessageModel);
            }
            shareModel.setUser(iUserService.findUserById(shareModel.getUser()));
            if (shareModel.getUser() == null) {
                responseMessageModel.setCode(403);
                responseMessageModel.setMessage("User not found!");

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMessageModel);
            }

            shareModel = iShareService.addNewShare(shareModel);

            if (shareModel != null) {
                responseMessageModel.setCode(200);
                responseMessageModel.setMessage("Success");
                return ResponseEntity.ok().body(responseMessageModel);
            } else {
                responseMessageModel.setCode(500);
                responseMessageModel.setMessage("Something went wrong");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessageModel);
            }
        } catch (DataIntegrityViolationException e) {
            responseMessageModel.setCode(409);
            responseMessageModel.setMessage("Share already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(responseMessageModel);
        }
    }

    @PostMapping("addAllShares")
    public ResponseEntity<?> addAllShares(@RequestBody List<ShareModel> shares) {
        Integer countAdded = 0;

        for (ShareModel shareModel : shares) {
            shareModel.setCompany(iCompanyService.findCompanyById(shareModel.getCompany().getId()));
            shareModel.setUser(iUserService.findUserById(shareModel.getUser()));

            if (shareModel.getUser() != null && shareModel.getCompany() != null) {
                shareModel = iShareService.addNewShare(shareModel);
                if (shareModel != null)
                    countAdded++;
            }
        }

        return ResponseEntity.ok().body(countAdded);
    }

}
