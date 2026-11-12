package com.sakiko.controller.user;


import com.sakiko.context.BaseContext;
import com.sakiko.entity.AddressBook;
import com.sakiko.result.Result;
import com.sakiko.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/user/addressBook")
@Api(tags = "C端地址簿接口")
public class AddressBookController {


    @Resource
    AddressBookService addressBookService;

    @PostMapping("/add")
    Result<String> add(@RequestBody AddressBook addressBook){

        addressBookService.add(addressBook);

        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("查询当前登录用户的所有地址信息")
    Result<List<AddressBook>> list(){
        AddressBook addressBook = new AddressBook();
        addressBook.setUserId(BaseContext.getCurrentId());
        List<AddressBook> list = addressBookService.list(addressBook);
        return Result.success(list);
    }

    @GetMapping("/default")
    @ApiOperation("查询默认地址")
    Result<AddressBook> defaultList(){
        AddressBook addressBook = addressBookService.defaultList(BaseContext.getCurrentId());
        if(addressBook != null){
            return Result.success(addressBook);
        }
        return Result.error("没有查询到默认地址");
    }
    @PutMapping
    @ApiOperation("根据id修改地址")
    public Result update(@RequestBody AddressBook addressBook) {
        addressBookService.update(addressBook);
        return Result.success();
    }

    @DeleteMapping
    @ApiOperation("根据id删除地址")
    public Result deleteById(Long id) {
        addressBookService.deleteById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询地址")
    public Result<AddressBook> getById(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        return Result.success(addressBook);
    }

    /**
     * 设置默认地址
     *
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    @ApiOperation("设置默认地址")
    public Result setDefault(@RequestBody AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookService.setDefault(addressBook);
        return Result.success();
    }



}
