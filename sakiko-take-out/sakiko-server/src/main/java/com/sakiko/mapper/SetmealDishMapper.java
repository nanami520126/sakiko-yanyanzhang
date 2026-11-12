package com.sakiko.mapper;

import com.sakiko.entity.SetmealDish;
import com.sakiko.vo.DishItemVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 批量插入套餐和菜品的关联关系
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 根据菜品id查询对应的套餐id
     */
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

    @Delete("delete from setmeal_dish where setmeal_dish.setmeal_id = #{id}")
    void deleteBySetmealId(Long id);

    @Select("select sd.name, sd.copies, d.image, d.description " +
            "from setmeal_dish sd left join dish d on sd.dish_id = d.id " +
            "where sd.setmeal_id = #{setmealId}")
    List<DishItemVO> getDishItemById(Long id);

}
