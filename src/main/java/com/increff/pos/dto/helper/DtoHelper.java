package com.increff.pos.dto.helper;

import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.*;
import com.increff.pos.model.enums.Role;
import com.increff.pos.model.form.*;
import com.increff.pos.pojo.*;
import com.increff.pos.util.ConvertUtil;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class DtoHelper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

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
        OrderItemData data = new OrderItemData();
        ConvertUtil.mapProperties(pojo, data);
        data.setProductName(product.getName());
        data.setBarcode(product.getBarcode());
        return data;
    }
    public static OrderData convertOrderPojoToData(OrderPojo pojo, List<OrderItemPojo> itemPojoList, ProductPojo product) throws ApiException {
        OrderData data = new OrderData();
        data.setId(pojo.getId());
        data.setStatus(pojo.getStatus());
        data.setCustomerName(pojo.getCustomerName());
        data.setCustomerContact(pojo.getCustomerContact());
        data.setTime(pojo.getTime().withZoneSameInstant(ZoneId.of("UTC")).format(DATE_FORMATTER));

        List<OrderItemData> itemDataList = new ArrayList<>();
        for (OrderItemPojo itemPojo : itemPojoList) {
            itemDataList.add(convertOrderItemPojoToData(itemPojo, product));
        }
        data.setItems(itemDataList);
        return data;
    }

    public static ProductData convertProductPojoToData(ProductPojo pojo,ClientPojo client) throws ApiException {
        ProductData data = new ProductData();
        ConvertUtil.mapProperties(pojo, data);
        data.setClientName(client.getClientName());
        return data;
    }

    public static ProductPojo convertProductFormToPojo(ProductForm form, ClientPojo clientPojo) throws ApiException {
        ProductPojo pojo = new ProductPojo();
        ConvertUtil.mapProperties(form, pojo);
        pojo.setClientId(clientPojo.getId());
        return pojo;
    }
    public static UserPojo convertSignupFormToPojo(SignupForm form,Role role) throws ApiException {
        UserPojo pojo = new UserPojo();
        ConvertUtil.mapProperties(form, pojo);
        pojo.setRole(role);
        return pojo;
    }

    public static UserPojo convertLoginFormToPojo(LoginForm form) throws ApiException {
        UserPojo pojo = new UserPojo();
        ConvertUtil.mapProperties(form, pojo);
        return pojo;
    }

    public static DailyReportData convertDailyReportPojoToData(DailyReportPojo pojo) throws ApiException {
        DailyReportData data = new DailyReportData();
        ConvertUtil.mapProperties(pojo, data);
        return data;
    }

    public static List<DailyReportData> convertDailyReportPojoListToData(List<DailyReportPojo> pojos) throws ApiException {
        return pojos.stream()
            .map(pojo -> {
                try {
                    return convertDailyReportPojoToData(pojo);
                } catch (ApiException e) {
                    throw new RuntimeException(e);
                }
            })
            .collect(Collectors.toList());
    }

    public static InventoryPojo convertInventoryFormToPojo(InventoryForm form, Integer productId) {
        InventoryPojo pojo = new InventoryPojo();
        pojo.setProductId(productId);
        pojo.setQuantity(form.getQuantity());
        return pojo;
    }

    public static InventoryData convertInventoryPojoToData(InventoryPojo pojo, ProductPojo product) throws ApiException {
        InventoryData data = new InventoryData();
        ConvertUtil.mapProperties(pojo, data);
        if (!Objects.isNull(product)) {
            data.setBarcode(product.getBarcode());
            data.setProductName(product.getName());
        }
        return data;
    }

    public static List<InventoryData> convertInventoryPojoListToData(List<InventoryPojo> pojos, List<ProductPojo> products) {
        return pojos.stream()
            .map(pojo -> {
                ProductPojo product = products.stream()
                    .filter(p -> Objects.equals(p.getId(), pojo.getProductId()))
                    .findFirst()
                    .orElse(null);
                try {
                    return convertInventoryPojoToData(pojo, product);
                } catch (ApiException e) {
                    throw new RuntimeException(e);
                }
            })
            .collect(Collectors.toList());
    }

    public static OrderItemPojo convertOrderItemFormToPojo(OrderItemForm form, Integer orderId, Integer productId) throws ApiException {
        OrderItemPojo pojo = new OrderItemPojo();
        pojo.setOrderId(orderId);
        pojo.setProductId(productId);
        ConvertUtil.mapProperties(form, pojo);
        return pojo;
    }

    public static OrderPojo convertBulkOrderFormToPojo(BulkOrderItemForm form) throws ApiException {
        OrderPojo pojo = new OrderPojo();
        pojo.setTime(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")));
        ConvertUtil.mapProperties(form, pojo);
        return pojo;
    }

    public static LoginData convertUserPojoToLoginData(UserPojo user, String token) throws ApiException {
        LoginData data = new LoginData();
        ConvertUtil.mapProperties(user, data);
        data.setToken(token);
        return data;
    }
    public static DailyReportData convertToData(DailyReportPojo pojo) throws ApiException {
        DailyReportData data = new DailyReportData();
        data.setDate(pojo.getDate().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        data.setOrderCount(pojo.getOrderCount());
        data.setTotalItems(pojo.getTotalItems());
        data.setRevenue(pojo.getRevenue());
        return data;
    }

    public static List<DailyReportData> convertToDataList(List<DailyReportPojo> pojoList) {
        return pojoList.stream()
                .map(pojo -> {
                    try {
                        return convertToData(pojo);
                    } catch (ApiException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }
}
