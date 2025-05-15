package com.increff.pos.api;

import com.increff.pos.dao.ClientDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class ClientApi {
    @Autowired
    private ClientDao clientDao;

    public void insertClient(ClientPojo clientPojo) throws ApiException {
        ClientPojo existing = clientDao.select("clientName", clientPojo.getClientName());
        ValidationUtil.checkNonNull(existing, "Client with name '" + clientPojo.getClientName() + "' already exists.");
        clientDao.insert(clientPojo);
    }

    public List<ClientPojo> getAllClient(int page, int size) {
        return clientDao.selectAll(page, size);
    }

    public ClientPojo getClientById(Integer id) throws ApiException {
        ClientPojo client = clientDao.select(id);
        ValidationUtil.checkNull(client, "Client with id " + id + " does not exist.");
        return client;
    }

    public void updateClient(String clientName, String newClientName) throws ApiException {
        ClientPojo existing = clientDao.select("clientName", newClientName);
        ClientPojo client = clientDao.select("clientName", clientName);
        ValidationUtil.checkNull(client, "Client with name '" + clientName + "' not found.");
        ValidationUtil.checkNonNull(existing, "Client with name '" + newClientName + "' already exists.");
        client.setClientName(newClientName);
    }
    

    public List<ClientPojo> getClientsByPartialName(String partialName, int page, int size) throws ApiException {
        List<ClientPojo> clients = clientDao.selectByPartialName(partialName, page, size);
        ValidationUtil.checkNull(clients, "Client with name '" + partialName + "' does not exist.");
        return clients;
    }

    public long getTotalClients() {
        return clientDao.getTotalCount();
    }

    public long getTotalClientsByPartialName(String partialName) {
        return clientDao.getTotalCountByPartialName(partialName);
    }
}
