package com.increff.pos.dto;

import com.increff.pos.api.ProductApi;
import com.increff.pos.api.ClientApi;
import com.increff.pos.exception.ApiException;
import com.increff.pos.flow.ProductFlow;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.pojo.ProductPojo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Collections;
import java.util.List;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-test.xml"})
@WebAppConfiguration
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ProductTest {

    @InjectMocks
    private ProductDto productDto;

    @Mock
    private ProductApi productApi;

    @Mock
    private ClientApi clientApi;

    @Mock
    private ProductFlow productFlow;

    private static final String TEST_BARCODE = "RUN123";
    private static final String TEST_CLIENT = "Nike";
    private static final Integer TEST_CLIENT_ID = 1;

    @Before
    public void init() throws ApiException {
        MockitoAnnotations.openMocks(this);
        ClientPojo defaultClient = new ClientPojo();
        defaultClient.setId(TEST_CLIENT_ID);
        defaultClient.setClientName(TEST_CLIENT);
        when(clientApi.getClientById(TEST_CLIENT_ID)).thenReturn(defaultClient);
        when(clientApi.getClientsByPartialName(TEST_CLIENT, 0, 1)).thenReturn(Collections.singletonList(defaultClient));
        when(clientApi.getClientsByPartialName(TEST_CLIENT.toLowerCase(), 0, 1)).thenReturn(Collections.singletonList(defaultClient));
        when(productFlow.getClientByNameOrThrow(TEST_CLIENT)).thenReturn(defaultClient);
        when(productFlow.getClientByNameOrThrow(TEST_CLIENT.toLowerCase())).thenReturn(defaultClient);
    }

    //Verify that a product is successfully inserted by calling productApi.add() once when valid data is provided.
    @Test
    public void testInsertProductSuccess() throws ApiException {
        ProductForm form = new ProductForm();
        form.setClientName(TEST_CLIENT);
        form.setName("Running Shoe");
        form.setBarcode(TEST_BARCODE);
        form.setMrp(1999.99);
        form.setImageUrl("http://example.com/img.jpg");
        doNothing().when(productApi).add(any(ProductPojo.class));
        productDto.insertProduct(form);
        verify(productApi, times(1)).add(any(ProductPojo.class));
    }

    // Verify that the correct product data is returned when queried by barcode.
    @Test
    public void testGetProductByBarcode() throws ApiException {
        // Create test data
        ProductPojo pojo = new ProductPojo();
        pojo.setBarcode(TEST_BARCODE);
        pojo.setClientId(TEST_CLIENT_ID);
        pojo.setName("Running Shoe");
        pojo.setMrp(1999.99);

        ClientPojo client = new ClientPojo();
        client.setId(TEST_CLIENT_ID);
        client.setClientName(TEST_CLIENT);

        // Set up mocks
        when(productApi.getByBarcode(TEST_BARCODE)).thenReturn(pojo);
        when(productApi.getByBarcode(TEST_BARCODE.toLowerCase())).thenReturn(pojo);
        when(productFlow.getClientByIdOrThrow(TEST_CLIENT_ID)).thenReturn(client);

        // Call the method
        ProductData data = productDto.getProductByBarcode(TEST_BARCODE);

        // Verify the response
        assertNotNull(data);
        assertEquals(TEST_BARCODE, data.getBarcode());
        assertEquals("Running Shoe", data.getName());
        assertEquals(TEST_CLIENT, data.getClientName());
    }

    // Verify that the product is correctly updated with the new details when the barcode is provided.
    @Test
    public void testUpdateProduct() throws ApiException {
        ProductForm form = new ProductForm();
        form.setName("Updated Shoe");
        form.setMrp(2099.99);
        form.setImageUrl("http://example.com/new.jpg");
        form.setClientName(TEST_CLIENT);
        form.setBarcode(TEST_BARCODE);

        ProductPojo pojo = new ProductPojo();
        pojo.setBarcode(TEST_BARCODE);
        pojo.setName("Old Shoe");
        pojo.setMrp(1999.99);
        pojo.setClientId(TEST_CLIENT_ID);

        when(productApi.getByBarcode(TEST_BARCODE)).thenReturn(pojo);
        when(productApi.getByBarcode(TEST_BARCODE.toLowerCase())).thenReturn(pojo);
        doNothing().when(productApi).updateByBarcode(eq(TEST_BARCODE.toLowerCase()), eq("updated shoe"), eq(2099.99), eq("http://example.com/new.jpg"));

        productDto.updateProduct(TEST_BARCODE, form);
        verify(productApi).updateByBarcode(eq(TEST_BARCODE.toLowerCase()), eq("updated shoe"), eq(2099.99), eq("http://example.com/new.jpg"));
    }

    // An exception is thrown if a client does not exist when attempting to fetch products by client name.
    @Test
    public void testGetProductsByClientName_throwsIfClientNotFound() throws ApiException {
        when(productApi.getByClientName("Adidas", 0, 10)).thenThrow(new ApiException("Client with name Adidas does not exist."));
        when(productApi.getTotalCountByClientName("Adidas")).thenReturn(0L);

        ApiException ex = assertThrows(ApiException.class, () ->
                productDto.getProductsByClientName("Adidas", 0, 10)
        );
        assertEquals("Client with name Adidas does not exist.", ex.getMessage());
    }
}