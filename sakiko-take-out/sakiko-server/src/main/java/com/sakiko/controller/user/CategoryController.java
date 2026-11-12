package com.sakiko.controller.user;

import com.sakiko.entity.Category;
import com.sakiko.result.Result;
import com.sakiko.service.CategoryService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController("userCategoryController")
@RequestMapping("/user/category")
@Api(tags ="用户端分类相关接口")
@Slf4j
public class CategoryController {

    @Resource
    CategoryService categoryService;

    @GetMapping("/list")
    Result<List<Category>> listCategory(Integer type){
        List<Category> list = categoryService.list(type);
        return Result.success(list);

    }


}
