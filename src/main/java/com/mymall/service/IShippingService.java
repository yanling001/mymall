package com.mymall.service;

import com.github.pagehelper.PageInfo;
import com.mymall.common.ServiceResponse;
import com.mymall.pojo.Shipping;

import java.util.List;

public interface IShippingService {
    ServiceResponse add(Integer id, Shipping shipping);

    ServiceResponse delet(Integer id, Integer shippingid);

    ServiceResponse update(Integer id, Shipping shipping);

    ServiceResponse select(Integer id, Integer shippingid);

    ServiceResponse<PageInfo> list(Integer id, Integer pagenum, Integer pagesize);
}
