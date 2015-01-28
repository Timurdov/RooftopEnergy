package nl.rooftopenergy.bionic.dao.user;

import nl.rooftopenergy.bionic.dao.Dao;
import nl.rooftopenergy.bionic.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserDao extends Dao<User,String>, UserDetailsService {

    UserDetails loadUserByUsername(String username);
    User findByName(String name);

}
