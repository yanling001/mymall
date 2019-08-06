package com.mymall.service.Impl;

import ch.qos.logback.classic.gaffer.PropertyUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mymall.common.Const;
import com.mymall.common.ServiceResponse;
import com.mymall.dao.CategoryMapper;
import com.mymall.dao.ProductMapper;
import com.mymall.pojo.Category;
import com.mymall.pojo.Product;
import com.mymall.service.ICartService;
import com.mymall.service.ICategoryService;
import com.mymall.service.IProductService;
import com.mymall.util.PropertiesUtil;
import com.mymall.vo.ProductDetailVo;
import com.mymall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
   ICategoryService iCategoryService;
    //保存或者更新产品
    public ServiceResponse saveOrUpdateProduct(Product product){
        if(product==null){
            return ServiceResponse.createByErrorMessage("商品为空");
        }else{
            //如果商品子图不为空用第一个子图来做主图
            if(StringUtils.isNotBlank(product.getSubImages())){
                String [] subimages=product.getSubImages().split(",");
                if(subimages.length>0)
                    product.setMainImage(subimages[0]);
            }
        }
        //如果id不为null则是update
        if(product.getId()!=null){
            //update数据
            int rowcount=productMapper.updateByPrimaryKey(product);
            if(rowcount>0)
                return ServiceResponse.createBysuccessMessage("更新产品成功");
            return ServiceResponse.createByErrorMessage("更新产品失败");
        }else{
            //insert数据
            int rowcount=productMapper.insert(product);
            if(rowcount>0)
                return ServiceResponse.createBysuccessMessage("插入产品成功");
            return ServiceResponse.createByErrorMessage("插入产品失败");
        }
    }

    public ServiceResponse setStatus(Integer id, Integer status) {
        if(id==null||status==null){
            return ServiceResponse.createByErrorMessage("参数为空");
        }
        Product product=new Product();
        product.setStatus(status);
        product.setId(id);
        int rowcount=productMapper.updateByPrimaryKeySelective(product);
        if(rowcount>0)
        return  ServiceResponse.createBysuccessMessage("上下架商品成功");
        else
            return ServiceResponse.createByErrorMessage("上下架商品失败");
    }

    public ServiceResponse<ProductDetailVo> managePoductDetail(Integer id) {
        if(id==null){
            return ServiceResponse.createByErrorMessage("参数错误");
        }
        Product product=productMapper.selectByPrimaryKey(id);
        if(product==null){
            return ServiceResponse.createByErrorMessage("商品id错误，未找到商品");
        }
        //因为业务逻辑需求pojo->bo(business object)->vo(view object)来组装pojo
        return ServiceResponse.createBysuccessMessage(assembleProductDetailVo(product));
    }

    //分页查询
    public ServiceResponse<PageInfo> getProductList(Integer pageNum, Integer pageSize) {
       //<groupId>com.github.pagehelper</groupId>使用此jar包实现分页
        //1.startpage-->start
        PageHelper.startPage(pageNum,pageSize);
        //2.填充自己的Sql查询
        List<Product> productList=productMapper.selectlist();
        List<ProductListVo> productListVos= Lists.newArrayList();
        for (Product product:productList){
            ProductListVo productListVo=assembleProductListVo(product);
            productListVos.add(productListVo);
        }
        //3.pageHelper--收尾
        PageInfo pageInfo=new PageInfo(productList);
        pageInfo.setList(productListVos);
        return ServiceResponse.createBysuccessMessage(pageInfo);
    }
    //也要实现分页
    public ServiceResponse<PageInfo> searchProductList(String productname, Integer productid, Integer pageNum, Integer pageSize) {
       //模糊查询
        if(StringUtils.isNotBlank(productname)){
            //%手机%
            productname=new StringBuilder().append("%").append(productname).append("%").toString();
        }
        //1.startpage-->start
        PageHelper.startPage(pageNum,pageSize);
        //2.填充自己的Sql查询
        List<Product> list=productMapper.selectByNameAndProductId(productid,productname);
        List<ProductListVo> productListVos= Lists.newArrayList();
        for (Product product:list){
            ProductListVo productListVo=assembleProductListVo(product);
            productListVos.add(productListVo);
        }
        //3.pageHelper--收尾
        PageInfo pageInfo=new PageInfo(list);
        pageInfo.setList(productListVos);
        return ServiceResponse.createBysuccessMessage(pageInfo);
    }

    @Override
    public ServiceResponse getProductDetail(Integer productid) {
        if (productid==null){
            return ServiceResponse.createBysuccessMessage("参数为空");
        }
        Product product=productMapper.selectByPrimaryKey(productid);
        if(product==null){
            return ServiceResponse.createByErrorMessage("产品已不存在以下架");
        }
        if(product.getStatus()!= Const.ProductStatusEnum.ON_SALE.getCode()){
            return ServiceResponse.createByErrorMessage("产品已下架");
        }
        return ServiceResponse.createBysuccessMessage(assembleProductDetailVo(product));
    }

    @Override
    public ServiceResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId, int pagenum, int pagesize,String orderBy) {
      //参数校验
        if(keyword==null&& categoryId==null)
            return ServiceResponse.createByErrorMessage("参数错误");
        PageHelper.startPage(pagenum,pagesize);
       //执行业务逻辑  返回list
        Category category=categoryMapper.selectByPrimaryKey(categoryId);
        if(category==null){
            //返回空分页
            List<ProductListVo> list=Lists.newArrayList();
           PageInfo pageInfo=new PageInfo(list);
           return ServiceResponse.createBysuccessMessage(pageInfo);
        }
        //category不为空  找到他的所有子分类
        List<Category>  categorylist  =Lists.newArrayList();
        categorylist=iCategoryService.getCategoryanddeepChildrenCategory(category.getId()).getData();
       if(StringUtils.isNotBlank(keyword)){
            keyword=new StringBuilder().append("%").append(keyword).append("%").toString();
       }
       //排序处理
        if (StringUtils.isNotBlank(orderBy)) {
            //Const.ProductListOrderBy.PRICE_ASC_DESC. 是一个set集合有两个（"price_desc","price_asc"）分别表示升降序
            if(Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                String[] orderByArray=orderBy.split("_");
                //使用PageHelper来实现排序处理
                PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);
            }
        }
        List<Product> productList=productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword)?null:keyword,
                categorylist.size()==0?null:categorylist);
        List<ProductListVo> productListVoList=Lists.newArrayList();
        for (Product product: productList){
            ProductListVo productListVo=assembleProductListVo(product);
            productListVoList.add(productListVo);
        }
        //分页
        PageInfo pageInfo=new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServiceResponse.createBysuccessMessage(pageInfo);
    }


    private ProductListVo assembleProductListVo(Product product) {
        ProductListVo productListVo=new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setCategoryId(product.getCategoryId());
          //设置文件服务器前缀
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());
        return  productListVo;
    }

    private ProductDetailVo assembleProductDetailVo(Product product) {
        ProductDetailVo productDetailVo=new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStock(product.getStock());

        //vo独有字段
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        Category category=categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category==null){
            //默认为根节点
            productDetailVo.setParentCateotyId(0);
        }else
            productDetailVo.setParentCateotyId(category.getParentId());
        return productDetailVo;
    }
}
