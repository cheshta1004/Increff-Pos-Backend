package com.increff.pos.util;

import com.increff.pos.model.form.*;
import java.util.Objects;

public class NormalizeUtil {

    public static void normalize(ClientForm form) {
        if (!Objects.isNull(form.getClientName())) {
            form.setClientName(form.getClientName().trim().toLowerCase());
        }
    }
    public static void normalize(InventoryForm form) {
        if (!Objects.isNull(form.getProductBarcode())) {
            form.setProductBarcode(form.getProductBarcode().trim().toLowerCase());
        }
        if (!Objects.isNull(form.getQuantity())) {
            form.setQuantity(Integer.parseInt(String.valueOf(form.getQuantity())));
        }
    }

    public static void normalize(ProductForm form) {
        if (!Objects.isNull(form.getBarcode())) {
            form.setBarcode(form.getBarcode().trim().toLowerCase());
        }
        if (!Objects.isNull(form.getName())) {
            form.setName(form.getName().trim().toLowerCase());
        }
        if (!Objects.isNull(form.getClientName())) {
            form.setClientName(form.getClientName().trim().toLowerCase());
        }
        if (!Objects.isNull(form.getImageUrl())) {
            form.setImageUrl(form.getImageUrl().trim());
        }
        if (!Objects.isNull(form.getMrp())) {
            String mrpStr = String.format("%.2f", form.getMrp());
            form.setMrp(Double.parseDouble(mrpStr));
        }
    }
    public static void normalize(SignupForm form) {
        if (!Objects.isNull(form.getEmail())) {
            form.setEmail(form.getEmail().trim().toLowerCase());
        }
        if (!Objects.isNull(form.getName())) {
            form.setName(form.getName().trim());
        }
        if (!Objects.isNull(form.getPassword())) {
            form.setPassword(form.getPassword().trim());
        }
    }
    public static void normalize(LoginForm form) {
        if (!Objects.isNull(form.getEmail())) {
            form.setEmail(form.getEmail().trim().toLowerCase());
        }
        if (!Objects.isNull(form.getPassword())) {
            form.setPassword(form.getPassword().trim().toLowerCase());
        }
    }
    public static void normalize(SalesReportFilterForm form) {
        if (!Objects.isNull(form.getClientName())) {
            form.setClientName(form.getClientName().trim().toLowerCase());
        }
    }
    

}
