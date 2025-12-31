package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;

import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetMealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
public class SetMealController {
    @Autowired
    private  SetMealService setMealService;
    //分类查询菜品数据
    @GetMapping("/page")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("分页查询:{}",setmealPageQueryDTO);
        PageResult pageResult = setMealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    /*
    * 新增套餐
    * */
    @PostMapping
    public Result save(@RequestBody SetmealDTO setmealDTO){
        log.info("新增套餐：{}",setmealDTO);
        setMealService.saveWithDish(setmealDTO);
        return Result.success();
    }

    /*
    * 套餐起售停售
    * */
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status,Long id){
        log.info("套餐起售停售：{},{}",status,id);
        setMealService.startOrStop(status,id);
        return Result.success();
    }


    /*
    * 批量删除套餐
    * */
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids){
        log.info("批量删除：{}",ids);
        setMealService.delete(ids);
        return Result.success();
    }


    /*
    * 查询修改时返回套餐数据
    * */
    @GetMapping("/{id}")
    public Result<SetmealVO> getById(@PathVariable Long id){
        log.info("查询修改时返回套餐数据：{}",id);
        SetmealVO setmealDTO = setMealService.getByIdWithDish(id);
        return Result.success(setmealDTO);
    }

    /*
    * 修改套餐数据
    * */
    @PutMapping
    public Result update(@RequestBody SetmealDTO setmealDTO){
        log.info("修改套餐数据：{}",setmealDTO);
        setMealService.updateWithDish(setmealDTO);
        return Result.success();
    }

}
