package com.increff.pos.dto;
import com.increff.pos.api.ClientApi;
import com.increff.pos.dto.helper.DtoHelper;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.ClientData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.form.ClientForm;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.util.NormalizeUtil;
import com.increff.pos.util.ValidationUtil;
import com.increff.pos.util.PaginationUtil;
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

    public PaginatedResponse<ClientData> getAllClient(int page, int size) throws ApiException {
        List<ClientPojo> pojoList = clientApi.getAllClient(page, size);
        List<ClientData> dataList = new ArrayList<>();
        for (ClientPojo p : pojoList) {
            dataList.add(DtoHelper.convertClientPojoToData(p));
        }
        long totalItems = clientApi.getTotalClients();
        return PaginationUtil.createPaginatedResponse(dataList, page, totalItems, size);
    }

    public PaginatedResponse<ClientData> getClientsByPartialName(String query, int page, int size) throws ApiException {
        String name = query.trim().toLowerCase();
        List<ClientPojo> pojoList = clientApi.getClientsByPartialName(name, page, size);
        List<ClientData> dataList = new ArrayList<>();
        for (ClientPojo p : pojoList) {
            dataList.add(DtoHelper.convertClientPojoToData(p));
        }
        long totalItems = clientApi.getTotalClientsByPartialName(name);
        return PaginationUtil.createPaginatedResponse(dataList, page, totalItems, size);
    }

    public void update(String name, ClientForm form) throws ApiException {
        ValidationUtil.validate(form);
        NormalizeUtil.normalize(form);
        clientApi.updateClient(name.trim().toLowerCase(), form.getClientName());
    }

}
//todo line breaks

