package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;

import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;


public interface SetMealService {
     PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    void saveWithDish(SetmealDTO setmealDTO);


    void startOrStop(Integer status, Long id);

    /*
    * 批量删除套餐
    * */
    void delete(List<Long> ids);


    /*
    * 查询回显修改套餐数据
    * */
    SetmealVO getByIdWithDish(Long id);

    void updateWithDish(SetmealDTO setmealDTO);
}
