package rs.teletrader.stock_marketplace.Repositories.User;

import org.springframework.data.jpa.repository.JpaRepository;

import rs.teletrader.stock_marketplace.Models.User.RoleModel;
import rs.teletrader.stock_marketplace.Models.User.RoleModel.RoleType;

public interface IRoleRepository extends JpaRepository<RoleModel,Integer>{
    
    public RoleModel findByRoleType(RoleType roleType);
}
