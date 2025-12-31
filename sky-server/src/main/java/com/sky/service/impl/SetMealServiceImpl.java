package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;

import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetMealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class SetMealServiceImpl implements SetMealService {
    @Autowired
    private SetMealMapper setMealMapper;
    @Autowired
    private SetMealDishMapper setMealDishMapper;
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {

        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setMealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());

    }


    /*
    * 新增套餐
    * */
    @Override
    @Transactional
    public void saveWithDish(SetmealDTO setmealDTO) {

// 1. 插入套餐基本信息
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);  // 将 DTO 的属性拷贝到 Entity

        setMealMapper.insert(setmeal);  // 执行插入，会自动填充 createTime、updateTime 等字段

        // 2. 获取自动生成的套餐 id
        Long setmealId = setmeal.getId();

        // 3. 插入套餐与菜品的关联关系
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();

            // 为每个关联关系设置套餐id
            setmealDishes.forEach(setmealDish ->{
                setmealDish.setSetmealId(setmealId);
            });

            // 批量插入套餐菜品关系
            setMealDishMapper.insertBatch(setmealDishes);

    }

    @Override
    public void startOrStop(Integer status, Long id) {
        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setMealMapper.updateStatus(setmeal);
    }

    @Override
    public void delete(List<Long> ids) {
        //起售中的套餐不能删除
        for (Long id : ids) {

            Setmeal setmeal = setMealMapper.getById(id);
            if (setmeal.getStatus() == StatusConstant.ENABLE) {
                //当前套餐处于起售中，不能删除
                throw new RuntimeException(MessageConstant.SETMEAL_ON_SALE);
            }
            //删除套餐表的数据
            setMealMapper.deleteById(id);


            //删除套餐和菜品的关联数表数据
            setMealDishMapper.deleteBySetmealId(id);
        }
    }

    @Override
    public SetmealVO getByIdWithDish(Long id) {

        //根据id查询套餐数据
        Setmeal setmeal = setMealMapper.getById(id);



        //根据id查询套餐关联的菜品数据
        List<SetmealDish> setmealDishes = setMealDishMapper.getBySetmealId(id);



        //组装并返回
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;

    }

    @Override
    public void updateWithDish(SetmealDTO setmealDTO) {

        /*
        * 修改套餐表数据
        * */
       Setmeal setmeal = new Setmeal();
       BeanUtils.copyProperties(setmealDTO, setmeal);
       setMealMapper.update(setmeal);


       /*
       * 删除套餐和菜品的关联表数据
       * */
        Long id = setmealDTO.getId();
        setMealDishMapper.deleteBySetmealId(id);

        /*

        * 插入新的套餐和菜品的关联表数据
        * */
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();

        setmealDishes.forEach(setmealDish -> {
        /*
        * 为每个关联关系设置套餐id
        * */
            setmealDish.setSetmealId(id);

        });
        setMealDishMapper.insertBatch(setmealDishes);
    }


}


