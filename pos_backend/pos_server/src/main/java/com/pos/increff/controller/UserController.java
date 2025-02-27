package com.pos.increff.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpStatus;

import com.pos.increff.model.data.UserData;
import com.pos.increff.model.form.UserForm;
import com.pos.increff.api.ApiException;
import com.pos.increff.dto.UserDto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
@RequestMapping("/session")
public class UserController {

    @Autowired
    private UserDto dto;

    @ApiOperation(value = "Adds a user")
    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> addUser(@RequestBody UserForm form) throws ApiException {
        dto.add(form);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "User registered successfully");
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "Logs in a user")
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody UserForm form) throws ApiException {
        UserData userData = dto.login(form);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("role", userData.getRole());
        response.put("email", userData.getEmail());
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "Deletes a user")
    @RequestMapping(path = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable int id) throws ApiException {
        dto.delete(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "User deleted successfully");
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "Gets list of all users")
    @RequestMapping(path = "/", method = RequestMethod.GET)
    public ResponseEntity<List<UserData>> getAllUsers() throws ApiException {
        return ResponseEntity.ok(dto.getAll());
    }

    @ApiOperation(value = "Logs out the current user")
    @RequestMapping(path = "/logout", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> logout() {
        SecurityContextHolder.clearContext();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Logged out successfully");
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "Checks authentication status")
    @RequestMapping(path = "/check", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> checkAuthStatus(Principal principal) throws ApiException {
        if (principal == null) {
            throw new ApiException("User not authenticated");
        }

        String email = principal.getName();
        String role = dto.getUserRole(email);
        
        Map<String, Object> response = new HashMap<>();
        response.put("authenticated", true);
        response.put("email", email);
        response.put("role", role);
        return ResponseEntity.ok(response);
    }
}
