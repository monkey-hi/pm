package com.zjy.activiti.activiti.form;

import org.activiti.engine.form.AbstractFormType;

public class DoubleFromType extends AbstractFormType {
	
	private static final String FORM_TYPE = "double";
	
	@Override
	public String getName() {
		return FORM_TYPE;
	}
	
	@Override
	public Object convertFormValueToModelValue(String propertyValue) {
		Double object = Double.valueOf(propertyValue);
		return object;
	}
	@Override
	public String convertModelValueToFormValue(Object modelValue) {
		if(null == modelValue){
			return null ;
		}
		return modelValue.toString();
	}
}
