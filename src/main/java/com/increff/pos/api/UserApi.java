package com.increff.pos.api;

import com.increff.pos.dao.UserDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.LoginData;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;

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

    public LoginData login(UserPojo userPojo) throws ApiException {
        UserPojo user = userDao.getByEmail(userPojo.getEmail());
        if (Objects.isNull(user) || !user.getPassword().equals(userPojo.getPassword())) {
            throw new ApiException("Invalid credentials");
        }

        JwtUtil jwtUtil = new JwtUtil();
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        LoginData data = new LoginData();
        data.setName(user.getName());
        data.setEmail(user.getEmail());
        data.setRole(user.getRole());
        data.setToken(token);

        return data;
    }


    public UserPojo getByEmail(String email) {
        return userDao.getByEmail(email);
    }
}
