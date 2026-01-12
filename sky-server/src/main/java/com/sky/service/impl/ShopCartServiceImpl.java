package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShopCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetMealMapper setMealMapper;

    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {

        //判断当前加入到购物车的商品是否在购物车中
        ShoppingCart shoppingCart=new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);


        //如果已经存在，数量加1
        if (list != null && list.size() > 0) {

            ShoppingCart cart = list.get(0);
            cart.setNumber(cart.getNumber() + 1);
            shoppingCartMapper.updateNUmberByID(cart);
        }


        //如果不存在，则添加到购物车，插入一条购物车数据
        else {
            Long dishId = shoppingCartDTO.getDishId();

            if (dishId != null) {
            //本次添加到购物车的是菜品
                Dish dish=dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
                shoppingCart.setNumber(1);
                shoppingCart.setCreateTime(LocalDateTime.now());
            }else {
                //本次添加到购物车是套餐
                Long setmealId = shoppingCartDTO.getSetmealId();
                Setmeal setmeal = setMealMapper.getById(setmealId);
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }
                shoppingCart.setNumber(1);
                shoppingCart.setCreateTime(LocalDateTime.now());
                shoppingCartMapper.insert(shoppingCart);

        }
    }

    @Override
    public List<ShoppingCart>  showShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingcart = ShoppingCart.builder().userId(userId).build();
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingcart);
        return list;
    }

    @Override
    public void clean() {
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.deleteByUserId(userId);
    }

    @Override
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        //设置查询条件，查询当前登录用户的购物车数据
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(BaseContext.getCurrentId()).build();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);

//        build方法构建
//        ShoppingCart shoppingCart = new ShoppingCart();
//        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
//        //设置查询条件，查询当前登录用户的购物车数据
//        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);

        if(list.size()!=0&&list!=null){
            //
        shoppingCart= list.get(0);
        Integer number = shoppingCart.getNumber();

        if(number==1){
            //判断当前购物车商品数量是否为1,为1时删除该商品
            shoppingCartMapper.deleteById(shoppingCart.getId());
        }
        else{
            //不为1时，数量减1
            shoppingCart.setNumber(number-1);
            shoppingCartMapper.updateNUmberByID(shoppingCart);
        }
    }




    }
}
