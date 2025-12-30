package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    void save(DishDTO dishDTO);

    PageResult select(DishPageQueryDTO dishPageQueryDTO);

    void deleteBatch(List<Long> ids);
    /*
    * 根据id查询菜品以及对应口味数据
    * */
    DishVO getByWithFlavor(Long id);

    void update(DishDTO dishDTO);
}
