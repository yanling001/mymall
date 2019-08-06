package com.mymall.controller.backend;


import com.mymall.common.Const;
import com.mymall.common.ServiceResponse;
import com.mymall.pojo.User;
import com.mymall.service.ICartService;
import com.mymall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@RequestMapping("/cart/")
@Controller
//购物车模块
public class CartController {
      @Autowired
      IUserService iUserService;
      @Autowired
    ICartService iCartService;
      @RequestMapping("add.action")
      @ResponseBody
    //购物车添加商品
   public ServiceResponse add(HttpSession session,Integer count,Integer productid){
       //校验用户  权限校验
       User user=(User) session.getAttribute(Const.CURRENT_USER);
       if(user==null){
           return ServiceResponse.createByErrorMessage("用户未登录");
       }
       if(iUserService.checkAdminRole(user).isSuccess()){
           return iCartService.add(user.getId(),count,productid);
       }else
       return ServiceResponse.createByErrorMessage("用户权限不够");
   }
    @RequestMapping("update.action")
    @ResponseBody
    //更新购物车的数据
    public ServiceResponse update(HttpSession session,Integer count,Integer productid){
        //校验用户  权限校验
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorMessage("用户未登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iCartService.update(user.getId(),count,productid);
        }else
            return ServiceResponse.createByErrorMessage("用户权限不够");
    }
    @RequestMapping("deleteproduct.action")
    @ResponseBody
    //更新购物车的数据
    public ServiceResponse deleteproduct(HttpSession session,String productids){
        //校验用户  权限校验
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorMessage("用户未登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iCartService.deleteproduct(user.getId(),productids);
        }else
            return ServiceResponse.createByErrorMessage("用户权限不够");
    }
    //查询购物车
    @RequestMapping("selectcart.action")
    @ResponseBody
    public ServiceResponse selectcart(HttpSession session){
        //校验用户  权限校验
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorMessage("用户未登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iCartService.list(user.getId());
        }else
            return ServiceResponse.createByErrorMessage("用户权限不够");
    }

    //全选
    @RequestMapping("selectall.action")
    @ResponseBody
    public ServiceResponse selectAll(HttpSession session){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorMessage("用户未登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iCartService.selectAll(user.getId(),Const.Cart.CHECKED,null);
        }else
            return ServiceResponse.createByErrorMessage("用户权限不够");
    }
    //全反选
    @RequestMapping("unselectall.action")
    @ResponseBody
    public ServiceResponse unselectAll(HttpSession session){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorMessage("用户未登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iCartService.selectAll(user.getId(),Const.Cart.UN_CHECKED,null);
        }else
            return ServiceResponse.createByErrorMessage("用户权限不够");
    }
    //单独选产品数
    @RequestMapping("select.action")
    @ResponseBody
    public ServiceResponse select(HttpSession session,Integer productid){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorMessage("用户未登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iCartService.selectAll(user.getId(),Const.Cart.CHECKED,productid);
        }else
            return ServiceResponse.createByErrorMessage("用户权限不够");
    }
    //单独反选
    @RequestMapping("unselect.action")
    @ResponseBody
    public ServiceResponse unselect(HttpSession session,Integer productid){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorMessage("用户未登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iCartService.selectAll(user.getId(),Const.Cart.UN_CHECKED,productid);
        }else
            return ServiceResponse.createByErrorMessage("用户权限不够");
    }
   //查询当前用户购物车里的产品数量
   @RequestMapping("getCartproductCount.action")
   @ResponseBody
   public ServiceResponse<Integer> getCartproductCount(HttpSession session,Integer userid){
       User user=(User) session.getAttribute(Const.CURRENT_USER);
       if(user==null){
           return ServiceResponse.createBysuccessMessage(0);
       }
       if(iUserService.checkAdminRole(user).isSuccess()){
           return iCartService.selectCartproductCount(userid);
       }else
           return ServiceResponse.createByErrorMessage("用户权限不够");

   }

}
