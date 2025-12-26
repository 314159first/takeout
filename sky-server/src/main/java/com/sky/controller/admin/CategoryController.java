package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
* 菜品管理
* */
@RestController
@RequestMapping("/admin/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;


    //新增菜品分类
    @PostMapping
    public Result save(@RequestBody CategoryDTO categoryDTO){
        log.info("新增分类:{}",categoryDTO);
        categoryService.save(categoryDTO);
        return Result.success();
    }

    //分页查询菜品
    @GetMapping("/page")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO){
        log.info("分页查询:{}",categoryPageQueryDTO);
        PageResult pageResult = categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(pageResult);
    }
    /*
    * 启用，禁用分类
    *
    * */
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status,@RequestParam Long id){
        log.info("启用，禁用菜品:{},{}",status,id);
        categoryService.startOrStop(status,id);
        return Result.success();
    }

    /*
     * 修改分类
     * */
    @PutMapping
    public Result update(@RequestBody CategoryDTO categoryDTO){
        log.info("修改分类:{}",categoryDTO);
        categoryService.update(categoryDTO);
        return Result.success();
    }
    /*
    * 根据id删除分类
    *
    *
    * */
    @DeleteMapping
    public Result delete(@RequestParam Long id){
        log.info("删除分类:{}",id);
        categoryService.delete(id);
        return Result.success();
    }
    /*
    * 根据类型查询分类
    * */
    @GetMapping("/list")
    public Result<List<Category>> list(Integer type){
        log.info("查询分类:type={}",type);
        List<Category> list = categoryService.list(type);
        return Result.success(list);

    }

}
