package com.increff.pos.api;

import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.LoginData;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.model.enums.Role;
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
public class UserApiTest {

    @Autowired
    private UserApi userApi;

    private UserPojo testUser;

    @Before
    public void setup() {
        testUser = new UserPojo();
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setName("Test User");
        testUser.setRole(Role.SUPERVISOR);
    }

    // Test to verify that a new user can successfully sign up and their details are correctly saved and retrieved
    @Test
    public void testSignup_success() {
        String result = userApi.signup(testUser);
        assertEquals("Signup successful!", result);
        
        UserPojo retrievedUser = userApi.getByEmail(testUser.getEmail());
        assertNotNull(retrievedUser);
        assertEquals(testUser.getEmail(), retrievedUser.getEmail());
        assertEquals(testUser.getName(), retrievedUser.getName());
        assertEquals(testUser.getRole(), retrievedUser.getRole());
    }

    // Test to verify that attempting to sign up with an already existing email returns an error
    @Test
    public void testSignup_duplicateEmail() {
        userApi.signup(testUser);

        String result = userApi.signup(testUser);
        assertEquals("Email already exists!", result);
    }

    // Test to verify that a user can log in successfully after signup and receives the correct login details and token
    @Test
    public void testLogin_success() throws ApiException {
        userApi.signup(testUser);
        LoginData loginData = userApi.login(testUser);
        
        assertNotNull(loginData);
        assertEquals(testUser.getEmail(), loginData.getEmail());
        assertEquals(testUser.getName(), loginData.getName());
        assertEquals(testUser.getRole(), loginData.getRole());
        assertNotNull(loginData.getToken());
    }

    // Test to verify that logging in with incorrect credentials throws an ApiException
    @Test(expected = ApiException.class)
    public void testLogin_invalidCredentials() throws ApiException {
        userApi.signup(testUser);
        UserPojo wrongUser = new UserPojo();
        wrongUser.setEmail(testUser.getEmail());
        wrongUser.setPassword("wrongpassword");
        
        userApi.login(wrongUser);
    }
    // Test to verify that logging in with a non-existent user throws an ApiException
    @Test(expected = ApiException.class)
    public void testLogin_nonexistentUser() throws ApiException {
        userApi.login(testUser);
    }

    // Test to verify that a user can be retrieved by email after signup
    @Test
    public void testGetByEmail_success() {
        userApi.signup(testUser);
        UserPojo retrievedUser = userApi.getByEmail(testUser.getEmail());
        
        assertNotNull(retrievedUser);
        assertEquals(testUser.getEmail(), retrievedUser.getEmail());
        assertEquals(testUser.getName(), retrievedUser.getName());
        assertEquals(testUser.getRole(), retrievedUser.getRole());
    }
    // Test to verify that querying with a non-existent email returns null
    @Test
    public void testGetByEmail_nonexistentUser() {
        UserPojo retrievedUser = userApi.getByEmail("nonexistent@example.com");
        assertNull(retrievedUser);
    }
} 