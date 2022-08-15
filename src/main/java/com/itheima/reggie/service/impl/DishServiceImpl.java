package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.Dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 尛猫
 * @version 1.0
 * @description: 菜品实体类
 * @date 2022/8/10 20:50
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;


    /**
     * 保存菜品和菜品口味数据
     *
     * @param dishDto
     */
    @Override
    @Transactional
    public void DishWithFlavor(DishDto dishDto) {
        /*保存菜品  dishDto继承了dish*/
        this.save(dishDto);
        /*将菜品的id取出  回写*/
        Long categoryId = dishDto.getId();
        /*将菜品id赋值给口味的菜品id*/
        List<DishFlavor> collect = dishDto.getFlavors().stream().peek((item) -> item.setDishId(categoryId)).collect(Collectors.toList());
        /*将口味写入数据库*/
        dishFlavorService.saveBatch(collect);

    }


    /**
     * 根据id查询菜品和口味
     *
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(long id) {
        /*查询菜品*/
        Dish byId = this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(byId, dishDto);
        /*根据菜品id查询口味*/
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, byId.getId());
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);
        /*返回数据*/
        dishDto.setFlavors(list);
        return dishDto;
    }

    /**
     * 修改菜品信息
     * 开启事务
     *
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().peek(item -> item.setDishId(dishDto.getId())).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
        this.updateById(dishDto);
    }

    /**
     * 批量删除数据
     *
     * @param ids
     */
    @Override
    public void deleteWithFlavor(long[] ids) {
        for (Long id : ids) {
            /*删除菜品*/
            super.removeById(id);
            /*删除菜品对于口味信息*/
            LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DishFlavor::getDishId, id);
            dishFlavorService.remove(queryWrapper);
        }
    }
}
