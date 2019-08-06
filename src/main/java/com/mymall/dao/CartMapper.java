package com.mymall.dao;

import com.mymall.common.ServiceResponse;
import com.mymall.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selectCartByUserIdProductId(@Param("userid") Integer userid, @Param("productid") Integer productid);

    List<Cart> selectCartByUserId(Integer userid);

    int  selectCartProductCheckedStatusByUserId(Integer userid);

    int deleteproduct(Integer userid, List<String> productList);

    int checkOrUncheckedProduct(@Param("userid") Integer userid, @Param("checked") Integer checked,
                                @Param("productid") Integer productid);

    int selectCartproductCount(Integer userid);
}