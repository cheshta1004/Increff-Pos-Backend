package com.increff.pos.dto;

import com.increff.pos.api.UserApi;
import com.increff.pos.config.RoleConfig;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.LoginData;
import com.increff.pos.model.enums.Role;
import com.increff.pos.model.form.LoginForm;
import com.increff.pos.model.form.SignupForm;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.util.NormalizeUtil;
import com.increff.pos.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserDto {
    @Autowired
    private RoleConfig roleConfig;
    @Autowired
    public UserApi userApi;

    public String signup(SignupForm form) throws ApiException{
        ValidationUtil.validate(form);
        NormalizeUtil.normalize(form);
        Role role = roleConfig.isSupervisor(form.getEmail()) ? Role.SUPERVISOR : Role.OPERATOR;
        UserPojo pojo = DtoHelper.convertSignupFormToPojo(form, role);
        return userApi.signup(pojo);
    }


    public LoginData login(LoginForm form) throws ApiException {
        ValidationUtil.validate(form);
        NormalizeUtil.normalize(form);
        UserPojo pojo = DtoHelper.convertLoginFormToPojo(form);
        return userApi.login(pojo);
    }
}


