package com.sakiko.controller.user;

import com.sakiko.constant.StatusConstant;
import com.sakiko.entity.Dish;
import com.sakiko.result.Result;
import com.sakiko.service.DishService;
import com.sakiko.vo.DishVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController("UserDishController")
@RequestMapping("/user/dish")
@Api(tags ="用户端菜品相关接口")
@Slf4j
public class DishController {

    @Resource
    private DishService dishService;

    @Resource
    private RedisTemplate redisTemplate;

    @GetMapping("/list")
    public Result<List<DishVO>> list(Long categoryId) {
//        //构造redis中的key，规则：dish_分类id
//        String key = "dish_" + categoryId;

//        //查询redis中是否存在菜品数据
//        List<DishVO> list = (List<DishVO>) redisTemplate.opsForValue().get(key);
//        if(list != null && list.size() > 0){
//            //如果存在，直接返回，无须查询数据库
//            return Result.success(list);
//        }
        ////////////////////////////////////////////////////////
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);//查询起售中的菜品

        //如果不存在，查询数据库，将查询到的数据放入redis中
        List<DishVO> list = dishService.listWithFlavor(dish);
        log.info("{}",list);
        ////////////////////////////////////////////////////////
//        redisTemplate.opsForValue().set(key, list);

        return Result.success(list);
    }

}
