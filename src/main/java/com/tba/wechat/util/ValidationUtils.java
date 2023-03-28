package com.tba.wechat.util;

import cn.hutool.core.collection.CollectionUtil;
import com.tba.wechat.exception.CustomException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author theAplyBoy
 * @version V1.0
 * @Date 2021-11-1 8:57
 * @description
 */
public class ValidationUtils {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private ValidationUtils() {
    }

    /**
     * 验证集合
     *
     * @param list
     * @param <T>
     */
    public static <T> void validate(List<T> list) {
        if (CollectionUtil.isNotEmpty(list)) {
            list.forEach(ValidationUtils::validate);
        }
    }

    /**
     * 验证单个对象
     *
     * @param obj Bean
     * @param <T> Bean 泛型
     */
    public static <T> void validate(T obj) {
        Set<ConstraintViolation<T>> resultSet = VALIDATOR.validate(obj, Default.class);
        ValidationUtils.checkResult(resultSet);
    }

    /**
     * 验证对象某个属性
     *
     * @param obj          Bean
     * @param propertyName 属性名称
     * @param <T>          Bean 泛型
     */
    public static <T> void validateProperty(T obj, String propertyName) {
        Set<ConstraintViolation<T>> resultSet = VALIDATOR.validateProperty(obj, propertyName, Default.class);
        ValidationUtils.checkResult(resultSet);
    }

    /**
     * 结果解析器
     *
     * @param resultSet 验证结果
     * @param <T>       验证对象泛型
     */
    private static <T> void checkResult(Set<ConstraintViolation<T>> resultSet) {
        if (CollectionUtil.isNotEmpty(resultSet)) {
            throw new CustomException(resultSet
                    .stream()
                    .map(item -> item.getPropertyPath().toString() + " " + item.getMessage())
                    .collect(Collectors.toList())
                    .toString());
        }
    }

}
