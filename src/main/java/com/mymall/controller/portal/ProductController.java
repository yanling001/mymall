package com.mymall.controller.portal;


import com.github.pagehelper.PageInfo;
import com.mymall.common.ServiceResponse;
import com.mymall.dao.ProductMapper;
import com.mymall.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

@RequestMapping("/product/")
@Controller
public class ProductController {
    @Autowired
    IProductService iProductService;
    @RequestMapping("detail.action")
    @ResponseBody
    //获取产品详细信息
    public ServiceResponse detail(Integer productid){
        return iProductService.getProductDetail(productid);
    }
    @RequestMapping("list.action")
    @ResponseBody
    //用户搜索的list分页
    public ServiceResponse<PageInfo> list(@RequestParam(value ="keyword" ,required = false) String keyword,
                                          @RequestParam(value ="categoryId",required = false) Integer categoryId,
                                           @RequestParam(value="pagenum",defaultValue = "1") int pagenum,
                                          @RequestParam(value = "pagesize",defaultValue = "10") int pagesize,
                                          @RequestParam(value ="orderBy",defaultValue = "")String orderBy){

        return iProductService.getProductByKeywordCategory(keyword,categoryId,pagenum,pagesize,orderBy);
    }

}
