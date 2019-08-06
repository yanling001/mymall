package com.mymall.dao;

import com.mymall.pojo.Category;
import com.mymall.pojo.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    List<Product> selectlist();

    List<Product> selectByNameAndProductId(Integer productid, String productname);

    List<Product> selectByNameAndCategoryIds(@Param("productName")String productName,@Param("categoryIdList") List<Category> categoryIdList);
}