package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DIshFlavorMapper;
import com.itheima.reggie.service.DishFlavorService;
import org.springframework.stereotype.Service;

/**
 * @author 尛猫
 * @version 1.0
 * @description: 菜品口味实体类
 * @date 2022/8/11 18:33
 */

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DIshFlavorMapper, DishFlavor> implements DishFlavorService {
}
