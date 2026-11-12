package com.sakiko.service;

import com.sakiko.dto.DishDTO;
import com.sakiko.dto.DishPageQueryDTO;
import com.sakiko.entity.Dish;
import com.sakiko.result.PageResult;
import com.sakiko.vo.DishVO;

import java.util.List;


public interface DishService {

    void saveWithFlavor(DishDTO dishDTO);

    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    DishVO getById(Long id);

    void deleteBatch(List<Long> ids);

    void startOrStop(Long id,Integer status);

    void updateWithFlavor(DishDTO dishDTO);

    List<Dish> list(Long categoryId);

    List<DishVO> listWithFlavor(Dish dish);
}
