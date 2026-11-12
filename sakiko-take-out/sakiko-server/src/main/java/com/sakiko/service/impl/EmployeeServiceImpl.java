package com.sakiko.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sakiko.constant.MessageConstant;
import com.sakiko.constant.PasswordConstant;
import com.sakiko.constant.StatusConstant;
import com.sakiko.context.BaseContext;
import com.sakiko.dto.EmployeeDTO;
import com.sakiko.dto.EmployeeLoginDTO;
import com.sakiko.dto.EmployeePageQueryDTO;
import com.sakiko.entity.Employee;
import com.sakiko.exception.AccountLockedException;
import com.sakiko.exception.AccountNotFoundException;
import com.sakiko.exception.BaseException;
import com.sakiko.exception.PasswordErrorException;
import com.sakiko.mapper.EmployeeMapper;
import com.sakiko.result.PageResult;
import com.sakiko.service.EmployeeService;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        //  后期需要进行md5加密，然后再进行比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    public void save(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();

        //对象属性拷贝
        BeanUtils.copyProperties(employeeDTO, employee);

        //设置账号的状态，默认正常状态 1表示正常 0表示锁定
        employee.setStatus(StatusConstant.ENABLE);

        //设置密码，默认密码123456
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

        //设置当前记录的创建时间和修改时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        //设置当前记录创建人id和修改人id
        employee.setCreateUser(BaseContext.getCurrentId());
        employee.setUpdateUser(BaseContext.getCurrentId());

        employeeMapper.insert(employee);//后续步骤定义

    }


    @Transactional
    public Integer importEmployees(MultipartFile file) {
        List<Employee> employeeList = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();

        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {   // 自动识别 .xls / .xlsx
            Sheet sheet = workbook.getSheetAt(0);

            // 从第 1 行开始（第 0 行是表头）
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                String username = formatter.formatCellValue(row.getCell(0));
                String name     = formatter.formatCellValue(row.getCell(1));
                String phone    = formatter.formatCellValue(row.getCell(2));
                String sex      = formatter.formatCellValue(row.getCell(3));
                String idNumber = formatter.formatCellValue(row.getCell(4));

                // 用户名必填，空行直接跳过
                if (username == null || username.trim().isEmpty()) {
                    continue;
                }
                // 用户名重复校验（防止文件内重复 + 数据库已存在）
                if (employeeMapper.getByUsername(username) != null) {
                    throw new BaseException("用户名已存在：" + username);
                }

                Employee employee = Employee.builder()
                        .username(username)
                        .name(name)
                        .phone(phone)
                        .sex(sex)
                        .idNumber(idNumber)
                        .password(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()))  // 默认 123456
                        .status(StatusConstant.ENABLE)
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .createUser(BaseContext.getCurrentId())
                        .updateUser(BaseContext.getCurrentId())
                        .build();

                employeeList.add(employee);
            }
        } catch (Exception e) {
            throw new RuntimeException("Excel 解析失败：" + e.getMessage(), e);
        }

        if (employeeList.isEmpty()) {
            return 0;
        }
        employeeMapper.insertBatch(employeeList);
        return employeeList.size();
    }

    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        // select * from employee limit 0,10
        //开始分页查询
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());

        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);//后续定义

        long total = page.getTotal();
        List<Employee> records = page.getResult();

        return new PageResult(total, records);
    }

    public void startOrStop(Integer status, Long id) {
        Employee employee = Employee.builder()
                .status(status)
                .id(id)
                .build();

        employeeMapper.update(employee);
    }

    @Override
    public Employee select(Long id) {
        Employee employee = employeeMapper.getById(id);
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        employee.setPassword("****");

        return employee;
    }

    @Override
    public void update(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);

        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(BaseContext.getCurrentId());

        employeeMapper.update(employee);
    }

    @Transactional
    public void delete(Long[] ids) {

        for (Long id : ids) {

            if (employeeMapper.getById(id) == null) {
                throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
            }
            if(id == 1){
                throw new BaseException("管理员不能被删除");
            }

            employeeMapper.deleteById(id);
        }
    }


}
