package rs.teletrader.stock_marketplace.Controllers.StockMarket.Order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rs.teletrader.stock_marketplace.Models.Stocks.Order.OrderModel;
import rs.teletrader.stock_marketplace.Models.Stocks.Order.OrderModel.OrderOption;
import rs.teletrader.stock_marketplace.Services.Stock.Company.ICompanyService;
import rs.teletrader.stock_marketplace.Services.Stock.OrderService.IOrderService;
import rs.teletrader.stock_marketplace.Services.User.IUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("api/order")
public class OrderController {

    @Autowired
    IOrderService iOrderService;

    @Autowired
    IUserService iUserService;

    @Autowired
    ICompanyService iCompanyService;

    @PostMapping("placeOrder")
    public ResponseEntity<?> placeOrder(@RequestBody OrderModel order) {
        if (order.getOrderOption() != null) {
            order.setUser(iUserService.findUserById(order.getUser()));
            order.setCompany(iCompanyService.findCompanyById(order.getCompany()));
            order = iOrderService.placeNewOrder(order);
            if (order == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No shares available");
            } else
                return ResponseEntity.ok().body("Success");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No order option");
        }
    }

    @GetMapping("getOrderBook")
    public ResponseEntity<?> getOrderBook() {
        return ResponseEntity.ok().body(iOrderService.getGlobalOrderBook());
    }

}
