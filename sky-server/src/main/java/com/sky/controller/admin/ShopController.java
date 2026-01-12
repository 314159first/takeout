package com.sky.controller.admin;

import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController("AdminShopController")
@RequestMapping ("/admin/shop")
public class ShopController {

    @Autowired
    private RedisTemplate  redisTemplate;

    /*
    * 设置 营业状态
    * */
    @PutMapping  ("/{status}")
    public Result  setStatus(@PathVariable Integer status) {
        log.info("设置营业状态:{}",status == 1 ? "营业中" : "打烊中");
        redisTemplate.opsForValue().set("SHOP_STATUS",status);
         return Result.success();

    }

    //  查询营业状态
     @GetMapping("/status")
     public Result<Integer> getStatus() {
        Integer shopStatus = (Integer) redisTemplate.opsForValue().get("SHOP_STATUS");
         log.info("查询营业状态:{}", shopStatus== 1 ? "营业中" : "打烊中");
        return Result.success(shopStatus);
    }



}
