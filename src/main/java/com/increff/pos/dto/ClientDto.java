package com.increff.pos.dto;
import com.increff.pos.api.ClientApi;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.ClientData;
import com.increff.pos.model.data.TopClientsData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.form.ClientForm;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.util.NormalizeUtil;
import com.increff.pos.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;


@Component
public class ClientDto {
    @Autowired
    private ClientApi clientApi;

    public void insertClient(ClientForm form) throws ApiException {
        ValidationUtil.validate(form);
        NormalizeUtil.normalize(form);
        ClientPojo pojo = DtoHelper.convertFormToClientPojo(form);
        clientApi.insertClient(pojo);
    }

    // Method to retrieve a paginated list of clients and return it as a PaginatedResponse of ClientData
    public PaginatedResponse<ClientData> getAllClient(int page, int size) throws ApiException {
        List<ClientPojo> pojoList = clientApi.getAllClient(page, size);
        List<ClientData> dataList = new ArrayList<>();
        for (ClientPojo p : pojoList) {
            dataList.add(DtoHelper.convertClientPojoToData(p));
        }
        long totalItems = clientApi.getTotalClients();
        int totalPages = (int) Math.ceil((double) totalItems / size);
        return new PaginatedResponse<>(dataList, page, totalPages, totalItems, size);
    }

    public PaginatedResponse<ClientData> getClientsByPartialName(String query, int page, int size) throws ApiException {
        List<ClientPojo> pojoList = clientApi.getClientsByPartialName(query, page, size);
        List<ClientData> dataList = new ArrayList<>();
        for (ClientPojo p : pojoList) {
            dataList.add(DtoHelper.convertClientPojoToData(p));
        }
        long totalItems = clientApi.getTotalClientsByPartialName(query);
        int totalPages = (int) Math.ceil((double) totalItems / size);
        return new PaginatedResponse<>(dataList, page, totalPages, totalItems, size);
    }

    public void update(String name, ClientForm form) throws ApiException {
        ValidationUtil.validate(form);
        NormalizeUtil.normalize(form);
        clientApi.updateClient(name, form.getClientName());
    }

}

