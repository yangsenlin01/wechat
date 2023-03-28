package com.tba.wechat.config.mybatisplus.service;

import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;

/**
 * @author 1050696985@qq.com
 * @version V1.0
 * @Date 2021-8-9 10:48
 * @description service接口基类
 */
public interface ICustomService<T> extends IService<T> {

    /**
     * 根据 ID 修改所有字段，包括为null的字段
     *
     * @param entity 实体对象
     * @return 是否成功
     */
    boolean updateAllColumnById(T entity);

    /**
     * 根据 ID 修改所有字段，包括为null的字段，如果修改失败会抛出异常
     *
     * @param entity 实体对象
     */
    void updateAllColumnByIdThrow(T entity);

    /**
     * TableId 注解存在更新记录（全字段），否插入一条记录
     * PS: 该方法不检查ID是否存在于数据，如果实体有ID，那么直接走更新方法
     *
     * @param entity 实体对象
     */
    boolean saveOrUpdateAllColumn(T entity);

    /**
     * 根据ID 批量更新（全字段）
     *
     * @param entityList 实体对象集合
     */
    boolean updateAllColumnBatchById(Collection<T> entityList);

    /**
     * 批量修改插入（全字段）
     * PS: 该方法不检查ID是否存在于数据，如果实体有ID，那么直接走更新方法
     *
     * @param entityList 实体对象集合
     */
    boolean saveOrUpdateAllColumnBatch(Collection<T> entityList);
}
