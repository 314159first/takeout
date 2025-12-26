package com.sky.aspect;

import com.sky.annotation.Autofill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import org.aspectj.lang.reflect.MethodSignature;



import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/*
* 自定义切面，实现公共字段自动填充
* */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    /*
    * 逻辑：
    * 1.切面定义，实现公共字段自动填充
    * 2.切点定义，指定要拦截的方法
    * 3.通知实现，为对应的方法参数赋值
    * */
    @Pointcut("execution(* com.sky.mapper.*.*(..))&&@annotation(com.sky.annotation.Autofill)")
    public void autoFillPointCut(){


    }
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        log.info("开始进行公共字段填充");
        //获取到当前方法被拦截的数据库操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        Autofill autofill =signature.getMethod().getAnnotation(Autofill.class);
        OperationType operationType = autofill.value();


        //获取到当前被拦截到的方法参数--实体对象
        Object[] args = joinPoint.getArgs();
        if(args == null || args.length == 0){
            return;
        }
        Object entity = args[0];




        //准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        if(operationType == OperationType.INSERT){
            //为插入操作的字段赋值
            try {
                //设置创建时间、更新时间
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME,LocalDateTime.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER,Long.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,Long.class);
               //通过反射为对象属性赋值
                setCreateTime.invoke(entity,now);
                setUpdateTime.invoke(entity,now);
                setCreateUser.invoke(entity,BaseContext.getCurrentId());
                setUpdateUser.invoke(entity,BaseContext.getCurrentId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (operationType==OperationType.UPDATE) {
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,Long.class);
               setUpdateTime.invoke(entity,now);
               setUpdateUser.invoke(entity,BaseContext.getCurrentId());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        //根据当前不同的操作类型，为对应的类属性通过反射赋值

    }

}
