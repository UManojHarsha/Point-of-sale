package com.pos.increff.dto;

import com.pos.increff.api.ApiException;
import com.pos.increff.api.UserApi;
import com.pos.increff.model.data.UserData;
import com.pos.increff.model.form.UserForm;
import com.pos.increff.pojo.UserPojo;
import com.pos.increff.util.ConversionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.pos.increff.spring.ApplicationProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;

@Component
public class UserDto extends AbstractDto {

    @Autowired
    private UserApi userApi;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ApplicationProperties applicationConfig;

    public void add(UserForm form) throws ApiException {
        System.out.println("Supervisor Emails: " + applicationConfig.getSupervisorEmails());
        System.out.println("Checking Email: " + form.getEmail());

        checkValid(form);
        UserPojo p = ConversionUtil.convert(form);
        // Hash password
        p.setPassword(passwordEncoder.encode(form.getPassword()));
        // Assign role
        String supervisorEmails = applicationConfig.getSupervisorEmails();
        List<String> emails = Arrays.asList(supervisorEmails.split(",")) ;
        boolean isSupervisor = emails.contains(form.getEmail());
        p.setRole(isSupervisor ? "SUPERVISOR" : "OPERATOR");
        userApi.add(p);
    }

    public String getUserRole(String email) throws ApiException {
        if (email == null) {
            throw new ApiException("Email address cannot be empty");
        }
        UserPojo user = userApi.getUserByEmail(email);
        return user != null ? user.getRole() : null;
    }

    public UserData login(UserForm form) throws ApiException {
        checkValid(form);
        UserPojo user = userApi.getUserByEmail(form.getEmail());
        if (user == null) {
            throw new ApiException("The email or password you entered is incorrect");
        }
            
        if (!passwordEncoder.matches(form.getPassword(), user.getPassword())) {
            throw new ApiException("The email or password you entered is incorrect");
        }

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            user.getEmail(), 
            null,
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        return ConversionUtil.convert(user);
    }

    public void delete(int id) {
        userApi.delete(id);
    }

    public void deleteAll() {
        userApi.deleteAll();
    }

    public List<UserData> getAll() throws ApiException {
        List<UserPojo> list = userApi.getAll();
        List<UserData> list2 = new ArrayList<UserData>();
        for (UserPojo p : list) {
            list2.add(ConversionUtil.convert(p));
        }
        return list2;
    }

    public void logout() {
        SecurityContextHolder.clearContext();
    }
}