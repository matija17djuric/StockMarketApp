package rs.teletrader.stock_marketplace.Controllers.StockMarket.Share;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rs.teletrader.stock_marketplace.Models.Stocks.Share.ShareModel;
import rs.teletrader.stock_marketplace.Models.User.UserModel;
import rs.teletrader.stock_marketplace.Services.Stock.Share.IShareService;
import rs.teletrader.stock_marketplace.Services.User.IUserService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("api/user/share")
public class ShareController {

    @Autowired
    IUserService iUserService;

    @Autowired
    IShareService iShareService;

    @GetMapping("getMyShares")
    public ResponseEntity<?> getUserShares() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserModel userModel = iUserService.findUserByUsername(userDetails.getUsername());

        List<ShareModel> userShares = userModel.getShares();

        return ResponseEntity.ok().body(userShares);
    }

}
