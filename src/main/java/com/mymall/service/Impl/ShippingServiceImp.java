package com.mymall.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mymall.common.ServiceResponse;
import com.mymall.dao.ShippingMapper;
import com.mymall.pojo.Shipping;
import com.mymall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("iShippingService")
public class ShippingServiceImp  implements IShippingService {
    @Autowired
    ShippingMapper shippingMapper;
    @Override
    public ServiceResponse add(Integer id, Shipping shipping) {
        shipping.setUserId(id);
        //将用户传来的商品加入数据库后要返回商品的主键id
        //修改mapper.xml中的配置即可
        int rowcount=shippingMapper.insert(shipping);
        if(rowcount>0){
            shipping.setId(rowcount);
            Map map= Maps.newHashMap();
            map.put("shippingid",shipping.getId());
        }
        return ServiceResponse.createByErrorMessage("商品上传错误");
    }

    @Override
    public ServiceResponse delet(Integer userid, Integer shippingid) {
        //穿userid的目的防止横向越权
       // 校验userid
        int rowcount=shippingMapper.delet(userid,shippingid);
        if (rowcount>0)
            return ServiceResponse.createBysuccessMessage("删除信息成功");
        else
            return  ServiceResponse.createByErrorMessage("删除错信息失败");

    }

    @Override
    public ServiceResponse update(Integer userid, Shipping shipping) {
        shipping.setUserId(userid);
        int rowcount = shippingMapper.update(shipping);
        if (rowcount>0)
            return ServiceResponse.createBysuccessMessage("更改信息成功");
        else
            return  ServiceResponse.createByErrorMessage("更改信息失败");

    }

    @Override
    public ServiceResponse select(Integer userid, Integer shippingid) {
        Shipping shipping=shippingMapper.select(userid,shippingid);
        if (shipping==null){
            return ServiceResponse.createByErrorMessage("商品未查找到");
        }
        return ServiceResponse.createBysuccessMessage("查找成功",shipping);
    }

    @Override
    public ServiceResponse<PageInfo> list(Integer userid, Integer pagenum, Integer pagesize) {
        PageHelper.startPage(pagenum,pagesize);
        List<Shipping> list=shippingMapper.selectbyuserid(userid);
        PageInfo pageInfo=new PageInfo(list);
        return ServiceResponse.createBysuccessMessage(pageInfo);
    }
}
