
package rs.teletrader.stock_marketplace.Configuration.AppStartup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import rs.teletrader.stock_marketplace.Models.User.RoleModel;
import rs.teletrader.stock_marketplace.Repositories.User.IRoleRepository;

@Component
public class CommandLineAppStartupRunner implements CommandLineRunner {

    @Autowired
    IRoleRepository iRoleRepository;

    @Override
    public void run(String... args){

        RoleModel roleModel = iRoleRepository.findByRoleType(RoleModel.RoleType.user);
        if (roleModel == null)
        {
            roleModel = new RoleModel();
            roleModel.setRoleType(RoleModel.RoleType.user);
            iRoleRepository.save(roleModel);

        }
        roleModel = iRoleRepository.findByRoleType(RoleModel.RoleType.bussynes);
        if (roleModel == null)
        {
            roleModel = new RoleModel();
            roleModel.setRoleType(RoleModel.RoleType.bussynes);
            iRoleRepository.save(roleModel);

        }
    }

}