package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.UserAddress;
import com.atguigu.gmall.bean.UserInfo;
import java.util.List;

public interface UserService {

    List<UserInfo> getUserInfoListAll();

    void addUser(UserInfo userInfo);

    void updateUser(UserInfo userInfo);

    void updateUserByName(String name, UserInfo userInfo);

    void delUser(UserInfo userInfo);

    List<UserAddress> findAddressByUserId(String userId);

    UserInfo login(UserInfo userInfo);

    UserInfo verify(String userId);
}
