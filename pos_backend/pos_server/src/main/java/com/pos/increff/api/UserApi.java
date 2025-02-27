package com.pos.increff.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pos.increff.dao.UserDao;
import com.pos.increff.pojo.UserPojo;

@Service
public class UserApi {

    @Autowired
    private UserDao dao;

    @Transactional
    public void add(UserPojo p) throws ApiException {
        validateUser(p);
        UserPojo existing = dao.select(p.getEmail());
        if (existing != null) {
            throw new ApiException("User with given email already exists");
        }
        dao.insert(p);
    }

    @Transactional(rollbackFor = ApiException.class)
        public UserPojo getUserByEmail(String email) throws ApiException {
        UserPojo user = dao.select(email);
        if (user == null) {
            throw new ApiException("User with given email does not exist");
        }
        return user ;
    }

    @Transactional(readOnly = true)
    public List<UserPojo> getAll() {
        return dao.selectAll();
    }

    @Transactional
    public void delete(int id) {
        dao.delete(id);
    }

    @Transactional
    public void deleteAll() {
        dao.deleteAll();
    }

    protected static void normalize(UserPojo p) {
        p.setRole(p.getRole().toUpperCase().trim());
    }

    private void validateUser(UserPojo user) throws ApiException {
        if (user == null) {
            throw new ApiException("User cannot be null");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new ApiException("User email cannot be empty");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new ApiException("User password cannot be empty");
        }
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            throw new ApiException("User role cannot be empty");
        }
        normalize(user);
    }   
}

