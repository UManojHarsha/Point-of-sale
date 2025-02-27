package com.pos.increff.api;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pos.increff.dao.ClientDao;
import com.pos.increff.pojo.ClientPojo;
import com.pos.increff.pojo.ProductPojo;
import com.pos.increff.util.DateTimeUtils;

@Service
public class ClientApi {

    @Autowired
    private ClientDao dao;

    @Transactional
    public void add(ClientPojo client) throws ApiException {
        validateClient(client);
        ClientPojo existing = dao.selectByName(client.getName());
        if (existing != null) {
            throw new ApiException("Client with given name already exists");
        }
        ClientPojo existing_email = dao.selectByEmail(client.getEmail());
        if (existing_email != null) {
            throw new ApiException("Client with given email already exists");
        }
        ClientPojo existing_contact = dao.selectByContact(client.getContactNo());
        if (existing_contact != null) {
            throw new ApiException("Client with given contact number already exists");
        }
        dao.insert(client);
    }

    @Transactional(readOnly = true)
    public ClientPojo get(int id) throws ApiException {
        ClientPojo client = dao.select(id);
        if (client == null) {
            throw new ApiException("Client with given ID does not exist");
        }
        return client;
    }

    @Transactional(readOnly = true)
    public ClientPojo getByName(String name) throws ApiException {
        ClientPojo client = dao.selectByName(name);
        if (client == null) {
            throw new ApiException("Client with given name does not exist");
        }
        return client;
    }

    @Transactional(readOnly = true)
    public List<ClientPojo> getAll() {
        List<ClientPojo> clients = dao.selectAll();
        return clients;
    }

    @Transactional(readOnly = true)
    public List<ClientPojo> getAll(int page, int pageSize) {
        List<ClientPojo> clients = dao.selectAll(page, pageSize);
        return clients;
    }

    @Transactional
    public void update(int id, ClientPojo updated) throws ApiException {
        normalize(updated);
        ClientPojo existing = dao.select(id);
        ClientPojo nameCheck = dao.selectByName(updated.getName());
        if (nameCheck != null && !nameCheck.getId().equals(id)) {
            throw new ApiException("Another client with this name already exists");
        }
        if(existing == null){
            throw new ApiException("Client with given ID does not exist");
        }
        ClientPojo existing_email = dao.selectByEmail(updated.getEmail());
        if (existing_email != null && !existing_email.getId().equals(id)) {
            throw new ApiException("Another client with this email already exists");
        }
        ClientPojo existing_contact = dao.selectByContact(updated.getContactNo());
        if (existing_contact != null && !existing_contact.getId().equals(id)) {
            throw new ApiException("Another client with this contact number already exists");
        }
        // Check if new name conflicts with another client
        dao.updateDetails(id , updated.getName() , updated.getEmail(), updated.getContactNo());
    }

    @Transactional
    public void delete(int id) throws ApiException {
        ClientPojo existing = get(id);
        if (existing == null) {
            throw new ApiException("Client with given ID does not exist");
        }
        dao.delete(id);
    }

    @Transactional
    public void deleteAll() {
        dao.deleteAll();
    }

    @Transactional(readOnly = true)
    public List<ClientPojo> searchByName(String name, int page, int pageSize) throws ApiException {
        try {
            String searchName = normalize(name);
            return dao.searchByName(searchName, page, pageSize);
        } catch (Exception e) {
            throw new ApiException("Error searching clients: " + e.getMessage());
        }
    }

    private void normalize(ClientPojo client) {
        client.setName(normalize(client.getName()));
    }

    private String normalize(String name) {
        return name.toLowerCase().trim();
    }

    private void validateClient(ClientPojo client) throws ApiException {
        if (client == null) {
            throw new ApiException("Client cannot be null");
        }
        if (client.getName() == null || client.getName().trim().isEmpty()) {
            throw new ApiException("Product name cannot be empty");
        }
        if(client.getEmail() == null || client.getEmail().trim().isEmpty()){
            throw new ApiException("Client email cannot be empty");
        }
        if(client.getContactNo() == null || client.getContactNo().trim().isEmpty()){
            throw new ApiException("Client contact number cannot be empty");
        }
        // Normalize the product name
        normalize(client);
    }

}
