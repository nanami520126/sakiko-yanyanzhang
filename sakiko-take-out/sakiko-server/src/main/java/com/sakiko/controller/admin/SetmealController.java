package com.sakiko.controller.admin;

import com.sakiko.dto.DishDTO;
import com.sakiko.dto.EmployeePageQueryDTO;
import com.sakiko.dto.SetmealDTO;
import com.sakiko.dto.SetmealPageQueryDTO;
import com.sakiko.result.PageResult;
import com.sakiko.result.Result;
import com.sakiko.service.SetmealService;
import com.sakiko.vo.DishVO;
import com.sakiko.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
@Api(tags = "套餐相关接口")
public class SetmealController {

    @Resource
    private SetmealService setmealService;

    @PostMapping("/save")
    @ApiOperation("新增套餐")
    public Result save(@RequestBody SetmealDTO setmealDTO) {
        log.info("新增套餐:{}", setmealDTO);
        setmealService.save(setmealDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("套餐分页查询")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("套餐分页查询，参数为：{}", setmealPageQueryDTO);
        PageResult pageResult = setmealService.pageQuery(setmealPageQueryDTO);//后续定义
        return Result.success(pageResult);
    }

    @GetMapping("/id/{id}")
    @ApiOperation("id查询逃禅")
    public Result<SetmealVO> getById(@PathVariable Long id){
        SetmealVO dish = setmealService.getById(id);
        log.info("查询菜品：{}",dish);
        return Result.success(dish);
    }
    @DeleteMapping()
    @ApiOperation("批量删除套餐")
    public Result delete(@RequestParam List<Long> ids) {
        log.info("批量删除套餐:{}", ids);
        setmealService.deleteBatch(ids);
        return Result.success();
    }
    @PostMapping("/status/{status}")
    @ApiOperation("套餐起售")
    public Result startOrStop(@PathVariable Integer status, @RequestParam Long id) {
        log.info("修改{}起售状态",id);
        setmealService.startOrStop(status,id);
        return Result.success();
    }
    @PutMapping
    @ApiOperation("修改套餐")
    public Result update(@RequestBody SetmealDTO setmealDTO) {
        log.info("修改菜品：{}", setmealDTO);
        setmealService.update(setmealDTO);
        return Result.success();
    }

}
