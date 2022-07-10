package com.xymq.execution;

import com.xymq.exception.ExceptionEnum;
import com.xymq.exception.XyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author 黎勇炫
 * @date 2022年07月10日 12:44
 */
@Component
public class ExecutionFactory {

    /**
     * 存放不同的策略实现
     */
    public static List<Execution> strategies = new ArrayList<>();

    /**
     * 通过构造器动态传参，自动把所有的策略实现加载进工厂
     */
    @Autowired
    public ExecutionFactory(Execution...args ) {
        Arrays.stream(args).forEach(strategys->{
            strategies.add(strategys);
        });
    }

    /**
     * 根据登录类型返回登录策略
     */
    public static Execution getStrategy(int type) {
        for (Execution strategy : strategies) {
            if(strategy.getType() == type){
                return strategy;
            }
        }
        throw new XyException(ExceptionEnum.ECEC_STRETEGY_NOT_FOUNT);
    }
}
