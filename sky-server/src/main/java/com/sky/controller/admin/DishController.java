package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @PostMapping
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品:{}",dishDTO);
        dishService.save(dishDTO);
        return Result.success();
    }
/*
* 菜品分类
* */
    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分类：{}",dishPageQueryDTO);
        PageResult pageResult = dishService.select(dishPageQueryDTO);
        return Result.success(pageResult);


    }
    /*
    * 批量删除菜品
    * */
    @DeleteMapping()
    public Result delete(@RequestParam List<Long> ids){
        log.info("删除菜品：{}",ids);
        dishService.deleteBatch(ids);
        return Result.success();
    }

    /*
    * 查询回显修改菜品数据
    * */
    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("查询回显修改菜品数据id：{}",id);
        DishVO dishVO = dishService.getByWithFlavor(id);
        return Result.success(dishVO);

    }

    /*
    * 修改菜品
    * */
    @PutMapping
    public Result updatewithFlavor(@RequestBody DishDTO dishDTO){
        log.info("修改菜品：{}",dishDTO);
        dishService.update(dishDTO);
        return Result.success();
    }

}
