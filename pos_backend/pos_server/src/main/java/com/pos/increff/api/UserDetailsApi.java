package com.pos.increff.api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.pos.increff.pojo.UserPojo;
import java.util.Collections;
@Service
public class UserDetailsApi implements UserDetailsService {
    @Autowired
    private UserApi userApi;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            UserPojo user = userApi.getUserByEmail(email);
            if (user == null) {
                throw new UsernameNotFoundException("User not found with email: " + email);
            }
            return new User(
                    user.getEmail(),
                    user.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole())));
        } catch (ApiException e) {
            throw new UsernameNotFoundException("Error loading user: " + e.getMessage());
        }
    }
}