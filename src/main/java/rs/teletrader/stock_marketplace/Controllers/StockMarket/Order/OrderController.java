package rs.teletrader.stock_marketplace.Controllers.StockMarket.Order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rs.teletrader.stock_marketplace.Models.Stocks.Company.CompanyModel;
import rs.teletrader.stock_marketplace.Models.Stocks.Order.OrderModel;
import rs.teletrader.stock_marketplace.Models.User.UserModel;
import rs.teletrader.stock_marketplace.Services.Stock.Company.ICompanyService;
import rs.teletrader.stock_marketplace.Services.Stock.OrderService.IOrderService;
import rs.teletrader.stock_marketplace.Services.User.IUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("api/user/order")
public class OrderController {

    @Autowired
    IOrderService iOrderService;

    @Autowired
    IUserService iUserService;

    @Autowired
    ICompanyService iCompanyService;

    @PostMapping("placeOrder")
    public ResponseEntity<?> placeOrder(@RequestBody OrderModel order) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = ((UserDetails) auth.getPrincipal()).getUsername();

        if (order.getOrderOption() != null) {
            UserModel user = iUserService.findUserByUsername(username);
            order.setUser(user);
            order.setCompany(iCompanyService.findCompanyById(order.getCompany().getId()));
            order = iOrderService.placeNewOrder(order);
            if (order == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No shares available");
            } else
                return ResponseEntity.ok().body(order);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No order option");
        }
    }

    @PostMapping("cancelOrder")
    public ResponseEntity<?> cancelOrder(@RequestBody OrderModel order) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = ((UserDetails) auth.getPrincipal()).getUsername();
        UserModel user = iUserService.findUserByUsername(username);
        order.setUser(user);
        order = iOrderService.cancelOrder(order);

        if (order != null) {
            return ResponseEntity.ok().body(order);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No order found");
        }
    }

    @GetMapping("getOrderBook")
    public ResponseEntity<?> getOrderBook() {
        return ResponseEntity.ok().body(iOrderService.getGlobalOrderBook());
    }

    @GetMapping("getMyOrders")
    public ResponseEntity<?> getUserOrders() {

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserModel userModel = iUserService.findUserByUsername(userDetails.getUsername());

        List<OrderModel> orders = userModel.getOrders();

        return ResponseEntity.ok().body(orders);
    }

    @GetMapping("getOpenOrdersForCompany/{idCompany}")
    public ResponseEntity<?> getOpenOrdersForCompany(@PathVariable Integer idCompany) {
        CompanyModel company = iCompanyService.findCompanyById(idCompany);
        if (company != null) {
            return ResponseEntity.ok().body(iOrderService.findAllOpenByCompanyId(idCompany));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Company not found!");
        }
    }

}
