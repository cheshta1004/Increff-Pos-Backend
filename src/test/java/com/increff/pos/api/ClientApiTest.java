package com.increff.pos.api;

import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.exception.ApiException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.annotation.DirtiesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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

    @PersistenceContext
    private EntityManager em;

    @Before
    public void setup() {
        // Clear the database
        em.createQuery("DELETE FROM ClientPojo").executeUpdate();
        em.flush();
        em.clear();
    }

    private ClientPojo createClient(String name) {
        ClientPojo client = new ClientPojo();
        client.setClientName(name);
        return client;
    }

    @Test
    public void testGetAllClient_pagination() throws ApiException {
        // Insert multiple clients
        clientApi.insertClient(createClient("client1"));
        clientApi.insertClient(createClient("client2"));
        clientApi.insertClient(createClient("client3"));

        // Test first page
        List<ClientPojo> firstPage = clientApi.getAllClient(0, 2);
        assertEquals(2, firstPage.size());
        assertEquals("client1", firstPage.get(0).getClientName());
        assertEquals("client2", firstPage.get(1).getClientName());

        // Test second page
        List<ClientPojo> secondPage = clientApi.getAllClient(1, 2);
        assertEquals(1, secondPage.size());
        assertEquals("client3", secondPage.get(0).getClientName());
    }

    @Test
    public void testGetClientById_success() throws ApiException {
        // Insert a client
        ClientPojo client = createClient("nike");
        clientApi.insertClient(client);

        // Get all clients to get the ID
        List<ClientPojo> allClients = clientApi.getAllClient(0, 10);
        assertFalse(allClients.isEmpty()); // Ensure at least one client exists
        Integer id = allClients.get(0).getId();


        // Test get by ID
        ClientPojo retrieved = clientApi.getClientById(id);
        assertEquals("nike", retrieved.getClientName());
    }

    @Test(expected = ApiException.class)
    public void testGetClientById_notFound() throws ApiException {
        clientApi.getClientById(999); // Non-existent ID
    }

    @Test
    public void testGetClientsByPartialName() throws ApiException {
        // Insert multiple clients
        clientApi.insertClient(createClient("nike-sports"));
        clientApi.insertClient(createClient("nike-casual"));
        clientApi.insertClient(createClient("adidas"));

        // Search for "nike"
        List<ClientPojo> results = clientApi.getClientsByPartialName("nike", 0, 10);
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(c -> c.getClientName().contains("nike")));
    }

    @Test
    public void testGetTotalClients() throws ApiException {
        // Insert multiple clients
        clientApi.insertClient(createClient("client1"));
        clientApi.insertClient(createClient("client2"));
        clientApi.insertClient(createClient("client3"));

        assertEquals(3, clientApi.getTotalClients());
    }

    @Test
    public void testGetTotalClientsByPartialName() throws ApiException {
        // Insert multiple clients
        clientApi.insertClient(createClient("nike-sports"));
        clientApi.insertClient(createClient("nike-casual"));
        clientApi.insertClient(createClient("adidas"));

        assertEquals(2, clientApi.getTotalClientsByPartialName("nike"));
    }
}