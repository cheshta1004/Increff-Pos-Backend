package com.increff.pos.dto;

import com.increff.pos.api.OrderApi;
import com.increff.pos.exception.ApiException;
import com.increff.pos.flow.OrderFlow;
import com.increff.pos.model.data.BulkOrderItemData;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.enums.OrderStatus;
import com.increff.pos.model.form.BulkOrderItemForm;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.util.NormalizeUtil;
import com.increff.pos.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderDto {

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
                ValidationUtil.validate(itemForm);
                NormalizeUtil.normalize(itemForm);
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
        List<OrderPojo> orderPojoList = orderApi.getAllOrders();
        List<OrderData> orderDataList = new ArrayList<>();
        for (OrderPojo pojo : orderPojoList) {
            List<OrderItemPojo> itemPojoList = orderApi.getOrderItems(pojo.getId());
            orderDataList.add(orderFlow.convertOrderPojoToData(pojo, itemPojoList));
        }
        orderDataList.sort((o1, o2) -> o2.getTime().compareTo(o1.getTime()));
        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, orderDataList.size());
        if (startIndex >= orderDataList.size()) {
            startIndex = 0;
            endIndex = Math.min(size, orderDataList.size());
        }
        List<OrderData> paginatedContent = orderDataList.subList(startIndex, endIndex);
        int totalPages = (int) Math.ceil((double) orderDataList.size() / size);
        return new PaginatedResponse<>(paginatedContent, page, totalPages, orderDataList.size(), size);
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
        if (Objects.isNull(statusStr) || statusStr.trim().isEmpty()) {
            throw new ApiException("Order status must not be empty");
        }
        OrderStatus status;
        try {
            status = OrderStatus.valueOf(statusStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ApiException("Invalid order status: " + statusStr + ". Valid statuses are: CREATED, COMPLETED.");
        }
        OrderPojo order = orderApi.getOrderById(orderId);
        if (Objects.isNull(order)) {
            throw new ApiException("Order not found for ID: " + orderId);
        }
        orderApi.updateStatus(orderId,status);
    }

    public List<OrderData> getOrdersByStatus(String statusStr) throws ApiException {
        if (Objects.isNull(statusStr) || statusStr.trim().isEmpty()) {
            throw new ApiException("Order status must not be empty");
        }
        OrderStatus status;
        try {
            status = OrderStatus.valueOf(statusStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ApiException("Invalid order status: " + statusStr + ". Valid statuses are: CREATED, COMPLETED.");
        }
        List<OrderPojo> pojoList = orderApi.getOrdersByStatus(status);
        List<OrderData> dataList = new ArrayList<>();
        for (OrderPojo pojo : pojoList) {
            List<OrderItemPojo> itemPojoList = orderApi.getOrderItems(pojo.getId());
            for (OrderItemPojo orderItem : itemPojoList) {
                ProductPojo product = orderFlow.getProductById(orderItem.getProductId());
                dataList.add(DtoHelper.convertOrderPojoToData(pojo, itemPojoList, product));
            }
        }
        return dataList;
    }

    public OrderData getOrderById(Integer orderId) throws ApiException {
        OrderPojo pojo = orderApi.getOrderById(orderId);
        if (Objects.isNull(pojo)) {
            throw new ApiException("Order not found for ID: " + orderId);
        }
        List<OrderItemPojo> itemPojoList = orderApi.getOrderItems(pojo.getId());
        return orderFlow.convertOrderPojoToData(pojo, itemPojoList);
    }

    private OrderPojo createNewOrder(BulkOrderItemForm form) {
        OrderPojo order = new OrderPojo();
        order.setTime(ZonedDateTime.now());
        order.setCustomerName(form.getCustomerName());
        order.setCustomerContact(form.getCustomerContact());
        return order;
    }

    private void placeOrderItem(OrderItemForm itemForm, Integer orderId, BulkOrderItemData result) throws ApiException {
        try {
            ValidationUtil.validate(itemForm);
            NormalizeUtil.normalize(itemForm);
            String barcode = itemForm.getBarcode().trim().toLowerCase();
            ProductPojo product = orderFlow.getProductByBarcode(barcode);
            if (Objects.isNull(product)) {
                throw new ApiException("Product not found for barcode: " + barcode);
            }
            orderFlow.reduceInventory(product.getId(), itemForm.getQuantity());

            OrderItemPojo itemPojo = new OrderItemPojo();
            itemPojo.setOrderId(orderId);
            itemPojo.setProductId(product.getId());
            itemPojo.setQuantity(itemForm.getQuantity());
            itemPojo.setSellingPrice(itemForm.getSellingPrice());
            orderApi.insertOrder(itemPojo);

            result.addSuccess(itemForm, "Order item placed successfully.");
        } catch (ApiException e) {
            result.addFailure(itemForm, "Failed to place order item: " + e.getMessage());
            throw new ApiException("Failed to place order item: " + e.getMessage());
        }
    }

    private OrderPojo createAndInsertOrder(BulkOrderItemForm form, BulkOrderItemData result) {
        OrderPojo order = createNewOrder(form);
        orderApi.insertOrder(order);
        result.setOrderId(order.getId());
        return order;
    }

    private void checkInventory(List<OrderItemForm> orderItems, BulkOrderItemData result) throws ApiException {
        for (OrderItemForm itemForm : orderItems) {
            ValidationUtil.validate(itemForm);
            NormalizeUtil.normalize(itemForm);
            String barcode = itemForm.getBarcode().trim().toLowerCase();
            ProductPojo product = orderFlow.getProductByBarcode(barcode);
            if (Objects.isNull(product)) {
                result.addFailure(itemForm, "Product not found for barcode: " + barcode);
                continue;
            }
            InventoryPojo inventory = orderFlow.getInventoryByProductId(product.getId());
            if (Objects.isNull(inventory)) {
                result.addFailure(itemForm, "No inventory found for product: " + product.getName());
                continue;
            }
            if (inventory.getQuantity() < itemForm.getQuantity()) {
                result.addFailure(itemForm, "Insufficient inventory for product: " + product.getName());
            }
        }
    }

}
