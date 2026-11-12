package com.sakiko.service;

import com.sakiko.dto.SetmealDTO;
import com.sakiko.dto.SetmealPageQueryDTO;
import com.sakiko.entity.Setmeal;
import com.sakiko.result.PageResult;
import com.sakiko.vo.DishItemVO;
import com.sakiko.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    void save(SetmealDTO setmealDTO);

    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    SetmealVO getById(Long id);

    void deleteBatch(List<Long> ids);

    void startOrStop(Integer status,long id);

    void update(SetmealDTO setmealDTO);

    List<Setmeal> list(Setmeal setmeal);

    List<DishItemVO> getDishItemById(Long id);
}
