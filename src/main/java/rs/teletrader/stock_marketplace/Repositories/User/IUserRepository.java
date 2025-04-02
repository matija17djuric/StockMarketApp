package rs.teletrader.stock_marketplace.Repositories.User;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.teletrader.stock_marketplace.Models.User.UserModel;

public interface IUserRepository extends JpaRepository<UserModel,Integer> {

    public UserModel findByUsername(String username);
}