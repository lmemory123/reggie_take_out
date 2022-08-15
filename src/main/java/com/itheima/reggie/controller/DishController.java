package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.Dto.DishDto;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 尛猫
 * @version 1.0
 * @description: TODO
 * @date 2022/8/11 18:37
 */

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;
    /*分类*/
    @Autowired
    private CategoryService categoryService;

    /**
     * 添加数据
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.DishWithFlavor(dishDto);
        return R.success("添加成功");
    }

    @DeleteMapping
    public R<String> deleteByIds(long[] ids) {
        dishService.deleteWithFlavor(ids);
        return R.success("删除成功");
    }


    /**
     * 根据id查询菜品
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable long id) {
        DishDto byIdWithFlavor = dishService.getByIdWithFlavor(id);
        return R.success(byIdWithFlavor);
    }

    /**
     * 修改
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> updateWithFlavor(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);
        return R.success("修改成功");
    }


    /**
     * 分页数据
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> pageR(int page, int pageSize, String name) {
        /*构建分页构造器对象*/
        Page<Dish> pages = new Page<>(page, pageSize);
        Page<DishDto> pageInfo = new Page<>(page, pageSize);
        /*条件构造器*/
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        /*添加模糊查询条件*/
        queryWrapper.like(StringUtils.isNotEmpty(name), Dish::getName, name);
        /*添加排序条件*/
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pages, queryWrapper);
        /*对象拷贝，排除records*/
        BeanUtils.copyProperties(pages, pageInfo, "records");
        /*对records进行处理在返回数据到DTO*/
        List<DishDto> records = pages.getRecords().stream().map(item -> {
            /*将属性copy到DTO*/
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            /*获取分类ID*/
            Long categoryId = item.getCategoryId();
            /*获取分类名称*/
            Category byId = categoryService.getById(categoryId);
            String categoryName;
            if (byId != null) {
                categoryName = byId.getName();
            } else {
                categoryName = "暂未分类";
            }
            /*赋值*/
            dishDto.setCategoryName(categoryName);
            /*返回数据*/
            return dishDto;
            /*将数据收回到DTO*/
        }).collect(Collectors.toList());
        /*将收回的数据在赋值给page*/
        pageInfo.setRecords(records);
        return R.success(pageInfo);
    }


    /**
     * 批量停售
     *
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> OnSale(@PathVariable("status") int status, long[] ids) {
        Dish dish = new Dish();
        for (long id : ids) {
            dish.setId(id);
            dish.setStatus(status);
            dishService.updateById(dish);
        }
        return R.success("修改成功");
    }

    /**
     * 批量起售
     *
     * @param status
     * @param ids
     * @return
     */
    @GetMapping("/status/{status}")
    public R<String> downSale(@PathVariable("status") int status, long[] ids) {
        Dish dish = new Dish();
        for (long id : ids) {
            dish.setId(id);
            dish.setStatus(status);
            dishService.updateById(dish);
        }
        return R.success("修改成功");
    }


    /**
     * 菜品展示
     *
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> listDish(Dish dish) {

        /*查询所有起售菜品*/
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus, 1);
        queryWrapper.orderByDesc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);

        /*将数据封装到dto对象中*/
        List<DishDto> DishDtoInfo = list.stream().map(item -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            return dishDto;
        }).collect(Collectors.toList());

        /*将菜品对应的口味也封装到dto中*/
        List<DishDto> collect = DishDtoInfo.stream().map(item -> {
            Long id = item.getId();
            LambdaQueryWrapper<DishFlavor> queryWrappers = new LambdaQueryWrapper<>();
            queryWrappers.eq(DishFlavor::getDishId, id);
            item.setFlavors(dishFlavorService.list(queryWrappers));
            return item;
        }).collect(Collectors.toList());


        return R.success(collect);

    }


}
