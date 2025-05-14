package com.increff.pos.dao;

import com.increff.pos.pojo.ClientPojo;
import org.junit.Before;
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
public class ClientDaoTest {

    @Autowired
    private ClientDao clientDao;

    @Before
    public void setUp() {
        // No setup needed, DB is cleaned between tests
    }

    private ClientPojo createClient(String name) {
        ClientPojo pojo = new ClientPojo();
        pojo.setClientName(name);
        return pojo;
    }

    @Test
    public void testInsertAndSelectAll() {
        ClientPojo c1 = createClient("Alpha");
        ClientPojo c2 = createClient("Beta");
        clientDao.insert(c1);
        clientDao.insert(c2);
        List<ClientPojo> all = clientDao.selectAll(0, 10);
        assertEquals(2, all.size());
    }

    @Test
    public void testSelectAllPagination() {
        for (int i = 0; i < 5; i++) {
            clientDao.insert(createClient("Client" + i));
        }
        List<ClientPojo> page1 = clientDao.selectAll(0, 2);
        List<ClientPojo> page2 = clientDao.selectAll(1, 2);
        assertEquals(2, page1.size());
        assertEquals(2, page2.size());
    }

    @Test
    public void testSelectByPartialName() {
        clientDao.insert(createClient("Alpha"));
        clientDao.insert(createClient("Beta"));
        clientDao.insert(createClient("Alphonso"));
        List<ClientPojo> results = clientDao.selectByPartialName("Alph", 0, 10);
        assertEquals(2, results.size());
    }

    @Test
    public void testSelectByPartialNamePagination() {
        clientDao.insert(createClient("Alpha"));
        clientDao.insert(createClient("Alphonso"));
        clientDao.insert(createClient("Alphabet"));
        List<ClientPojo> page1 = clientDao.selectByPartialName("Alph", 0, 2);
        List<ClientPojo> page2 = clientDao.selectByPartialName("Alph", 1, 2);
        assertEquals(2, page1.size());
        assertEquals(1, page2.size());
    }

    @Test
    public void testGetTotalCount() {
        clientDao.insert(createClient("Alpha"));
        clientDao.insert(createClient("Beta"));
        assertEquals(2, clientDao.getTotalCount());
    }

    @Test
    public void testGetTotalCountByPartialName() {
        clientDao.insert(createClient("Alpha"));
        clientDao.insert(createClient("Alphonso"));
        clientDao.insert(createClient("Beta"));
        assertEquals(2, clientDao.getTotalCountByPartialName("Alph"));
    }

    @Test
    public void testSelectByPartialNameNoResults() {
        clientDao.insert(createClient("Alpha"));
        List<ClientPojo> results = clientDao.selectByPartialName("Zeta", 0, 10);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testGetTotalCountByPartialNameNoResults() {
        clientDao.insert(createClient("Alpha"));
        assertEquals(0, clientDao.getTotalCountByPartialName("Zeta"));
    }
}
