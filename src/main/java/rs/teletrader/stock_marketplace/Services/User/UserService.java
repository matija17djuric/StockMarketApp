package rs.teletrader.stock_marketplace.Services.User;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import rs.teletrader.stock_marketplace.Models.User.RoleModel;
import rs.teletrader.stock_marketplace.Models.User.UserModel;
import rs.teletrader.stock_marketplace.Repositories.User.IRoleRepository;
import rs.teletrader.stock_marketplace.Repositories.User.IUserRepository;

@Service
public class UserService implements IUserService{

    @Autowired
    IUserRepository iUserRepository;

    @Autowired
    IRoleRepository iRoleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserModel user = iUserRepository.findByUsername(username);
        if(user!=null)
        {
            List<SimpleGrantedAuthority> authorities = new ArrayList();

            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(user.getRole().getRoleType().name());

            authorities.add(simpleGrantedAuthority);

            UserDetails userDetails = new User(user.getUsername(),"" , authorities);
            return userDetails;
        }
        else
            throw new UsernameNotFoundException("USER DOESN'T EXIST");
    }

    @Override
    public UserModel registerNewUser(UserModel userModel) {
        
        userModel.setPassword(passwordEncoder.encode(userModel.getPassword()));
        userModel.setRole(iRoleRepository.findByRoleType(RoleModel.RoleType.user));
        userModel = iUserRepository.save(userModel);
        return userModel;
    }

    @Override
    public UserModel findUserById(UserModel user) {
        return iUserRepository.findById(user.getId()).get();
    }
    
}
