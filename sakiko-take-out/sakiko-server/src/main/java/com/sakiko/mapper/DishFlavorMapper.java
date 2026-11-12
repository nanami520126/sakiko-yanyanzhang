package com.sakiko.mapper;

import com.sakiko.entity.DishFlavor;
import com.sakiko.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
public interface DishFlavorMapper {

    /**
     * 批量插入口味数据
     */
    void insertBatch(List<DishFlavor> flavors);

    /**
     * 删除口味
     */
    @Delete("delete from dish_flavor where dish_id = #{dishId}")
    void deleteByDishId(Long dishId);

    /**
     * 根据id查菜品口味
     */
    @Select("SELECT * FROM dish_flavor where dish_id = #{dishId}")
    List<DishFlavor> getByDishId(Long dishId);
}
