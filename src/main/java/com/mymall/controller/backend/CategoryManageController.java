package com.mymall.controller.backend;


import com.mymall.common.Const;
import com.mymall.common.ResponseCode;
import com.mymall.common.ServiceResponse;
import com.mymall.pojo.User;
import com.mymall.service.ICategoryService;
import com.mymall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {
    @Autowired
    IUserService iUserService;
    @Autowired
    ICategoryService iCategoryService;
    @RequestMapping("/addCategory.action")
    @ResponseBody
    public ServiceResponse addCategory(HttpSession session,String categoryName,
                                       @RequestParam(value = "parentid",defaultValue="0") int parentid){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录请登录");
        }
        //校验是不是管理员在userservice层实现
      ServiceResponse response=  iUserService.checkAdminRole(user);
        //是管理员
        if(response.isSuccess()){
          return iCategoryService.addCategory(categoryName , parentid);
        }else
            return ServiceResponse.createByErrorMessage("不是管理员无权限");
    }
    @RequestMapping("/setCategoryName.action")
    @ResponseBody
    public ServiceResponse setCategoryName(HttpSession session,Integer categoryid,String categoryName){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录请登录");
        }
        ServiceResponse response=  iUserService.checkAdminRole(user);
        //是管理员
        if(response.isSuccess()){
            return iCategoryService.updateCategoryName(categoryid , categoryName);
        }else
            return ServiceResponse.createByErrorMessage("用户无权限");
    }
    //获取平级的商品
    @RequestMapping("/getChildrenParallelCategory.action")
    @ResponseBody
    public  ServiceResponse getChildrenParallelCategory(HttpSession session,
                                                        @RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorMessage("用户不存在");
        }
        ServiceResponse response=  iUserService.checkAdminRole(user);
        if(response.isSuccess()){
            return iCategoryService.getChildrenParallelCategory(categoryId);
        }else
            return ServiceResponse.createByErrorMessage("用户无权限");
    }
    //递归获取所有子节点
    @RequestMapping("/getCategoryanddeepChildrenCategory.action")
    @ResponseBody
    public  ServiceResponse getCategoryanddeepChildrenCategory(HttpSession session,
                                                        @RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorMessage("用户不存在");
        }
        ServiceResponse response=  iUserService.checkAdminRole(user);
        if(response.isSuccess()){
            //获取当前节点及所有子节点
            return iCategoryService.getCategoryanddeepChildrenCategory(categoryId);
        }else
            return ServiceResponse.createByErrorMessage("用户无权限");
    }
}
