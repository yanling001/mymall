package com.mymall.service;

import com.mymall.common.ServiceResponse;

import java.util.List;

public interface ICartService {
    ServiceResponse add(Integer userid, Integer count, Integer productid);

    ServiceResponse update(Integer id, Integer count, Integer productid);

    ServiceResponse deleteproduct(Integer userid, String productids);

    ServiceResponse list(Integer id);

    ServiceResponse selectAll(Integer userid, Integer checked, Integer productid);

    ServiceResponse<Integer> selectCartproductCount(Integer userid);
}
