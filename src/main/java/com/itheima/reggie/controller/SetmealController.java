package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.Dto.SetmealDto;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
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
 * @description: 套餐管理
 * @date 2022/8/12 14:37
 */


@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;


    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmeal) {
        setmealService.save(setmeal);
        List<SetmealDish> setmealDishes = setmeal.getSetmealDishes();
        List<SetmealDish> collect = setmealDishes.stream().map(item -> {
            item.setSetmealId(setmeal.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(collect);
        return R.success("保存成功");
    }


    /**
     * 查询套餐基本信息
     *
     * @param page
     * @param pageSize
     * @param setmeal
     * @return
     */
    @GetMapping("/page")
    public R<Page<Setmeal>> pageR(int page, int pageSize, Setmeal setmeal) {
        Page<Setmeal> pages = new Page<>(page, pageSize);
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(setmeal.getName()), Setmeal::getName, setmeal.getName());
        setmealService.page(pages, queryWrapper);
        return R.success(pages);
    }

    /**
     * 根据id查询套餐和套餐详细信息
     *
     * @param setmealId
     * @return
     */
    @GetMapping("{setmealId}")
    public R<SetmealDto> selectByIdDish(@PathVariable long setmealId) {
        SetmealDto setmealDto = new SetmealDto();
        Setmeal setmeal = setmealService.getById(setmealId);
        BeanUtils.copyProperties(setmeal, setmealDto);
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmealId);
        List<SetmealDish> list = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(list);
        return R.success(setmealDto);
    }

    @PutMapping
    public R<String> updateByIDWithSetmealDish(@RequestBody SetmealDto setmealDto) {
        setmealService.updateById(setmealDto);
        /*先删除套餐详细信息*/
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(queryWrapper);

        /*将菜与套餐id绑定关系*/
        List<SetmealDish> collect = setmealDto.getSetmealDishes().stream().map(item -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        /*重新添加套餐详细信息*/
        setmealDishService.saveBatch(collect);
        return R.success("修改成功");
    }

    /**
     * 删除套餐
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteById(long[] ids) {
        for (long id : ids) {
            /*条件构造器删除套餐菜品*/
            LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SetmealDish::getSetmealId, id);
            setmealDishService.remove(queryWrapper);
            /*删除套餐*/
            setmealService.removeById(id);
        }
        return R.success("删除成功");
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
        Setmeal setmeal = new Setmeal();
        for (long id : ids) {
            setmeal.setId(id);
            setmeal.setStatus(status);
            setmealService.updateById(setmeal);

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
        Setmeal setmeal = new Setmeal();
        for (long id : ids) {
            setmeal.setId(id);
            setmeal.setStatus(status);
            setmealService.updateById(setmeal);

        }
        return R.success("修改成功");
    }


    @GetMapping("/list")
    public R<List<Setmeal>> listR(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId()).eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }


}


