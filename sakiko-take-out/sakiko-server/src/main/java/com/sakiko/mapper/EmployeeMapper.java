package com.sakiko.mapper;

import com.github.pagehelper.Page;
import com.sakiko.dto.EmployeePageQueryDTO;
import com.sakiko.entity.Employee;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return Employee
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    @Select("select * from employee where id = #{id}")
    Employee getById(Long id);

    /**
     * 添加员工信息
     * @param employee
     * @return void
     */
    @Insert("insert into employee (name, username, password, phone, sex, id_number, create_time, update_time, create_user, update_user,status) " +
            "values " +
            "(#{name},#{username},#{password},#{phone},#{sex},#{idNumber},#{createTime},#{updateTime},#{createUser},#{updateUser},#{status})")
    void insert(Employee employee);

    void insertBatch(List<Employee> employeeList);

    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);


    void update(Employee employee);

    @Delete("DELETE from employee where id = #{id}")
    void deleteById(Long id);
}
