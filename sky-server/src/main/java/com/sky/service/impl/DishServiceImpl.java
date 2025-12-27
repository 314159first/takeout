package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    private  DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    public DishServiceImpl(DishMapper dishMapper) {
        this.dishMapper = dishMapper;
    }

    //新增菜品口味数据
    @Override
    @Transactional
    public void save(DishDTO dishDTO) {

        // 插入菜品数据
        Dish dish = new Dish();
        //属性拷贝
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insert(dish);



        //获取insert语句生成的主键值


        // 保存菜品口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0){
           flavors.forEach(dishFlavor -> dishFlavor.setDishId(dish.getId()));

        }
        //向口味表中插入
        dishFlavorMapper.insertBatch(flavors);

    }
}
