package rs.teletrader.stock_marketplace.Services.User;

import org.springframework.security.core.userdetails.UserDetailsService;

import rs.teletrader.stock_marketplace.Models.User.UserModel;

public interface IUserService extends UserDetailsService{
    
    UserModel registerNewUser(UserModel userModel);

    UserModel findUserById(UserModel user);
}
