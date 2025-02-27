package com.pos.increff.dto;

import com.pos.increff.api.ApiException;
import com.pos.increff.api.ClientApi;
import com.pos.increff.dao.ClientDao;
import com.pos.increff.model.data.ClientData;
import com.pos.increff.model.form.ClientForm;
import com.pos.increff.pojo.ClientPojo;
import com.pos.increff.model.data.PaginatedData;
import com.pos.increff.util.ConversionUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ClientDto extends AbstractDto {

    @Autowired
    private ClientApi clientApi;

    public void add(ClientForm form) throws ApiException {
        checkValid(form);
        ClientPojo p = ConversionUtil.convert(form);
        clientApi.add(p);
    }

    public void delete(Integer id) throws ApiException {
        if (id == null) {
            throw new ApiException("Client ID cannot be null");
        }
        clientApi.delete(id);
    }

    public ClientData get(Integer id) throws ApiException {
        if (id == null) {
            throw new ApiException("Client ID cannot be null");
        }
        return ConversionUtil.convert(clientApi.get(id));
    }


    //TODO: Remove next page and handle it in frontend
    public PaginatedData<ClientData> getAll(int page, int pageSize) throws ApiException {
        List<ClientPojo> clients = clientApi.getAll(page, pageSize);
        boolean hasNextPage = clients.size() > pageSize;
        if (hasNextPage) {
            clients = clients.subList(0, pageSize);
        }
        List<ClientData> clientDataList = new ArrayList<>();
        for (ClientPojo client : clients) {
            clientDataList.add(ConversionUtil.convert(client));
        }
        return new PaginatedData<>(clientDataList, page, pageSize, hasNextPage);
    }

    public void deleteAll() throws ApiException {
        clientApi.deleteAll();
    }

    public void update(Integer id, ClientForm form) throws ApiException {
        if (id == null) {
            throw new ApiException("Client ID cannot be null");
        }
        checkValid(form);
        ClientPojo p = ConversionUtil.convert(form);
        clientApi.update(id, p);
    }

    public PaginatedData<ClientData> searchByName(String name, int page, int pageSize) throws ApiException {
        List<ClientPojo> clients = clientApi.searchByName(name, page, pageSize);
        boolean hasNextPage = clients.size() > pageSize;
        if (hasNextPage) {
            clients = clients.subList(0, pageSize);
        }
        List<ClientData> clientDataList = new ArrayList<>();
        for (ClientPojo client : clients) {
            clientDataList.add(ConversionUtil.convert(client));
        }
        return new PaginatedData<>(clientDataList, page, pageSize, hasNextPage);
    }
}
