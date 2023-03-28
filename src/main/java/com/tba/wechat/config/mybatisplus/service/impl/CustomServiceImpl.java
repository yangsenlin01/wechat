package com.tba.wechat.config.mybatisplus.service.impl;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.tba.wechat.config.mybatisplus.custommapper.CustomMapper;
import com.tba.wechat.config.mybatisplus.injector.method.UpdateAllColumnById;
import com.tba.wechat.config.mybatisplus.service.ICustomService;
import com.tba.wechat.exception.CustomException;
import org.apache.ibatis.binding.MapperMethod;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * @author 1050696985@qq.com
 * @version V1.0
 * @Date 2021-8-9 10:51
 * @description service实现类基类
 */
public class CustomServiceImpl<M extends CustomMapper<T>, T> extends ServiceImpl<M, T> implements ICustomService<T> {

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateAllColumnById(T entity) {
        return SqlHelper.retBool(this.baseMapper.updateAllColumnById(entity));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateAllColumnByIdThrow(T entity) {
        if (!SqlHelper.retBool(this.baseMapper.updateAllColumnById(entity))) {
            throw new CustomException("更新失败，数据可能已经被修改");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveOrUpdateAllColumn(T entity) {
        if (null != entity) {
            TableInfo tableInfo = TableInfoHelper.getTableInfo(this.entityClass);
            Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!");
            String keyProperty = tableInfo.getKeyProperty();
            Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!");
            Object idVal = ReflectionKit.getFieldValue(entity, tableInfo.getKeyProperty());
            return StringUtils.checkValNull(idVal) ? save(entity) : updateAllColumnById(entity);
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateAllColumnBatchById(Collection<T> entityList) {
        return executeBatch(entityList, DEFAULT_BATCH_SIZE, (sqlSession, entity) -> {
            MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
            param.put(Constants.ENTITY, entity);
            sqlSession.update(mapperClass.getName() + StringPool.DOT + UpdateAllColumnById.METHOD, param);
        });
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveOrUpdateAllColumnBatch(Collection<T> entityList) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
        Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!");
        String keyProperty = tableInfo.getKeyProperty();
        Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!");
        return SqlHelper.saveOrUpdateBatch(this.entityClass, this.mapperClass, this.log, entityList, DEFAULT_BATCH_SIZE, (sqlSession, entity) -> {
            Object idVal = ReflectionKit.getFieldValue(entity, keyProperty);
            return StringUtils.checkValNull(idVal);
        }, (sqlSession, entity) -> {
            MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
            param.put(Constants.ENTITY, entity);
            sqlSession.update(mapperClass.getName() + StringPool.DOT + UpdateAllColumnById.METHOD, param);
        });
    }
}
