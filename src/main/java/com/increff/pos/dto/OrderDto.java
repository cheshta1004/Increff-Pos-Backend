package com.increff.pos.dto;

import com.increff.pos.api.OrderApi;
import com.increff.pos.dto.helper.DtoHelper;
import com.increff.pos.exception.ApiException;
import com.increff.pos.flow.OrderFlow;
import com.increff.pos.model.data.BulkOrderItemData;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.enums.OrderStatus;
import com.increff.pos.model.form.BulkOrderItemForm;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.util.PaginationUtil;
import com.increff.pos.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Objects;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import com.increff.pos.pojo.InventoryPojo;
@Component
@Transactional
public class OrderDto {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    @Autowired
    private OrderApi orderApi;
    @Autowired
    private OrderFlow orderFlow;

    public BulkOrderItemData placeOrder(BulkOrderItemForm form) throws ApiException {
        ValidationUtil.validate(form);
        BulkOrderItemData result = new BulkOrderItemData();
        checkInventory(form.getOrderItems(), result);
        if (!result.getFailureList().isEmpty()) {
            return result;
        }
        try {
            OrderPojo order = createAndInsertOrder(form, result);
            for (OrderItemForm itemForm : form.getOrderItems()) {
                placeOrderItem(itemForm, order.getId(), result);
            }
            if (!result.getFailureList().isEmpty()) {
                throw new ApiException("Some order items failed to process. Transaction will be rolled back.");
            }
            return result;
        } catch (ApiException e) {
            throw new ApiException("Failed to place order: " + e.getMessage());
        }
    }

    public PaginatedResponse<OrderData> getAllOrders(int page, int size) throws ApiException {
        try {
            List<OrderPojo> orderPojoList = orderApi.getAllOrders();
            List<OrderData> orderDataList = convertOrdersToData(orderPojoList);
            List<OrderData> sortedOrders = sortOrdersByTime(orderDataList);
            return PaginationUtil.createPaginatedResponse(sortedOrders, page, orderDataList.size(), size);
        } catch (Exception e) {
            throw new ApiException("Error getting orders: " + e.getMessage());
        }
    }

    public List<OrderItemData> getOrderItems(Integer orderId) {
        return orderApi.getOrderItems(orderId)
                .stream()
                .map(pojo -> {
                    try {
                        ProductPojo product = orderFlow.getProductById(pojo.getProductId());
                        return DtoHelper.convertOrderItemPojoToData(pojo, product);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    public void updateStatus(Integer orderId, String statusStr) throws ApiException {
        OrderStatus status = parseOrderStatus(statusStr);
        if (status.equals(OrderStatus.CANCELLED)) {
            cancelOrderItems(orderId);
        }
        orderApi.updateStatus(orderId, status);
    }

    public OrderData getOrderById(Integer orderId) throws ApiException {
        OrderPojo pojo = orderApi.getOrderById(orderId);
        List<OrderItemPojo> itemPojoList = orderApi.getOrderItems(pojo.getId());
        ProductPojo product = itemPojoList.isEmpty() ? null : orderFlow.getProductById(itemPojoList.get(0).getProductId());
        return DtoHelper.convertOrderPojoToData(pojo, itemPojoList, product);
    }

    private OrderPojo createNewOrder(BulkOrderItemForm form) throws ApiException {
        return DtoHelper.convertBulkOrderFormToPojo(form);
    }

    private void placeOrderItem(OrderItemForm itemForm, Integer orderId, BulkOrderItemData result) throws ApiException {
        try {
            String barcode = itemForm.getBarcode();
            ProductPojo product = orderFlow.getProductByBarcode(barcode);
            orderFlow.reduceInventory(product.getId(), itemForm.getQuantity());
            OrderItemPojo itemPojo = DtoHelper.convertOrderItemFormToPojo(itemForm, orderId, product.getId());
            orderApi.insertOrder(itemPojo);
            result.addSuccess(itemForm, "Order item placed successfully.");
        } catch (ApiException e) {
            result.addFailure(itemForm, e.getMessage());
            throw e;
        }
    }

    private OrderPojo createAndInsertOrder(BulkOrderItemForm form, BulkOrderItemData result) throws ApiException {
        OrderPojo order = createNewOrder(form);
        orderApi.insertOrder(order);
        result.setOrderId(order.getId());
        return order;
    }

    private void checkInventory(List<OrderItemForm> orderItems, BulkOrderItemData result) throws ApiException {
        for (OrderItemForm itemForm : orderItems) {
            try {
                ProductPojo product = orderFlow.getProductByBarcode(itemForm.getBarcode());
                InventoryPojo inventory = orderFlow.getInventoryByProductId(product.getId());
                if (inventory.getQuantity() < itemForm.getQuantity()) {
                    throw new ApiException("Insufficient inventory for product: " + product.getName());
                }
            } catch (ApiException e) {
                result.addFailure(itemForm, e.getMessage());
            }
        }
    }
    private OrderStatus parseOrderStatus(String statusStr) throws ApiException {
        String status = statusStr.trim().toUpperCase();
        try {
            OrderStatus result = OrderStatus.valueOf(status);
            return result;
        } catch (IllegalArgumentException e) {
            throw new ApiException("Invalid order status: " + status + ". Valid statuses are: CREATED, COMPLETED, CANCELLED.");
        }
    }
    
    private void cancelOrderItems(Integer orderId) throws ApiException {
        List<OrderItemPojo> orderItems = orderApi.getOrderItems(orderId);
        for (OrderItemPojo item : orderItems) {
            orderFlow.restoreInventory(item.getProductId(), item.getQuantity());
        }
    }

    private List<OrderData> convertOrdersToData(List<OrderPojo> orderPojoList) throws ApiException {
        List<OrderData> orderDataList = new ArrayList<>();
        for (OrderPojo pojo : orderPojoList) {
            List<OrderItemPojo> itemPojoList = orderApi.getOrderItems(pojo.getId());
            ProductPojo product = null;
            if (!itemPojoList.isEmpty()) {
                product = orderFlow.getProductById(itemPojoList.get(0).getProductId());
            }
            orderDataList.add(DtoHelper.convertOrderPojoToData(pojo, itemPojoList, product));
        }
        return orderDataList;
    }
    private List<OrderData> sortOrdersByTime(List<OrderData> orderDataList) {
        orderDataList.sort((o1, o2) -> {
            try {
                ZonedDateTime time1 = ZonedDateTime.parse(o1.getTime(), DATE_FORMATTER);
                ZonedDateTime time2 = ZonedDateTime.parse(o2.getTime(), DATE_FORMATTER);
                return time2.compareTo(time1); // Descending order
            } catch (DateTimeParseException e) {
                return o2.getTime().compareTo(o1.getTime());
            }
        });
        return orderDataList;
    }

}
