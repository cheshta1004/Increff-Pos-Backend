package com.increff.pos.flow;

import com.increff.pos.api.ClientApi;
import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.ClientPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
@Component
public class ProductFlow {

    @Autowired
    private ClientApi clientApi;

    public ClientPojo getClientByNameOrThrow(String clientName) throws ApiException {
        List<ClientPojo> clients = clientApi.getClientsByPartialName(clientName, 0, 1);
        if (clients.isEmpty()) {
            throw new ApiException("Client with name '" + clientName + "' does not exist.");
        }
        return clients.get(0);
    }

    public ClientPojo getClientByIdOrThrow(int clientId) throws ApiException {
        ClientPojo client = clientApi.getClientById(clientId);
        if (client == null) {
            throw new ApiException("Client with ID '" + clientId + "' does not exist.");
        }
        return client;
    }
}
