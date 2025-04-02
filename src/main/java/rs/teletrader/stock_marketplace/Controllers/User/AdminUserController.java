package rs.teletrader.stock_marketplace.Controllers.User;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rs.teletrader.stock_marketplace.Models.User.UserModel;
import rs.teletrader.stock_marketplace.Services.User.IUserService;

@RestController
@RequestMapping("api/admin/user")
public class AdminUserController {

    @Autowired
    IUserService iUserService;

    @PostMapping("registerAllUsers")
    public ResponseEntity<?> registerListOfUsers(@RequestBody List<UserModel> users) {
        Integer nuberOfRegistered = 0;
        for (UserModel userModel : users) {
            try {
                iUserService.registerNewUser(userModel);
                nuberOfRegistered++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return ResponseEntity.ok().body(nuberOfRegistered);
    }
}
