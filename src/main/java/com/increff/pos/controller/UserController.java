package com.increff.pos.controller;

import com.increff.pos.dto.UserDto;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.LoginData;
import com.increff.pos.model.form.LoginForm;
import com.increff.pos.model.form.SignupForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserDto userDto;

    @RequestMapping(path = "/signup", method = RequestMethod.POST)
    public String signup(@RequestBody @Valid SignupForm form) throws ApiException{
        return userDto.signup(form);
    }
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public LoginData login(@RequestBody @Valid LoginForm form, HttpServletRequest request) throws ApiException {
      return userDto.login(form);
    }


}
