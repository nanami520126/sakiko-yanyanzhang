package com.sakiko.service;

import com.sakiko.dto.EmployeeDTO;
import com.sakiko.dto.EmployeeLoginDTO;
import com.sakiko.dto.EmployeePageQueryDTO;
import com.sakiko.entity.Employee;
import com.sakiko.result.PageResult;
import org.springframework.web.multipart.MultipartFile;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    void save(EmployeeDTO employeeDTO);

    Integer importEmployees(MultipartFile file);

    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    void startOrStop(Integer status, Long id);

    Employee select(Long id);

    void update(EmployeeDTO employeeDTO);

    void delete(Long[] ids);

}
