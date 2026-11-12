package com.sakiko.mapper;

import com.github.pagehelper.Page;
import com.sakiko.dto.CategoryPageQueryDTO;
import com.sakiko.entity.Category;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {

    /**
     * 插入分类数据
     */
    @Insert("insert into category (type, name, sort, status, create_time, update_time, create_user, update_user) " +
            "values (#{type}, #{name}, #{sort}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void insert(Category category);

    /**
     * 分类分页查询
     */
    Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 根据id删除分类
     */
    @Delete("delete from category where id = #{id}")
    void deleteById(Long id);

    @Select("SELECT * from category where id = #{id} ")
    Category findById(Long id);

    /**
     * 根据id动态修改分类
     */
    void update(Category category);

    /**
     * 根据类型查询分类
     */
    @Select("select * from category where type = #{type} order by sort asc, create_time desc")
    List<Category> list(Integer type);
}
