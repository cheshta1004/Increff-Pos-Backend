package com.increff.pos.dto;

import com.increff.pos.api.UserApi;
import com.increff.pos.config.RoleConfig;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.LoginData;
import com.increff.pos.model.enums.Role;
import com.increff.pos.model.form.LoginForm;
import com.increff.pos.model.form.SignupForm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-test.xml"})
@WebAppConfiguration
@Transactional
public class UserTest {

    @Autowired
    private UserDto userDto;

    @Autowired
    private UserApi userApi;

    @Autowired
    private RoleConfig roleConfig;

    @Before
    public void init() {
    }

    // Test successful signup and login for a supervisor user.
    @Test
    public void testSignupAsSupervisor() throws ApiException {
        SignupForm form = new SignupForm();
        form.setName("Admin User");
        form.setEmail("admin@example.com");
        form.setPassword("password123");

        String result = userDto.signup(form);

        assertNotNull(result);
        assertTrue(result.contains("Signup successful"));
        
        // Verify by trying to login
        LoginForm loginForm = new LoginForm();
        loginForm.setEmail(form.getEmail());
        loginForm.setPassword(form.getPassword());
        
        LoginData loginData = userDto.login(loginForm);
        assertEquals(form.getEmail(), loginData.getEmail());
        assertEquals(Role.SUPERVISOR, loginData.getRole());
    }

    // Test successful signup and login for an operator user.
    @Test
    public void testSignupAsOperator() throws ApiException {
        SignupForm form = new SignupForm();
        form.setName("Employee User");
        form.setEmail("employee@example.com");
        form.setPassword("secure");

        String result = userDto.signup(form);

        assertNotNull(result);
        assertTrue(result.contains("Signup successful"));

        LoginForm loginForm = new LoginForm();
        loginForm.setEmail(form.getEmail());
        loginForm.setPassword(form.getPassword());
        
        LoginData loginData = userDto.login(loginForm);
        assertEquals(form.getEmail(), loginData.getEmail());
        assertEquals(Role.OPERATOR, loginData.getRole());
    }


    // Test login functionality after user signup to verify correct role is returned.
    @Test
    public void testLogin() throws ApiException {
        SignupForm signupForm = new SignupForm();
        signupForm.setName("Test User");
        signupForm.setEmail("user@example.com");
        signupForm.setPassword("password");
        userDto.signup(signupForm);

        LoginForm loginForm = new LoginForm();
        loginForm.setEmail(signupForm.getEmail());
        loginForm.setPassword(signupForm.getPassword());

        LoginData result = userDto.login(loginForm);

        assertEquals(loginForm.getEmail(), result.getEmail());
        assertEquals(Role.OPERATOR, result.getRole());
    }
}
