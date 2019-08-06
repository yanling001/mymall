package com.mymall.service;

import com.mymall.common.ServiceResponse;
import com.mymall.pojo.User;

public interface IUserService {
    public ServiceResponse<User> login(String username, String password);
    public ServiceResponse<String> register(User user);
    public ServiceResponse<String> checkValid(String str, String tye);
    public ServiceResponse<String> forgetCheckAnswer(String answer, String question, String username);

    public ServiceResponse<String> forgetresetpassword(String username, String newpassword, String forgetToken);

    public  ServiceResponse<String> resetpassword(User user, String oldpassword, String newpassword);

    ServiceResponse checkAdminRole(User user);
}
