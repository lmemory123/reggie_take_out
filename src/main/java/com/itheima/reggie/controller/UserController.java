package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.util.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author 尛猫
 * @version 1.0
 * @description: 用户接口
 * @date 2022/8/13 0:22
 */

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 验证码
     *
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> SendMsg(@RequestBody User user, HttpSession session) {
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)) {
            String s = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info(s);
            session.setAttribute(phone, s);
            return R.success("短信发送成功");
        }


        return R.error("发送失败");
    }


    /**
     * 登陆校验
     *
     * @param user
     * @param session
     * @param request
     * @return
     */
    @PostMapping("/login")
    public R<String> login(@RequestBody Map user, HttpSession session, HttpServletRequest request) {
        System.out.println(user.toString());
        String phone = (String) user.get("phone");
        String code = (String) user.get("code");
        String attribute = (String) session.getAttribute(phone);
        if (attribute.equals(code)) {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User one = userService.getOne(queryWrapper);
            if (one == null) {
                User users = new User();
                users.setPhone(phone);
                userService.save(users);
            }
            User ById = userService.getOne(queryWrapper);
            /*将用户id报错到Attribute中*/
            request.getSession().setAttribute("user", ById.getId());
            BaseContext.setThreadLocal(ById.getId());

            return R.success("登陆成功");
        }
        return R.error("登陆失败");
    }


    /**
     * 退出账户
     *
     * @param request
     * @return
     */
    @PostMapping("/loginout")
    public R<String> loginOut(HttpServletRequest request) {
        request.getSession().removeAttribute("user");
        return R.success("退出成功");
    }


}
