package com.increff.pos.flow;

import com.increff.pos.api.ClientApi;
import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.ClientPojo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Collections;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ProductFlowTest {

    @InjectMocks
    private ProductFlow productFlow;

    @Mock
    private ClientApi clientApi;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetClientByNameOrThrow_success() throws ApiException {
        ClientPojo client = new ClientPojo();
        client.setId(1);
        client.setClientName("Nike");
        when(clientApi.getClientsByPartialName("Nike", 0, 1)).thenReturn(Collections.singletonList(client));
        ClientPojo result = productFlow.getClientByNameOrThrow("Nike");
        assertEquals("Nike", result.getClientName());
    }

    @Test(expected = ApiException.class)
    public void testGetClientByNameOrThrow_notFound() throws ApiException {
        when(clientApi.getClientsByPartialName("Adidas", 0, 1)).thenThrow(new ApiException("Client with name 'Adidas' does not exist."));
        productFlow.getClientByNameOrThrow("Adidas");
    }

    @Test
    public void testGetClientByIdOrThrow_success() throws ApiException {
        ClientPojo client = new ClientPojo();
        client.setId(2);
        client.setClientName("Puma");
        when(clientApi.getClientById(2)).thenReturn(client);
        ClientPojo result = productFlow.getClientByIdOrThrow(2);
        assertEquals("Puma", result.getClientName());
    }

    @Test(expected = ApiException.class)
    public void testGetClientByIdOrThrow_notFound() throws ApiException {
        when(clientApi.getClientById(99)).thenThrow(new ApiException("Client with id 99 does not exist."));
        productFlow.getClientByIdOrThrow(99);
    }
}