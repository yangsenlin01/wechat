package com.tba.wechat.config.mybatisplus.injector;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.tba.wechat.config.mybatisplus.injector.method.UpdateAllColumnById;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 1050696985@qq.com
 * @version V1.0
 * @Date 2021-8-9 10:34
 * @description 自定义方法注入器
 */

@Component
public class CustomInjector extends DefaultSqlInjector {

    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass, tableInfo);
        methodList.add(new UpdateAllColumnById(UpdateAllColumnById.METHOD));
        return methodList;
    }

}
