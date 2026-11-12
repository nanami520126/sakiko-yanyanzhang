package com.sakiko.mapper;

import com.github.pagehelper.Page;
import com.sakiko.dto.DishPageQueryDTO;
import com.sakiko.entity.Dish;
import com.sakiko.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {


    /**
     * 根据分类id查询菜品数量
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);
    /**
     * 根据id查询菜品
     */
    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);
    /**
     * 根据id删除菜品
     */
    @Delete("delete from dish where id = #{id}")
    void deleteById(Long id);

    /**
     * 根据状态统计菜品数量
     */
    @Select("select count(id) from dish where status = #{status}")
    Integer countByStatus(Integer status);
    /**
     * 插入菜品数据，并回填主键id
     */
    void insert(Dish dish);

    /**
     * 菜品分页查询（对应 DishMapper.xml）
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);
    /**
     * 根据id动态修改菜品（对应 DishMapper.xml）
     */
    void update(Dish dish);

    /**
     * 动态条件查询菜品列表（对应 DishMapper.xml）
     */
    List<Dish> list(Dish dish);




    
}
