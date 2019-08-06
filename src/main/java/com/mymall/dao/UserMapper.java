package com.mymall.dao;

import com.mymall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUsername(String username);

    User selectLogin(@Param("username") String username, @Param("password") String password);

    int checkEmail(String email);

    String selectQuestionByUsername(String username);

    int forgetCheckAnswer(@Param("username") String username, @Param("question") String question, @Param("answer") String answer);

    int updateuserpassword(@Param("username") String username, @Param("newpassword") String newpassword);

    int checkPassword(@Param("id") Integer id, @Param("oldpassword") String oldpassword);
}