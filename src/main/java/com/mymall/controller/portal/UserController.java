package com.mymall.controller.portal;

import com.mymall.common.Const;
import com.mymall.common.ServiceResponse;
import com.mymall.pojo.User;
import com.mymall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/")
public class UserController {
    @Autowired
    IUserService iUserService;
    @RequestMapping(value="login.action",method = RequestMethod.POST)
    @ResponseBody
    //登录方法
    public ServiceResponse<User>  login(String username, String password, HttpSession session){
        ServiceResponse<User> response=iUserService.login(username,password);
        if(response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }
    //登出方法
    @RequestMapping("logout.action")
    @ResponseBody
    public ServiceResponse<User>  logout( HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServiceResponse.createBysuccess();
    }
    @RequestMapping("register.action")
    @ResponseBody
    public ServiceResponse<String>  register(User user){
         return iUserService.register(user);
    }
    @RequestMapping("checkValid.action")
    @ResponseBody
    public ServiceResponse<String>  checkValid(String str,String type){
        return iUserService.checkValid(str,type);
    }
    @RequestMapping("getUserInfo.action")
    @ResponseBody
    public ServiceResponse<User> getUserInfo(HttpSession session){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
       if(user!=null){
           return ServiceResponse.createBysuccessMessage(user);
       }
       return ServiceResponse.createByErrorMessage("用户未登录无法获取当前用户信息");
    }
    @RequestMapping("forgetCheckAnswer.action")
    @ResponseBody
public ServiceResponse<String> forgetCheckAnswer(String answer,String question,String username){
    return iUserService.forgetCheckAnswer(answer,question,username);
}
    @RequestMapping("forgetresetpassword.action")
    @ResponseBody
    public ServiceResponse<String> forgetresetpassword(String username,String newpassword,String forgetToken){

        return  iUserService.forgetresetpassword(username,newpassword,forgetToken);
    }
    @RequestMapping("resetpassword.action")
    @ResponseBody
    public ServiceResponse<String> resetpassword(String username,String oldpassword,String newpassword,HttpSession session){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorMessage("用户未登录");
        }
        return  iUserService.resetpassword(user,oldpassword,newpassword);
    }
}
