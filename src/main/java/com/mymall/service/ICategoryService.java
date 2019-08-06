package com.mymall.service;

import com.mymall.common.ServiceResponse;
import com.mymall.pojo.Category;

import java.util.List;
import java.util.Set;

public interface ICategoryService {
    public ServiceResponse addCategory(String categroyName, Integer parentid);

    public ServiceResponse updateCategoryName(Integer categoryid, String categoryName);

    ServiceResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);

    ServiceResponse<List<Category>> getCategoryanddeepChildrenCategory(Integer categoryId);

    ServiceResponse<List<Integer>>  getCategoryanddeepChildrenCategorybyid(Integer id);
}
