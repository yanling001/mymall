package com.mymall.service.Impl;


import com.google.common.collect.Lists;
import com.mymall.common.ServiceResponse;
import com.mymall.dao.CategoryMapper;
import com.mymall.pojo.Category;
import com.mymall.service.ICategoryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {
    private Logger logger= LoggerFactory.getLogger(CategoryServiceImpl.class);
    @Autowired
    private CategoryMapper categoryMapper;
    public ServiceResponse addCategory(String categroyName,Integer parentid){
        if(parentid==null|| StringUtils.isBlank(categroyName))
            return ServiceResponse.createByErrorMessage("添加参数错误");
        Category category=new Category();
        category.setName(categroyName);
        category.setParentId(parentid);
        category.setStatus(true);//代表次分类是可用的
        int rowCount=categoryMapper.insert(category);
        if(rowCount>0){
            return ServiceResponse.createBysuccessMessage("添加成功");
        }

        return ServiceResponse.createByErrorMessage("添加失败");
    }

    public ServiceResponse updateCategoryName(Integer categoryid, String categoryName) {
        //校验categoryid
        if(categoryid==null||StringUtils.isBlank(categoryName)){
            return ServiceResponse.createByErrorMessage("参数错误");
        }
        Category category=new Category();
        category.setName(categoryName);
        category.setId(categoryid);
        int rowcount=categoryMapper.updateByPrimaryKeySelective(category);
        if(rowcount>0){
            return ServiceResponse.createBysuccessMessage("修改成功");
        }
        return ServiceResponse.createByErrorMessage("修改失败");
    }

    public ServiceResponse<List<Category>> getChildrenParallelCategory(Integer categoryId) {
        List<Category> list=categoryMapper.getChildrenParallelCategory(categoryId);
        if(CollectionUtils.isEmpty(list)){
            logger.info("未找到当前分类的子分类");
        }
        return ServiceResponse.createBysuccessMessage(list);
    }

    public ServiceResponse<List<Category>> getCategoryanddeepChildrenCategory( Integer categoryId) {
        Set<Category> childrens=new HashSet<Category>();
        findChildCategory(childrens,categoryId);
        List<Category> list= Lists.newArrayList();
        if(childrens!=null) {
          for (Category category:childrens){
              list.add(category);
          }
        }
        return  ServiceResponse.createBysuccessMessage(list);
    }

    @Override
    public ServiceResponse getCategoryanddeepChildrenCategorybyid(Integer id) {

        return null;
    }

    public  Set<Category> findChildCategory(Set<Category>childrens, Integer categoryId){
        Category category1=categoryMapper.selectByPrimaryKey(categoryId);
        if(category1!=null){
                     childrens.add(category1);
        }
        //以当前节点的id为父id找儿子集合
        List<Category> list= categoryMapper.getChildrenParallelCategory(categoryId);
        for (Category category:list){
            findChildCategory(childrens,category.getId());
        }
        return childrens;
    }
}
