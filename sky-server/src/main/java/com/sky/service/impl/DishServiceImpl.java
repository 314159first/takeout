package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
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
    @Autowired
    private SetMealDishMapper setMealDishMapper;

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

    @Override
    public PageResult select(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.selectBypage(dishPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());


    }

    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //判断当前菜品能否删除

        for (Long id : ids) {
            Dish dish =dishMapper.getById(id);
            if (dish.getStatus() == 1){
                throw new DeletionNotAllowedException("当前菜品正在起售中，不能删除");
            }
        }

        //当前菜品是否关联套餐，关联了就不能删除
        List<Long> setmealIds= setMealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && setmealIds.size() > 0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }


        //删除菜品表的菜品数据
        for (Long id : ids) {
            dishMapper.deleteByID(id);
        }
        //删除口味关联的数据
        for (Long id : ids) {
            dishFlavorMapper.deleteByDishId(id);

        }
    }

    /*
    * 根据id修改菜品时查询菜品数据以及口味数据
    * */
    @Override
    public DishVO getByWithFlavor(Long id) {

        //根据id查询菜品数据
        Dish dish = dishMapper.getById(id);


        //根据id查询口味数据
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);

        //组装并返回
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }

    /*
    * 修改菜品表数据
    * */
    @Override
    public void update(DishDTO dishDTO) {
        //修改菜品表数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.update(dish);

        //删除原有的口味数据
        dishFlavorMapper.deleteByDishId(dishDTO.getId());



        //插入新的口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0){
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dish.getId()));
            dishFlavorMapper.insertBatch(flavors);
        }

    }
}
