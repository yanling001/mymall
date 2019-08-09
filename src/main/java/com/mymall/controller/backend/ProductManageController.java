package com.mymall.controller.backend;


import com.google.common.collect.Maps;
import com.mymall.common.Const;
import com.mymall.common.ServiceResponse;
import com.mymall.pojo.Product;
import com.mymall.pojo.User;

import com.mymall.service.IFileService;
import com.mymall.service.IProductService;
import com.mymall.service.IUserService;
import com.mymall.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

//商品主要包括商品图像上传
@Controller
@RequestMapping("/manage/product")
public class ProductManageController {
    @Autowired
    IFileService iFileService;
     @Autowired
    IUserService iUserService;
     @Autowired
    IProductService iProductService;

     @RequestMapping("/productSave.action")
     @ResponseBody
    public ServiceResponse productSave(HttpSession session, Product product){
        //校验用户  权限校验
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorMessage("用户未登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //是管理员  增加产品
           return iProductService.saveOrUpdateProduct(product);
        }else
            return  ServiceResponse.createByErrorMessage("无权限操作");
    }
    //设置商品的上下架（状态status）
    @RequestMapping("/setStatus.action")
    @ResponseBody
    public ServiceResponse setStatus(HttpSession session, Integer id, Integer status){
        //校验用户  权限校验
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorMessage("用户未登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.setStatus(id,status);
        }else
            return  ServiceResponse.createByErrorMessage("无权限操作");
    }
    @RequestMapping("/getDetail.action")
    @ResponseBody
    //通过id获取产品详细信息
    public ServiceResponse getDetail(HttpSession session, Integer id){
        //校验用户  权限校验
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorMessage("用户未登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.managePoductDetail(id);
        }else
            return  ServiceResponse.createByErrorMessage("无权限操作");
    }
    @RequestMapping("/getList.action")
    @ResponseBody
    //实现产品的分页
    // <groupId>com.github.pagehelper</groupId>使用此jar包实现分页
    public ServiceResponse getList(HttpSession session,@RequestParam(value="pageNum",defaultValue="10")Integer pageNum,
                                   @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        //校验用户  权限校验
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorMessage("用户未登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.getProductList(pageNum,pageSize);
        }else
            return  ServiceResponse.createByErrorMessage("无权限操作");
    }


    @RequestMapping("/searchProduct.action")
    @ResponseBody
    public ServiceResponse searchProduct(HttpSession session,String productname,Integer productid,
                                         @RequestParam(value="pageNum",defaultValue="10")Integer pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        //校验用户  权限校验
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorMessage("用户未登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.searchProductList(productname,productid,pageNum,pageSize);
        }else
            return  ServiceResponse.createByErrorMessage("无权限操作");
    }

    @RequestMapping("/uplod.action")
    @ResponseBody
    //文件上传
    public ServiceResponse uplod(HttpServletRequest request, @RequestParam("file") MultipartFile file){
        //校验用户  权限校验
        //User user=(User) session.getAttribute(Const.CURRENT_USER);
        //if(user==null){
          //  return ServiceResponse.createByErrorMessage("用户未登录");
        //}
        //if(iUserService.checkAdminRole(user).isSuccess()){
            //String path =request.getSession().getServletContext().getRealPath("upload");
            String targetFileName=iFileService.uplod(file,"/developer/image");
            String url= PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
            System.out.println(url);
           //将uri和url当做key存入map中
            Map filemap= Maps.newHashMap();
            filemap.put("uri",targetFileName);
            filemap.put("url",url);
            return ServiceResponse.createBysuccessMessage(filemap);
        //}else
          //  return  ServiceResponse.createByErrorMessage("无权限操作");
    }
    //富文本的文件上传
    //富文本上传采用前端插件simditor 进行上传
    //simditor的 前端约定
    /*
       "success" :
       "msg"     :
       "file_path" :
     */
    @RequestMapping("/richtextImageuplod")
    @ResponseBody
    public Map richtextImageuplod(HttpServletRequest request, HttpServletResponse response, @RequestParam("file") MultipartFile file, HttpSession session) {
        Map map=Maps.newHashMap();
        //校验用户  权限校验
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
             map.put("success",false);
             map.put("msg","请先登录");
             return map;
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            String path = request.getSession().getServletContext().getRealPath("uplod");
            String targetFileName = iFileService.uplod(file, path);
            if(StringUtils.isBlank(targetFileName))
            {
                map.put("success",false);
                map.put("msg","上传图片失败");
                return map;
            }
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix");
            //将uri和url当做key存入map中
             map.put("success",true);
            map.put("msg", "上传成功");
            map.put("file_path", url);
            return map;
        } else {
            map.put("success",false);
            map.put("msg","请管理员登录");
            return map;
        }

    }
}
