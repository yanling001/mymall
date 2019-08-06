package com.mymall.service.Impl;

import com.mymall.common.Const;
import com.mymall.common.ServiceResponse;
import com.mymall.common.TokenCache;
import com.mymall.dao.UserMapper;
import com.mymall.pojo.User;
import com.mymall.service.IUserService;
import com.mymall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service("iUserService")
public class UserServiceimpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    public ServiceResponse<User> login(String username, String password) {
        int resultcount = userMapper.checkUsername(username);
        if (resultcount == 0) {
            return ServiceResponse.createByErrorMessage("用户不存在");
        }
        // MD5加密验证密码
        String MD5password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, MD5password);
        if (user == null) {
            return ServiceResponse.createByErrorMessage("密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServiceResponse.createBysuccessMessage("登录成功", user);

    }

    public ServiceResponse<String> register(User user) {
        //校验用户名是否存在
        ServiceResponse serviceResponse=this.checkValid(user.getUsername(),Const.USERNAME);
        if(!serviceResponse.isSuccess()){
            return serviceResponse;
        }
        serviceResponse=this.checkValid(user.getEmail(),Const.EMAIL);
        if(!serviceResponse.isSuccess()){
            return serviceResponse;
        }
        //校验........
        //设置用户权限
        user.setRole(Const.Role.ROLE_CUTOMER);
        //对密码进行MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int    resultcount = userMapper.insert(user);
        if (resultcount == 0) {
            return ServiceResponse.createByErrorMessage("注册失败");
        }
        return ServiceResponse.createBysuccessMessage("注册成功");
    }

    public ServiceResponse<String> checkValid(String str, String tye) {
        if (!StringUtils.isBlank(tye)) {
            //开始交验
            int resultcount;
            if (Const.USERNAME.equals(tye)) {
                resultcount = userMapper.checkUsername(str);
                if (resultcount > 0) {
                    return ServiceResponse.createByErrorMessage("用户名已存在");
                }
            }
                if (Const.EMAIL.equals(tye)) {
                    resultcount = userMapper.checkEmail(str);
                    if (resultcount > 0) {
                        return ServiceResponse.createByErrorMessage("用户名已存在");
                    }
                }

        }else {
            return ServiceResponse.createByErrorMessage("参数错误");
        }
        return ServiceResponse.createBysuccessMessage("校验成功");
    }
    public ServiceResponse selectQuestion(String username){
        //校验用户
       if(checkValid(username,Const.USERNAME).isSuccess()){
          //用户不存在
          return ServiceResponse.createByErrorMessage("用户不存在");
       }
       String question= userMapper.selectQuestionByUsername(username);
       if(StringUtils.isNotBlank(question)){
           return ServiceResponse.createBysuccessMessage(question);
       }
       return ServiceResponse.createByErrorMessage("找回密码的问题是空的");
    }
    public ServiceResponse<String> forgetCheckAnswer(String answer,String question,String username){
        int resultCount=userMapper.forgetCheckAnswer(username,question,answer);
        if(resultCount>0){
            String forgetToken= UUID.randomUUID().toString();//把forgetToken放到缓存中
            TokenCache.setKey("token_"+username,forgetToken);
            return ServiceResponse.createBysuccessMessage(forgetToken);
        }
        return ServiceResponse.createByErrorMessage("问题的答案错误");
    }

    public ServiceResponse<String> forgetresetpassword(String username, String newpassword, String forgetToken) {
        if(StringUtils.isBlank(forgetToken)){
            return ServiceResponse.createByErrorMessage("参数错误 ， token 需要传递");
        }
        String token =TokenCache.getKey("token_"+"username");
        if(StringUtils.isBlank(token)){
            return ServiceResponse.createByErrorMessage("用户名参数错误 ，找不到 token或token已过期");
        }
        if(StringUtils.equals(forgetToken,token)){
            //重置密码
          int resultCount=  userMapper.updateuserpassword(username,MD5Util.MD5EncodeUtf8(newpassword));
          if(resultCount>0)
              return ServiceResponse.createBysuccessMessage("成功修改密码");
        }else
        return ServiceResponse.createByErrorMessage("修改密码失败token错误");
        return ServiceResponse.createByErrorMessage("修改密码失败");
    }

    public ServiceResponse<String> resetpassword(User user, String oldpassword, String newpassword) {
        //校验旧密码 防止横向越权
        int resultcount=userMapper.checkPassword(user.getId(),MD5Util.MD5EncodeUtf8(oldpassword));
        if(resultcount>0){
            //修改新密码
            user.setPassword(MD5Util.MD5EncodeUtf8(newpassword));
            resultcount=userMapper.updateByPrimaryKeySelective(user);
            if(resultcount>0)
                return ServiceResponse.createBysuccessMessage("密码修改成功");
            else
                return ServiceResponse.createByErrorMessage("密码修改失败请重试");
        }
        else
        return ServiceResponse.createByErrorMessage("旧密码输入错误");
    }

    public ServiceResponse checkAdminRole(User user) {
        if(user!=null && user.getRole().intValue()==Const.Role.ROLE_ADMIN)
            return ServiceResponse.createBysuccess();
        else
            return  ServiceResponse.createByError();
    }
}
