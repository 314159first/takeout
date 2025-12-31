package com.sky.mapper;

import com.sky.annotation.Autofill;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetMealDishMapper {

    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);
    /*
    * 插入套餐和菜品的关联关系
    * */
    @Autofill(value = OperationType.INSERT)
    void insertBatch(List<SetmealDish> setmealDishes);

    @Delete("delete from setmeal_dish where setmeal_id = #{id}")
    void deleteBySetmealId(Long id);

    List<SetmealDish> getBySetmealId(Long id);
}
