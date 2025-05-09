package com.increff.pos.controller;

import com.increff.pos.dto.ClientDto;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.PaginatedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.increff.pos.model.form.ClientForm;
import com.increff.pos.model.data.ClientData;
import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    @Autowired
    private ClientDto clientDto;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    public void createClient(@RequestBody ClientForm clientForm) throws ApiException {
        clientDto.insertClient(clientForm);
    }

    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public PaginatedResponse<ClientData> getClientByName(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) throws ApiException {
        return clientDto.getClientsByPartialName(query, page, size);
    }

    @RequestMapping(path="/get", method = RequestMethod.GET)
    public PaginatedResponse<ClientData> getAllClients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) throws ApiException {
        return clientDto.getAllClient(page, size);
    }

    @RequestMapping(path = "/update/{name}", method = RequestMethod.PUT)
    public void updateClient(@PathVariable String name, @RequestBody ClientForm clientForm) throws ApiException {
        clientDto.update(name, clientForm);
    }
}
