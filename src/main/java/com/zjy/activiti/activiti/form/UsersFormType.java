package com.zjy.activiti.activiti.form;

import java.util.Arrays;

import org.activiti.engine.form.AbstractFormType;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 表单用户字段
 * @author 郑金友
 *
 */
public class UsersFormType extends AbstractFormType {

    @Override
    public String getName() {
        return "users";
    }

    @Override
    public Object convertFormValueToModelValue(String propertyValue) {
        String[] split = StringUtils.split(propertyValue, ",");
        return Arrays.asList(split);
    }

    @Override
    public String convertModelValueToFormValue(Object modelValue) {
        return String.valueOf(modelValue);
    }

}
