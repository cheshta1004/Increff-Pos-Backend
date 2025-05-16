package com.increff.pos.util;

import com.increff.pos.model.data.PaginatedResponse;
import java.util.List;

public class PaginationUtil {
    
    public static <T> PaginatedResponse<T> createPaginatedResponse(List<T> content, int page, long totalItems,int size){
        int totalPages = (int) Math.ceil((double) totalItems / size);
        return new PaginatedResponse<>(content, page, totalPages, totalItems, size);
    }
} 