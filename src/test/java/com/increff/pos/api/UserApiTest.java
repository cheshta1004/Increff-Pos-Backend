package com.increff.pos.api;

import com.increff.pos.dao.UserDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.model.enums.Role;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-test.xml"})
@WebAppConfiguration
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserApiTest {

    @InjectMocks
    private UserApi userApi;

    @Mock
    private UserDao userDao;

    private UserPojo testUser;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Setup test data
        testUser = new UserPojo();
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setName("Test User");
        testUser.setRole(Role.SUPERVISOR);
    }

    @Test
    public void testSignup_Success() {
        when(userDao.emailExists(testUser.getEmail())).thenReturn(false);
        doNothing().when(userDao).insert(testUser);

        String result = userApi.signup(testUser);

        assertEquals("Signup successful!", result);
        verify(userDao, times(1)).emailExists(testUser.getEmail());
        verify(userDao, times(1)).insert(testUser);
    }

    @Test
    public void testSignup_DuplicateEmail() {
        when(userDao.emailExists(testUser.getEmail())).thenReturn(true);

        String result = userApi.signup(testUser);

        assertEquals("Email already exists!", result);
        verify(userDao, times(1)).emailExists(testUser.getEmail());
        verify(userDao, never()).insert(any(UserPojo.class));
    }

    @Test
    public void testLogin_Success() throws ApiException {
        when(userDao.getByEmail(testUser.getEmail())).thenReturn(testUser);

        UserPojo result = userApi.login(testUser);

        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());
        assertEquals(testUser.getPassword(), result.getPassword());
        assertEquals(testUser.getName(), result.getName());
        assertEquals(testUser.getRole(), result.getRole());
        verify(userDao, times(1)).getByEmail(testUser.getEmail());
    }

    @Test(expected = ApiException.class)
    public void testLogin_InvalidPassword() throws ApiException {
        UserPojo wrongPasswordUser = new UserPojo();
        wrongPasswordUser.setEmail(testUser.getEmail());
        wrongPasswordUser.setPassword("wrongpassword");

        when(userDao.getByEmail(testUser.getEmail())).thenReturn(testUser);

        userApi.login(wrongPasswordUser);
    }

    @Test(expected = ApiException.class)
    public void testLogin_UserNotFound() throws ApiException {
        when(userDao.getByEmail(testUser.getEmail())).thenReturn(null);

        userApi.login(testUser);
    }

    @Test
    public void testGetByEmail_Success() {
        when(userDao.getByEmail(testUser.getEmail())).thenReturn(testUser);

        UserPojo result = userApi.getByEmail(testUser.getEmail());

        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());
        assertEquals(testUser.getPassword(), result.getPassword());
        assertEquals(testUser.getName(), result.getName());
        assertEquals(testUser.getRole(), result.getRole());
        verify(userDao, times(1)).getByEmail(testUser.getEmail());
    }

    @Test
    public void testGetByEmail_NotFound() {
        when(userDao.getByEmail(testUser.getEmail())).thenReturn(null);

        UserPojo result = userApi.getByEmail(testUser.getEmail());

        assertNull(result);
        verify(userDao, times(1)).getByEmail(testUser.getEmail());
    }
} 