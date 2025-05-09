package com.increff.pos.api;

import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.exception.ApiException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-test.xml"})
@WebAppConfiguration
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ClientApiTest {

    @Autowired
    private ClientApi clientApi;

    private ClientPojo createClient(String name) {
        ClientPojo client = new ClientPojo();
        client.setClientName(name);
        return client;
    }

    // Test successful insertion of a new client.
    @Test
    public void testInsertClient_success() throws ApiException {
        ClientPojo client = createClient("nike");
        clientApi.insertClient(client);

        List<ClientPojo> allClients = clientApi.getAllClient(0, 10);
        assertEquals(1, allClients.size());
        assertEquals("nike", allClients.get(0).getClientName());
    }

    // Test if inserting a duplicate client throws an ApiException.
    @Test(expected = ApiException.class)
    public void testInsertClient_duplicate_shouldThrow() throws ApiException {
        clientApi.insertClient(createClient("adidas"));
        clientApi.insertClient(createClient("adidas")); // Should throw ApiException
    }

    // Test successful update of an existing client.
    @Test
    public void testUpdateClient_success() throws ApiException {
        clientApi.insertClient(createClient("puma"));
        clientApi.updateClient("puma", "puma-updated");

        List<ClientPojo> allClients = clientApi.getAllClient(0, 10);
        assertEquals(1, allClients.size());
        assertEquals("puma-updated", allClients.get(0).getClientName());
    }
}
