package com.increff.pos.dto;

import com.increff.pos.api.UserApi;
import com.increff.pos.config.RoleConfig;
import com.increff.pos.dto.helper.DtoHelper;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.LoginData;
import com.increff.pos.model.enums.Role;
import com.increff.pos.model.form.LoginForm;
import com.increff.pos.model.form.SignupForm;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.util.JwtUtil;
import com.increff.pos.util.NormalizeUtil;
import com.increff.pos.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserDto {
    @Autowired
    private RoleConfig roleConfig;
    @Autowired
    private UserApi userApi;
//todo -
    public String signup(SignupForm form) throws ApiException {
        ValidationUtil.validate(form);
        NormalizeUtil.normalize(form);
        return userApi.signup(DtoHelper.convertSignupFormToPojo(form, roleConfig.isSupervisor(
            form.getEmail()) ? Role.SUPERVISOR : Role.OPERATOR));
    }

    public LoginData login(LoginForm form) throws ApiException {
        ValidationUtil.validate(form);
        NormalizeUtil.normalize(form);
        UserPojo user = userApi.login(DtoHelper.convertLoginFormToPojo(form));
        JwtUtil jwtUtil = new JwtUtil();
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        return DtoHelper.convertUserPojoToLoginData(user, token);
    }
}


