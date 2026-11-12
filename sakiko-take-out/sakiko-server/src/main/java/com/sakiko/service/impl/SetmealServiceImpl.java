package com.sakiko.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sakiko.constant.MessageConstant;
import com.sakiko.constant.StatusConstant;
import com.sakiko.context.BaseContext;
import com.sakiko.dto.SetmealDTO;
import com.sakiko.dto.SetmealPageQueryDTO;
import com.sakiko.entity.Setmeal;
import com.sakiko.entity.SetmealDish;
import com.sakiko.exception.BaseException;
import com.sakiko.exception.DeletionNotAllowedException;
import com.sakiko.mapper.CategoryMapper;
import com.sakiko.mapper.DishMapper;
import com.sakiko.mapper.SetmealDishMapper;
import com.sakiko.mapper.SetmealMapper;
import com.sakiko.result.PageResult;
import com.sakiko.service.SetmealService;
import com.sakiko.vo.DishItemVO;
import com.sakiko.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Resource
    SetmealMapper setmealMapper;

    @Resource
    SetmealDishMapper setmealDishMapper;

    @Resource
    DishMapper dishMapper;

    @Resource
    CategoryMapper categoryMapper;

    @Override
    @Transactional
    public void save(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        //校验分类是否存在
        if (categoryMapper.findById(setmeal.getCategoryId()) == null) {
            throw new BaseException("分类不存在");
        }

        //校验套餐中的菜品是否都存在
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && !setmealDishes.isEmpty()) {
            for (SetmealDish setmealDish : setmealDishes) {
                if (dishMapper.getById(setmealDish.getDishId()) == null) {
                    throw new BaseException("菜品不存在");
                }
                if(Objects.equals(dishMapper.getById(setmealDish.getDishId()).getStatus(), StatusConstant.DISABLE)){
                    throw new BaseException("包含不可售菜品");
                }
            }
        }

        setmeal.setStatus(StatusConstant.ENABLE);
        //设置创建时间、修改时间、创建人、修改人
        setmeal.setCreateTime(LocalDateTime.now());
        setmeal.setUpdateTime(LocalDateTime.now());
        setmeal.setCreateUser(BaseContext.getCurrentId());
        setmeal.setUpdateUser(BaseContext.getCurrentId());

        setmealMapper.insert(setmeal);

        //插入套餐和菜品的关联关系
        Long setmealId = setmeal.getId();
        if (setmealDishes != null && !setmealDishes.isEmpty()) {
            setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealId));
            setmealDishMapper.insertBatch(setmealDishes);
        }
    }

    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public SetmealVO getById(Long id) {
        Setmeal setmeal = setmealMapper.getById(id);
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        return setmealVO;
    }

    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {


        ids.forEach(setmealId -> {
            if (StatusConstant.ENABLE.equals(setmealMapper.getById(setmealId).getStatus())) {
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
            setmealDishMapper.deleteBySetmealId(setmealId);
            setmealMapper.deleteById(setmealId);
        });
    }

    @Override
    public void startOrStop(Integer status, long id) {
        Setmeal setmeal = Setmeal.builder()
                .status(status)
                .id(id)
                .updateTime(LocalDateTime.now())
                .updateUser(BaseContext.getCurrentId())
                .build();

        setmealMapper.update(setmeal);

    }

    @Override
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        //校验分类是否存在
        if (categoryMapper.findById(setmeal.getCategoryId()) == null) {
            throw new BaseException("分类不存在");
        }
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && !setmealDishes.isEmpty()) {
            for (SetmealDish setmealDish : setmealDishes) {
                if (dishMapper.getById(setmealDish.getDishId()) == null) {
                    throw new BaseException("菜品不存在");
                }
                if(dishMapper.getById(setmealDish.getDishId()).getStatus() == StatusConstant.DISABLE){
                    throw new BaseException("包含不可售菜品");
                }
            }
        }
        setmeal.setStatus(StatusConstant.ENABLE);
        setmeal.setUpdateTime(LocalDateTime.now());
        setmeal.setUpdateUser(BaseContext.getCurrentId());

        setmealMapper.update(setmeal);

        setmealDishMapper.deleteBySetmealId(setmeal.getId());
        //插入套餐和菜品的关联关系
        Long setmealId = setmeal.getId();
        if (setmealDishes != null && !setmealDishes.isEmpty()) {
            setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealId));
            setmealDishMapper.insertBatch(setmealDishes);
        }
    }

    @Override
    public List<Setmeal> list(Setmeal setmeal) {
        return setmealMapper.list(setmeal);

    }

    @Override
    public List<DishItemVO> getDishItemById(Long id) {

        return setmealDishMapper.getDishItemById(id);
    }
}
