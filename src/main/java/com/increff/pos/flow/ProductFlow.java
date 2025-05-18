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
//todo naming
    public ClientPojo getClientByNameOrThrow(String clientName) throws ApiException {
        return clientApi.getClientsByPartialName(clientName, 0, 1).get(0);
    }

    public ClientPojo getClientByIdOrThrow(int clientId) throws ApiException {
        return clientApi.getClientById(clientId);
    }
}
