package com.sakiko.controller.user;

import com.sakiko.constant.StatusConstant;
import com.sakiko.entity.Dish;
import com.sakiko.entity.Setmeal;
import com.sakiko.result.Result;
import com.sakiko.service.SetmealService;
import com.sakiko.vo.DishItemVO;
import com.sakiko.vo.DishVO;
import com.sakiko.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController("UserSetmealController")
@RequestMapping("/user/setmeal")
@Api(tags ="用户端套餐相关接口")
@Slf4j
public class SetmealController {

    @Resource
    private SetmealService setmealService;

    @GetMapping("/list")
    public Result<List<Setmeal>> list(Long categoryId) {
        Setmeal setmeal = new Setmeal();
        setmeal.setCategoryId(categoryId);
        setmeal.setStatus(StatusConstant.ENABLE);
        List<Setmeal> list = setmealService.list(setmeal);
        return Result.success(list);
    }
    @GetMapping("/dish/{id}")
    @ApiOperation("根据套餐id查询包含的菜品列表")
    public Result<List<DishItemVO>> dishList(@PathVariable("id") Long id) {
        List<DishItemVO> list = setmealService.getDishItemById(id);
        return Result.success(list);

    }
}
