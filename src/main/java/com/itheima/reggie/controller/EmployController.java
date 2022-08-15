package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployController {

    @Autowired
    private EmployeeService employeeService;

    /*
     * 员工登陆
     * */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
//      明文密码MD5处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
//        根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> QueryWrapper = new LambdaQueryWrapper<>();
        QueryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee one = employeeService.getOne(QueryWrapper);
//        如果没有查询到则返回登陆失败的结果
        if (one == null || !one.getPassword().equals(password)) {
            return R.error("登陆失败");
        }
        if (one.getStatus() == 0) {
            return R.error("您的账号以禁用");
        }
        /*登陆成功将数据保存到Session中*/
        request.getSession().setAttribute("employee", one.getId());
        /*返回数据*/
        return R.success(one);
    }


    /*
     * 退出
     * */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");

    }

    /**
     * 添加员工信息
     *
     * @param employee
     * @param request
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Employee employee, HttpServletRequest request) {
//        设置初始密码并进行md5处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        设置时间戳
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
//        设置
        long empId = (long) request.getSession().getAttribute("employee");
        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);
        employeeService.save(employee);
        return R.success("新增员工成功");
    }


    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return R
     */
    @GetMapping("page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("page = {} pageSize = {} name = {}", page, pageSize, name);
        Page<Employee> pageInfo = new Page(page, pageSize);
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        employeeService.page(pageInfo, lambdaQueryWrapper);
        return R.success(pageInfo);
    }


    /**
     * 根据id修改员工信息
     *
     * @param employee
     * @param request
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Employee employee, HttpServletRequest request) {
        log.info(employee.toString());
        log.info("线程id=" + Thread.currentThread().getId());

        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));
        employeeService.updateById(employee);
        return R.success("修改成功");
    }


    /*
     * 根据id查找信息
     * */
    @GetMapping("/{id}")
    public R<Employee> selectOne(@PathVariable long id) {
        QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        System.out.println(id);
        Employee one = employeeService.getOne(queryWrapper);
        return R.success(one);
    }

}
