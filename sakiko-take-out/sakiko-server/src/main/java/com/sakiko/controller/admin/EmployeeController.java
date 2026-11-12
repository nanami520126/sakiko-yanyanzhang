package com.sakiko.controller.admin;

import com.sakiko.constant.JwtClaimsConstant;
import com.sakiko.dto.EmployeeDTO;
import com.sakiko.dto.EmployeeLoginDTO;
import com.sakiko.dto.EmployeePageQueryDTO;
import com.sakiko.entity.Employee;
import com.sakiko.properties.JwtProperties;
import com.sakiko.result.PageResult;
import com.sakiko.result.Result;
import com.sakiko.service.EmployeeService;
import com.sakiko.utils.JwtUtil;
import com.sakiko.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")
public class EmployeeController {

    @Resource
    private EmployeeService employeeService;
    @Resource
    private JwtProperties jwtProperties;
    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        claims.put(JwtClaimsConstant.USERNAME, employee.getUsername());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        //登录成功后，将jwt令牌保存到redis，过期时间与jwt保持一致
        //key: login:token:{token}，value: 员工id
        redisTemplate.opsForValue().set(
                "login:token:" + token,
                employee.getId().toString(),
                jwtProperties.getAdminTtl(),
                TimeUnit.MILLISECONDS);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 新增员工
     * @param employeeDTO
     * @return
     */
    @PostMapping("/add")
    @ApiOperation("新增员工")
    public Result save(@RequestBody EmployeeDTO employeeDTO){
        log.info("新增员工：{}",employeeDTO);
        employeeService.save(employeeDTO);//该方法后续步骤会定义
        return Result.success();
    }
    /**
     * 批量导入员工
     */
    @PostMapping("/import")
    public Result<Integer> importEmployees(@RequestParam("file") MultipartFile file) {
        Integer count = employeeService.importEmployees(file);
        return Result.success(count);
    }

    /**
     * 员工分页查询
     * @param employeePageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("员工分页查询")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO){
        log.info("员工分页查询，参数为：{}", employeePageQueryDTO);
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);//后续定义
        return Result.success(pageResult);
    }


    @PostMapping("/status/{status}")
    @ApiOperation("启用禁用员工账号")
    public Result startOrStop(@PathVariable Integer status, @RequestParam Long id){
        log.info("启用禁用员工账号：{},{}",status,id);
        employeeService.startOrStop(status,id);//后绪步骤定义
        return Result.success();
    }

    @GetMapping("/id/{id}")
    @ApiOperation("id查询员工")
    public Result<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.select(id);
        log.info("查询员工账号：{}",id);
        return Result.success(employee);
    }

    /**
     * 编辑员工信息
     * @param employeeDTO
     * @return
     */
    @PutMapping
    @ApiOperation("编辑员工信息")
    public Result update(@RequestBody EmployeeDTO employeeDTO){
        log.info("编辑员工信息：{}", employeeDTO);
        employeeService.update(employeeDTO);
        return Result.success();
    }

    @DeleteMapping
    @ApiOperation("批量删除员工")
    public Result delete(@RequestBody Long[] ids){
        log.info("批量删除员工{}", ids);
        employeeService.delete(ids);
        return Result.success();
    }





}
