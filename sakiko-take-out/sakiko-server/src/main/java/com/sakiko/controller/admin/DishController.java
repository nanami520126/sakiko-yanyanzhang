package com.sakiko.controller.admin;

import com.sakiko.dto.DishDTO;
import com.sakiko.dto.DishPageQueryDTO;
import com.sakiko.entity.Dish;
import com.sakiko.entity.Employee;
import com.sakiko.result.PageResult;
import com.sakiko.result.Result;
import com.sakiko.service.DishService;
import com.sakiko.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {

    @Resource
    private DishService dishService;

    /**
     * 新增菜品
     *
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品：{}", dishDTO);
        dishService.saveWithFlavor(dishDTO);//后绪步骤开发
        return Result.success();
    }


    /**
     * 分页查询
     *
     * @param
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询:{}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);//后绪步骤开发
        return Result.success(pageResult);
    }

    /**
     * 按id查询
     *
     * @param
     * @return
     */

    @GetMapping("/id/{id}")
    @ApiOperation("id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id){
        DishVO dish = dishService.getById(id);

        log.info("查询菜品：{}",dish);
        return Result.success(dish);
    }

    /**
     * 菜品批量删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("菜品批量删除")
    public Result delete(@RequestParam List<Long> ids) {
        log.info("菜品批量删除：{}", ids);
        dishService.deleteBatch(ids);//后绪步骤实现
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("菜品起售")
    public Result startOrStop(@PathVariable Integer status, @RequestParam Long id) {
        log.info("菜品id{}起售：{}", id,  status);
        dishService.startOrStop(id,status);
        return Result.success();
    }

    /**
     * 修改菜品
     *
     * @param dishDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改菜品")
    public Result update(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品：{}", dishDTO);
        dishService.updateWithFlavor(dishDTO);
        return Result.success();
    }

    /**
     * 根据分类id查询菜品（起售中的菜品）
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> list(Long categoryId) {
        log.info("根据分类id查询菜品：{}", categoryId);
        List<Dish> list = dishService.list(categoryId);
        return Result.success(list);
    }

}