package com.increff.pos.api;

import com.increff.pos.dao.UserDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;

@Service
@Transactional
public class UserApi {
    @Autowired
    private UserDao userDao;

    public String signup(UserPojo userPojo) {
        if (userDao.emailExists(userPojo.getEmail())) {
            return "Email already exists!";
        }
        userDao.insert(userPojo);
        return "Signup successful!";
    }

    public UserPojo login(UserPojo userPojo) throws ApiException {
        UserPojo user = userDao.getByEmail(userPojo.getEmail());
        ValidationUtil.checkNull(user, "Invalid credentials");
        if (!user.getPassword().equals(userPojo.getPassword())) {
            throw new ApiException("Invalid credentials");
        }
        return user;
    }

    public UserPojo getByEmail(String email) {
        return userDao.getByEmail(email);
    }

}
