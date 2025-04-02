package rs.teletrader.stock_marketplace.Controllers.User;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rs.teletrader.stock_marketplace.Models.ResponseMessage.ResponseMessageModel;
import rs.teletrader.stock_marketplace.Models.User.UserModel;
import rs.teletrader.stock_marketplace.Services.User.IUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    @Autowired
    IUserService iUserService;

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody UserModel userModel) {

        String token = iUserService.loginUser(userModel);

        if (!token.isEmpty()) {
            return ResponseEntity.ok().body(token);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Username or password incorrect");
        }

    }

}
