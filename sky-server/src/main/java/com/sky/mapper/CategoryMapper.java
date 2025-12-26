package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CategoryMapper {

    //新增分类
    @Insert("insert into category (type, name, sort, create_time, update_time, create_user, update_user,status) VALUES (#{type}, #{name}, #{sort}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser},#{status})")
     void insert(Category category);
    //分页查询菜品
    Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);
    /*
    * 修改分类,以及禁用启用
    * */

    void update(Category category);


    void delete(Long id);

    List<Category> list(Integer type);

    //分类查询
    
}
