package com.mymall.service;

import com.github.pagehelper.PageInfo;
import com.mymall.common.ServiceResponse;
import com.mymall.pojo.Product;
import com.mymall.vo.ProductDetailVo;

public interface IProductService {
    public ServiceResponse saveOrUpdateProduct(Product product);

    ServiceResponse setStatus(Integer id, Integer status);

    ServiceResponse<ProductDetailVo> managePoductDetail(Integer id);

    ServiceResponse getProductList(Integer pageNum, Integer pageSize);


    ServiceResponse<PageInfo> searchProductList(String productname, Integer productid, Integer pageNum, Integer pageSize);

    ServiceResponse getProductDetail(Integer productid);

    ServiceResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId, int pagenum, int pagesize,String orderBy);
}
