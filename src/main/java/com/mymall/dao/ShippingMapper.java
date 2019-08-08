package com.mymall.dao;

import com.mymall.pojo.Shipping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelectiveship(Shipping record);

    int updateByPrimaryKey(Shipping record);

    int delet(@Param("userid") Integer userid,@Param("shippingid") Integer shippingid);

    int update(Shipping shipping);

    Shipping select(@Param("userid")Integer userid, @Param("shippingid")Integer shippingid);

    List<Shipping> selectbyuserid(Integer userid);
}