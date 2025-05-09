package com.increff.pos.dto;

import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.*;
import com.increff.pos.model.enums.Role;
import com.increff.pos.model.form.*;
import com.increff.pos.pojo.*;
import com.increff.pos.util.ConvertUtil;
import com.increff.pos.util.NormalizeUtil;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class DtoHelper {

    public  static ClientPojo convertFormToClientPojo(ClientForm form) throws ApiException {
        ClientPojo pojo = new ClientPojo();
        ConvertUtil.mapProperties(form, pojo);
        return pojo;
    }

    public static ClientData convertClientPojoToData(ClientPojo pojo) throws ApiException {
        ClientData data = new ClientData();
        ConvertUtil.mapProperties(pojo, data);
        return data;
    }
    public static InventoryData convertInventoryPojoToData(InventoryPojo pojo) throws ApiException {
        InventoryData data = new InventoryData();
        ConvertUtil.mapProperties(pojo, data);
        return data;
    }

    public static OrderItemData convertOrderItemPojoToData(OrderItemPojo pojo,ProductPojo product) throws ApiException {
        if (Objects.isNull(product)) {
            throw new ApiException("Product not found for barcode: " + product.getBarcode());
        }
        OrderItemData data = new OrderItemData();
        data.setId(pojo.getId());
        data.setOrderId(pojo.getOrderId());
        data.setBarcode(product.getBarcode());
        data.setQuantity(pojo.getQuantity());
        data.setSellingPrice(pojo.getSellingPrice());
        data.setProductName(product.getName());

        return data;
    }
    public static OrderData convertOrderPojoToData(OrderPojo pojo,List<OrderItemPojo> itemPojoList,ProductPojo product) throws ApiException {
        OrderData data = new OrderData();
        data.setId(pojo.getId());
        data.setTime(pojo.getTime());
        data.setStatus(pojo.getStatus());
        data.setCustomerName(pojo.getCustomerName());
        data.setCustomerContact(pojo.getCustomerContact());

        List<OrderItemData> itemDataList = new ArrayList<>();
        for (OrderItemPojo itemPojo : itemPojoList) {
            itemDataList.add(convertOrderItemPojoToData(itemPojo,product));
        }
        data.setItems(itemDataList);
        return data;
    }

    public static ProductData convertProductPojoToData(ProductPojo pojo,ClientPojo client) throws ApiException {
        ProductData data = new ProductData();
        data.setId(pojo.getId());
        data.setBarcode(pojo.getBarcode());
        data.setName(pojo.getName());
        data.setMrp(pojo.getMrp());
        data.setImageUrl(pojo.getImageUrl());
        if (Objects.isNull(pojo.getClientId())) {
            data.setClientName("(No Client)");
        } else {
            try {
                data.setClientName(client.getClientName() );
            } catch (Exception e) {
                data.setClientName("(Client Lookup Error)");
            }
        }
        return data;
    }

    public static ProductPojo convertProductFormToPojo(ProductForm form, ClientPojo clientPojo) throws ApiException {
        ProductPojo pojo = new ProductPojo();
        pojo.setName(form.getName());
        pojo.setBarcode(form.getBarcode());
        pojo.setMrp(form.getMrp());
        pojo.setImageUrl(form.getImageUrl());
        pojo.setClientId(clientPojo.getId());
        return pojo;
    }
    public static UserPojo convertSignupFormToPojo(SignupForm form,Role role) {
        UserPojo pojo = new UserPojo();
        pojo.setEmail(form.getEmail());
        pojo.setName(form.getName());
        pojo.setPassword(form.getPassword());
        pojo.setRole(role);
        return pojo;
    }

    public static UserPojo convertLoginFormToPojo(LoginForm form) {
        UserPojo pojo = new UserPojo();
        pojo.setEmail(form.getEmail());
        pojo.setPassword(form.getPassword());
        return pojo;
    }

}
