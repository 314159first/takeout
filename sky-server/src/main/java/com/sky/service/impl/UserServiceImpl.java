package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMaapper;
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        //调用微信接口服务，获得当前用户的openid
        Map<String, String> map = new HashMap<>() ;
        map.put("appid", "wx3fa00966d1f3e396");
        map.put("secret", "4902ed5111baf8d14e99e18f025b50bd");
        map.put("js_code", userLoginDTO.getCode());
        map.put("grant_type", "authorization_code");
        String json=HttpClientUtil.doGet("https://api.weixin.qq.com/sns/jscode2session", map);
        JSONObject jsonObject = JSONObject.parseObject(json);
        String openid=jsonObject.getString("openid");


        //判断openid是否为空，如果为空则抛出异常
        if(openid==null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        //判断当前用户是否为新用户
        User user=userMaapper.getByOpenid(openid);
        // 如果是新用户，自动完成注册
        if (user==null)
        {
            user=User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMaapper.insert(user);
        }



        //返回用户对象
        return user;
    }
}
