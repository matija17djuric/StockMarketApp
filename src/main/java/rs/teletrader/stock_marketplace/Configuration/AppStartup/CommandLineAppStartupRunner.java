
package rs.teletrader.stock_marketplace.Configuration.AppStartup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import rs.teletrader.stock_marketplace.Models.User.RoleModel;
import rs.teletrader.stock_marketplace.Models.User.UserModel;
import rs.teletrader.stock_marketplace.Repositories.User.IRoleRepository;
import rs.teletrader.stock_marketplace.Repositories.User.IUserRepository;

@Component
public class CommandLineAppStartupRunner implements CommandLineRunner {

    @Autowired
    IRoleRepository iRoleRepository;
    @Autowired
    IUserRepository iUserRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        RoleModel roleModel = iRoleRepository.findByRoleType(RoleModel.RoleType.user);
        if (roleModel == null) {
            roleModel = new RoleModel();
            roleModel.setRoleType(RoleModel.RoleType.user);
            iRoleRepository.save(roleModel);

        }
        roleModel = iRoleRepository.findByRoleType(RoleModel.RoleType.admin);
        if (roleModel == null) {
            roleModel = new RoleModel();
            roleModel.setRoleType(RoleModel.RoleType.admin);
            iRoleRepository.save(roleModel);

        }

        if (iUserRepository.findByUsername("administrator") == null) {
            UserModel userModel = new UserModel();
            userModel.setUsername("administrator");
            userModel.setPassword(passwordEncoder.encode("adminPass"));
            iUserRepository.save(userModel);
        }
    }

}