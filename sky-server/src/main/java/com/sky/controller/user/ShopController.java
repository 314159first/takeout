package com.sky.controller.user;

import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController("UserShopController")
@RequestMapping ("/user/shop")
public class ShopController {


    @Autowired
    private RedisTemplate  redisTemplate;
    //  查询营业状态
     @GetMapping("/status")
     public Result<Integer> getStatus() {
        Integer shopStatus = (Integer) redisTemplate.opsForValue().get("SHOP_STATUS");
         log.info("查询营业状态:{}", shopStatus== 1 ? "营业中" : "打烊中");
        return Result.success(shopStatus);
    }

}
