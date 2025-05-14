package com.increff.pos.dto;

import com.increff.pos.api.ClientApi;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.ClientData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.form.ClientForm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.annotation.DirtiesContext;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-test.xml"})
@WebAppConfiguration
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ClientTest {

    @Autowired
    private ClientDto clientDto;

    @Autowired
    private ClientApi clientApi;

    @Before
    public void setup() {
    }

    //This test verifies that inserting a client name with extra spaces results in a normalized lowercase name ("increff") being stored and retrieved successfully.
    @Test
    public void testInsertClient() throws ApiException {
        ClientForm form = new ClientForm();
        form.setClientName("  Increff  ");
        clientDto.insertClient(form);
        PaginatedResponse<ClientData> response = clientDto.getAllClient(0, 10);
        assertTrue(response.getContent().stream()
                .anyMatch(data -> data.getClientName().equals("increff")));
    }

    //Attempting to insert a client with a blank name throws an ApiException with an appropriate validation error message.
    @Test
    public void testInsertClientInvalid() throws ApiException {
        ClientForm form = new ClientForm(); // missing name

        ApiException ex = assertThrows(ApiException.class, () -> {
            clientDto.insertClient(form);
        });

        assertTrue(ex.getFieldErrors().get(0).getMessage().toLowerCase().contains("client name must not be blank"));
    }

    //After inserting a client, the getAllClient() method correctly retrieves the client data with accurate pagination and matching details.
    @Test
    public void testGetAllClient() throws ApiException {
        ClientForm form = new ClientForm();
        form.setClientName("client1");
        clientDto.insertClient(form);
        PaginatedResponse<ClientData> response = clientDto.getAllClient(0, 10);
        assertEquals(1, response.getContent().size());
        assertEquals(1, response.getTotalItems());
        assertEquals(1, response.getTotalPages());
        assertEquals("client1", response.getContent().get(0).getClientName());
    }

    //Partial name search correctly returns matching clients and that normalization (e.g. lowercase) is applied consistently.
    @Test
    public void testGetClientsByPartialName() throws ApiException {
        ClientForm form = new ClientForm();
        form.setClientName("clientABC");
        clientDto.insertClient(form);
        PaginatedResponse<ClientData> response = clientDto.getClientsByPartialName("cli", 0, 1);
        assertEquals(1, response.getContent().size());
        assertEquals("clientabc", response.getContent().get(0).getClientName());
    }

    // Inserts a client, updates its name, and verifies the updated name is normalized and saved correctly.
    @Test
    public void testUpdate() throws ApiException {
        ClientForm form = new ClientForm();
        form.setClientName("oldName");
        clientDto.insertClient(form);
        form.setClientName("  NewName ");
        clientDto.update("oldName", form);
        PaginatedResponse<ClientData> response = clientDto.getAllClient(0, 10);
        assertTrue(response.getContent().stream()
                .anyMatch(data -> data.getClientName().equals("newname")));
    }
}