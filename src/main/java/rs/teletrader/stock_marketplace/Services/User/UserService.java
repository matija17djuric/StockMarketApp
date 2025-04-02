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

import rs.teletrader.stock_marketplace.Configuration.WebSecurity.JWT.JWTUtils;
import rs.teletrader.stock_marketplace.Models.User.RoleModel;
import rs.teletrader.stock_marketplace.Models.User.UserModel;
import rs.teletrader.stock_marketplace.Repositories.User.IRoleRepository;
import rs.teletrader.stock_marketplace.Repositories.User.IUserRepository;

@Service
public class UserService implements IUserService {

    @Autowired
    IUserRepository iUserRepository;

    @Autowired
    IRoleRepository iRoleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JWTUtils jwtUtils;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserModel userModel = iUserRepository.findByUsername(username);
        String detailsUsername, detailsPassword, detailsRole;
        if (userModel == null)
            throw new UsernameNotFoundException("USER NOT FOUND");
        detailsUsername = userModel.getUsername();
        detailsPassword = userModel.getPassword();
        detailsRole = userModel.getRole().getRoleType().name();
        return User.withUsername(detailsUsername).password(detailsPassword)
                .authorities(new SimpleGrantedAuthority(detailsRole)).build();
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

    @Override
    public UserModel loginUser(UserModel userModel) {
        UserModel dbUser = iUserRepository.findByUsername(userModel.getUsername());
        if (dbUser != null) {
            if (passwordEncoder.matches(userModel.getPassword(), dbUser.getPassword())) {
                dbUser.setPassword(jwtUtils.generateToken(loadUserByUsername(userModel.getUsername())));
                dbUser.setShares(null);
                return dbUser;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public UserModel findUserByUsername(String username) {
        return iUserRepository.findByUsername(username);
    }

}
