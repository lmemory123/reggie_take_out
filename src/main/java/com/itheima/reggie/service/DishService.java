package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.Dto.DishDto;
import com.itheima.reggie.entity.Dish;


public interface DishService extends IService<Dish> {

    //保存菜品和口味
    public void DishWithFlavor(DishDto dishDto);

    //根据di查询菜品和口味
    public DishDto getByIdWithFlavor(long id);

    //修改菜品和口味
    public void updateWithFlavor(DishDto dishDto);

    //根据id删除菜品和口味
    void deleteWithFlavor(long[] ids);


}
