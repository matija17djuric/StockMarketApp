package rs.teletrader.stock_marketplace.Controllers.User;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rs.teletrader.stock_marketplace.Models.ResponseMessage.ResponseMessageModel;
import rs.teletrader.stock_marketplace.Models.User.UserModel;
import rs.teletrader.stock_marketplace.Services.User.IUserService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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

        userModel = iUserService.loginUser(userModel);
        if (userModel != null) {
            Map<String, Object> responseObject = new HashMap<>();
            responseObject.put("user", userModel);
            responseObject.put("token", userModel.getPassword());
            return ResponseEntity.ok().body(responseObject);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Username or password incorrect");
        }

    }

    @PostMapping("register")
    public ResponseEntity<?> registerUser(@RequestBody UserModel userModel) {

        ResponseMessageModel responseMessageModel = new ResponseMessageModel();

        try {
            userModel = iUserService.registerNewUser(userModel);
            if (userModel != null) {
                responseMessageModel.setCode(200);
                responseMessageModel.setMessage("User registered succesfully!");
                return ResponseEntity.ok().body(responseMessageModel);
            } else {
                responseMessageModel.setCode(500);
                responseMessageModel.setMessage("Something went wrong!");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessageModel);
            }
        } catch (DataIntegrityViolationException e) {
            responseMessageModel.setCode(409);
            responseMessageModel.setMessage("Username already exists!");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(responseMessageModel);
        }
    }
}
