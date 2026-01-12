package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/admin/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品:{}",dishDTO);
        dishService.save(dishDTO);

        //清理缓存数据
        String key="dish_"+dishDTO.getCategoryId();
        CleanCache( key);

        return Result.success();
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
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
        //将所有的菜品缓存数据删除，所有以dish_ 开头的缓存数据全部删除
        CleanCache("dish_*");

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
        //清理缓存数据
        CleanCache("dish_*");
        return Result.success();
    }


    /*
     * 根据分类ID查询菜品
     * */
    @GetMapping("/list")
    public Result<List<Dish>> list(Long categoryId){
        log.info("根据分类ID查询菜品：{}",categoryId);
        List<Dish> list = dishService.list(categoryId);
        return Result.success(list);
    }

    /*
    * 菜品状态改变：起售、停售
    * */
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status,Long id){
        log.info("菜品状态改变：{}",status);
        dishService.startOrStop(status,id);
        //清理缓存数据
        CleanCache("dish_*");
        return Result.success();
    }

    private void CleanCache(String pattern){
        Set keys=redisTemplate.keys(pattern);
        redisTemplate.delete(keys);

    }
}
