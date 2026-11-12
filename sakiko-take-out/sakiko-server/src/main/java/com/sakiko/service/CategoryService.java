package com.sakiko.service;

import com.sakiko.dto.CategoryDTO;
import com.sakiko.dto.CategoryPageQueryDTO;
import com.sakiko.entity.Category;
import com.sakiko.result.PageResult;

import java.util.List;

public interface CategoryService {

    /**
     * 新增分类
     */
    void save(CategoryDTO categoryDTO);

    /**
     * 分类分页查询
     */
    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 根据id删除分类
     */
    void deleteById(Long id);

    /**
     * 修改分类
     */
    void update(CategoryDTO categoryDTO);

    /**
     * 启用、禁用分类
     */
    void startOrStop(Integer status, Long id);

    /**
     * 根据类型查询分类
     */
    List<Category> list(Integer type);
}
