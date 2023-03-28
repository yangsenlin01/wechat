package com.tba.wechat.config.mybatisplus.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.tba.wechat.config.mybatisplus.entity.CustomEntity;
import org.apache.ibatis.reflection.MetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author 1050696985@qq.com
 * @version V1.0
 * @Date 2021-8-9 1:24
 * @description 新增&更新自动填充
 */

@Component
public class CreateAndUpdateMetaObjectHandler implements MetaObjectHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(CreateAndUpdateMetaObjectHandler.class);

    @Override
    public void insertFill(MetaObject metaObject) {
        try {
            this.fillCreatedField(metaObject);
            this.fillUpdatedField(metaObject);
        } catch (Exception e) {
            throw new RuntimeException("自动注入异常 => " + e.getMessage());
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        try {
            this.fillUpdatedField(metaObject);
        } catch (Exception e) {
            throw new RuntimeException("自动注入异常 => " + e.getMessage());
        }
    }

    /**
     * 填充创建人、创建时间
     *
     * @param metaObject
     */
    private void fillCreatedField(MetaObject metaObject) {
        if (metaObject.hasGetter(CustomEntity._FIELD_CREATE_AT)) {
            if (metaObject.getValue(CustomEntity._FIELD_CREATE_AT) == null) {
                this.strictInsertFill(metaObject, CustomEntity._FIELD_CREATE_AT, Date.class, new Date());
            }
        }
        if (metaObject.hasGetter(CustomEntity._FIELD_CREATE_BY)) {
            if (metaObject.getValue(CustomEntity._FIELD_CREATE_BY) == null) {
                this.strictInsertFill(metaObject, CustomEntity._FIELD_CREATE_BY, Long.class, getUserId());
            }
        }
        if (metaObject.hasGetter(CustomEntity._FIELD_CREATE_BY_NAME)) {
            if (metaObject.getValue(CustomEntity._FIELD_CREATE_BY_NAME) == null) {
                this.strictInsertFill(metaObject, CustomEntity._FIELD_CREATE_BY_NAME, String.class, getNickName());
            }
        }
    }

    /**
     * 填充更新人、更新时间
     *
     * @param metaObject
     */
    private void fillUpdatedField(MetaObject metaObject) {
        // this.strictUpdateFill(metaObject, CustomEntity._FIELD_UPDATE_AT, Date.class, new Date());
        if (metaObject.hasGetter(CustomEntity._FIELD_UPDATE_AT)) {
            this.customStrictUpdateFill(metaObject, CustomEntity._FIELD_UPDATE_AT, Date.class, new Date());
        }
        if (metaObject.hasGetter(CustomEntity._FIELD_UPDATE_BY)) {
            this.customStrictUpdateFill(metaObject, CustomEntity._FIELD_UPDATE_BY, Long.class, getUserId());
        }
        if (metaObject.hasGetter(CustomEntity._FIELD_UPDATE_BY_NAME)) {
            this.customStrictUpdateFill(metaObject, CustomEntity._FIELD_UPDATE_BY_NAME, String.class, getNickName());
        }
    }

    /**
     * 整合 {@link MetaObjectHandler#strictUpdateFill(MetaObject, String, Class, Object)} 方法的调用链，调用链逻辑不变，只是把调用链简化了
     *
     * @param metaObject
     * @param fieldName
     * @param fieldType
     * @param fieldVal
     * @param <T>
     * @param <E>
     * @return
     */
    private <T, E extends T> MetaObjectHandler customStrictUpdateFill(MetaObject metaObject, String fieldName, Class<T> fieldType, E fieldVal) {
        TableInfo tableInfo = findTableInfo(metaObject);
        if (tableInfo.isWithUpdateFill()) {
            Supplier<E> fieldValFunction = () -> fieldVal;
            tableInfo.getFieldList().stream()
                    .filter(j -> j.getProperty().equals(fieldName) && fieldType.equals(j.getPropertyType())
                            && j.isWithUpdateFill()).findFirst()
                    .ifPresent(j -> customStrictFillStrategy(metaObject, fieldName, fieldValFunction));
        }
        return this;
    }

    /**
     * 重写这个方法的逻辑，对有值的属性进行覆盖
     * {@link MetaObjectHandler#strictFillStrategy(MetaObject, String, Supplier)}
     *
     * @param metaObject
     * @param fieldName
     * @param fieldVal
     * @return
     */
    private MetaObjectHandler customStrictFillStrategy(MetaObject metaObject, String fieldName, Supplier<?> fieldVal) {
        Object obj = fieldVal.get();
        if (Objects.nonNull(obj)) {
            metaObject.setValue(fieldName, obj);
        }
        return this;
    }

    private Long getUserId() {
        try {
            return 1L;
        } catch (Exception e) {
            LOGGER.debug("自动注入失败，未获取到认证信息");
        }
        return 1L;
    }

    private String getNickName() {
        try {
            return "tom";
        } catch (Exception e) {
            LOGGER.debug("自动注入失败，未获取到认证信息");
        }
        return "tom";
    }
}
