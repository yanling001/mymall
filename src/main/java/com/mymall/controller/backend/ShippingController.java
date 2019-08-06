package com.mymall.controller.backend;


import com.mymall.common.Const;
import com.mymall.common.ResponseCode;
import com.mymall.common.ServiceResponse;
import com.mymall.pojo.Shipping;
import com.mymall.pojo.User;
import com.mymall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/shipping")
public class ShippingController {
    @Autowired
    private IShippingService iShippingService;
    @ResponseBody
    @RequestMapping("/add.action")
    //添加商品
    public ServiceResponse add(HttpSession session, Shipping shipping){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录请登录");
        }
        return iShippingService.add(user.getId(),shipping);
    }
    @ResponseBody
    @RequestMapping("/delet.action")
    //删除商品
    public ServiceResponse delet(HttpSession session, Integer shippingid){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录请登录");
        }
        return iShippingService.delet(user.getId(),shippingid);
    }
    @ResponseBody
    @RequestMapping("/update.action")
    //更新商品
    public ServiceResponse update(HttpSession session, Shipping shipping){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录请登录");
        }
        return iShippingService.update(user.getId(),shipping);
    }
    @ResponseBody
    @RequestMapping("/select.action")
    //商品列表
    public ServiceResponse select(HttpSession session, Integer shippingid){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录请登录");
        }
        return iShippingService.select(user.getId(),shippingid);
    }
    @ResponseBody
    @RequestMapping("/list.action")
    //商品分页
    public ServiceResponse list(HttpSession session, @RequestParam(value = "pagenum" ,defaultValue = "1") Integer pagenum,
                                @RequestParam(value = "pagesize",defaultValue = "10") Integer pagesize){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录请登录");
        }
        return iShippingService.list(user.getId(),pagenum,pagesize);
    }
}


