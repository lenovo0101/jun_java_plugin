package com.jun.plugin.poi.test.excel.converter.collections;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.jun.plugin.poi.test.excel.converter.AbstractFieldConvertor;
import com.jun.plugin.poi.test.excel.converter.FieldValueConverter;
import com.jun.plugin.poi.test.excel.core.handler.ConverterHandler;
import com.jun.plugin.poi.test.excel.exception.ConversionException;
import com.jun.plugin.poi.test.excel.vo.OutValue;

/**
 * @author Wujun
 *
 * @date 2016-3-24
 * Description:  
 */
public class CollectionConverter extends AbstractFieldConvertor {
	private final String splitCharacter;
	public final static String SPACE_SPLIT=" ";
	public final static String SPACE_COMMA=",";
	public final static String SPACE_SEMICOLON=";";
	
	public CollectionConverter() {
		splitCharacter=SPACE_COMMA;
	}

	@Override
	public boolean canConvert(Class type) {
		return type.equals(ArrayList.class) ||type.equals(List.class)|| type.equals(HashSet.class)||type.equals(Set.class)
				|| type.equals(LinkedList.class);
	}

	@Override
	public OutValue toObject(Object source, ConverterHandler converterHandler) {
		if(source==null){
			return null;
		}
		Collection collection = (Collection)source;
		StringBuilder bd=new StringBuilder();
		for (Object obj : collection) {
			Class  class1 = obj.getClass();
			FieldValueConverter converter = converterHandler.getLocalConverter(class1);
			if(converter==null){
				throw new ConversionException("can find the converter for type ["
								+ class1 + "]");
			}
			OutValue outValue = converter.toObject(obj, converterHandler);
			bd.append(outValue.getValue());
			bd.append(splitCharacter);
		}
		StringBuilder builder = bd.replace(bd.length()-1, bd.length(), "");
		return OutValue.stringValue(builder.toString());
	}

	@Override
	public Object fromString(String cell, ConverterHandler converterHandler,
			Type targetType) {
		if(Strings.isNullOrEmpty(cell)){
			return null;
		}
		if(targetType==null){
			return null;
		}
		Collection collection = (Collection) createCollection(targetType);
		if(collection==null) {
			return null;
		}
		Class genericsClass = getGenericsClass(targetType);
		FieldValueConverter converter = converterHandler.getLocalConverter(genericsClass);
		if(converter==null){
			throw new ConversionException("can find the converter for type ["
							+ genericsClass + "]");
		}
		
		String[] splitArr = cell.split(splitCharacter);
		
		 for (int i = 0; i < splitArr.length; i++) {
			 Object object = converter.fromString(splitArr[i], converterHandler,genericsClass);
			 collection.add(object);
			}
		
		return collection;
	}

	private Collection createCollection(Type type) {
		if (type == null) {
			return null;
		}
		if(type.equals(ArrayList.class)||type.equals(List.class)){
			return Lists.newArrayList();
		}else if(type.equals(HashSet.class)||type.equals(Set.class)){
			return Sets.newHashSet();
		}else if(type.equals(LinkedList.class)){
			return Lists.newArrayList();
		}else{
			return null;
		}
		
	}
	private  Class getGenericsClass(Type fc ) {
		Class fieldClazz;
	        if (fc instanceof ParameterizedType) // ??????????????????????????????
	        {
	            ParameterizedType pt = (ParameterizedType) fc;
	 
	            fieldClazz = (Class) pt.getActualTypeArguments()[0]; //??????????????????class???????????????
	        }else{
	        	fieldClazz=String.class;
	        }
	 
	    return fieldClazz;
	}
}
