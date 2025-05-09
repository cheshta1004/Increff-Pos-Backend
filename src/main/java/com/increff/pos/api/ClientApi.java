package com.increff.pos.api;

import com.increff.pos.dao.ClientDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.TopClientsData;
import com.increff.pos.pojo.ClientPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class ClientApi {
    @Autowired
    private ClientDao clientDao;

    public void insertClient(ClientPojo clientPojo) throws ApiException {
        ClientPojo existing = clientDao.select("clientName", clientPojo.getClientName());
        if (Objects.nonNull(existing)) {
            throw new ApiException("Client with name '" + clientPojo.getClientName() + "' already exists.");
        }
        clientDao.persist(clientPojo);
    }

    public List<ClientPojo> getAllClient(int page, int size) {
        return clientDao.selectAll(page, size);
    }

    public ClientPojo getClientById(Integer id) throws ApiException {
        ClientPojo client = clientDao.select(id);
        if (Objects.isNull(client)) {
            throw new ApiException("Client with id " + id + " does not exist.");
        }
        return client;
    }

    public void updateClient(String clientName, String newClientName) throws ApiException {
        ClientPojo existing = clientDao.select("clientName", newClientName);
        ClientPojo client = clientDao.select("clientName", clientName);
    
        if (Objects.isNull(client)) {
            throw new ApiException("Client with name '" + clientName + "' not found.");
        }
    
        if (Objects.nonNull(existing) && !existing.getId().equals(client.getId())) {
            throw new ApiException("Client name '" + newClientName + "' is already in use.");
        }
    
        client.setClientName(newClientName);
    }
    

    public List<ClientPojo> getClientsByPartialName(String partialName, int page, int size) {
        return clientDao.selectByPartialName(partialName, page, size);
    }

    public long getTotalClients() {
        return clientDao.getTotalCount();
    }

    public long getTotalClientsByPartialName(String partialName) {
        return clientDao.getTotalCountByPartialName(partialName);
    }
}
