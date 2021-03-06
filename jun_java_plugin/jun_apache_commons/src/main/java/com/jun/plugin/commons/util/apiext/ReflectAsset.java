package com.jun.plugin.commons.util.apiext;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.ref.Reference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jun.plugin.commons.util.callback.IConvertValue;
import com.jun.plugin.commons.util.callback.IConvertValueDate;

@SuppressWarnings("rawtypes")
public abstract class ReflectAsset {
	public static Logger logger = LoggerFactory.getLogger(ReflectAsset.class);

	private static String[] excludeGet = new String[] { "getClass" };

	public static Object invokeStaticMothed(String className,
			String methodName,Class[] paramclass,Object... param) throws Exception {
		Class c = Class.forName(className);
		Method m = c.getMethod(methodName, paramclass);
		Object retobj = m.invoke(c, param);
		return retobj;
	}
	/****
	 * įŽååæ°
	 * @param className
	 * @param methodName
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static Object invokeStaticMothed(String className,
			String methodName,Object... param) throws Exception {
		Class[] paramClass=null;
		if (!org.apache.commons.lang3.ArrayUtils.isEmpty(param)) {
			paramClass = new Class[param.length];
			for (int i = 0; i < param.length; i++) {
				paramClass[i] = param.getClass();
			}
		} 
		return invokeStaticMothed(className,methodName,paramClass,param);
	}
	
	

	public static Object invokeMothed(Object invokeObj, String methodName,
			Object... param) {
		Class c = invokeObj.getClass();
		if (StringUtil.isNull(methodName)) {
			logger.error("åå°ä¸­įŧēå°æšæŗ");
			return null;
		}
		Method[] m = c.getMethods();// .getMethod(methodName,ptypes);
		Method exeMethod = null;
		for (int i = 0; i < m.length; i++) {
			Method tempMethod = m[i];
			if (!methodName.equals(tempMethod.getName())) {// æšæŗåä¸į¸į­
				continue;
			}
			Class[] classAry = tempMethod.getParameterTypes();
			if (classAry.length != param.length) {// æšæŗįåæ°ä¸åšé
				continue;
			}
			if ((param == null && classAry == null)
					|| (classAry.length == 0 && param.length == 0)) {// æ åæ°č°į¨
				exeMethod = tempMethod;
				break;
			}

			boolean isthisMethod = true;
			for (int j = 1; j < classAry.length; j++) {
				Class classAryEle = classAry[j];
				Object paramEle = param[j];
				if (classAryEle.isArray() && paramEle.getClass().isArray()) {// TODO
																				// åæ°æ¯æ°įģįåĻäŊæĨč¯ĸåŽįåįąģåīŧīŧīŧīŧīŧ
					try {
						Object[] paramArry = (Object[]) param;
						Object[] classInstArry = (Object[]) classAryEle
								.newInstance();
						if (paramArry[0].getClass().isAssignableFrom(
								classInstArry[0].getClass())) {
							isthisMethod = false;
							break;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (!paramEle.getClass().isArray()
						&& !classAryEle.isArray()) {
					if (!paramEle.getClass().isAssignableFrom(classAryEle)) {
						isthisMethod = false;
						break;
					}
				} else {
					isthisMethod = false;
					break;
				}
			}
			if (isthisMethod) {
				exeMethod = tempMethod;
				break;
			}

		}
		if (exeMethod != null) {
			try {
				return exeMethod.invoke(invokeObj, param);
			} catch (Exception e) {
				logger.error("åå°č°į¨æšæŗåēéã");
			}
		}
		return null;
	}

	/***
	 * æ¯åĻæ¯åēæŦæ°æŽįąģå
	 * 
	 * @param clz
	 * @return
	 */
	public static boolean isPrimitieClass(Class clz) {
		try {
			return ((Class) clz.getField("TYPE").get(null)).isPrimitive();
		} catch (Exception e) {
			return false;
		}
	}

	/****
	 * æžå°getæšæŗä¸æ˛Ąæåæ°įæšæŗ
	 * 
	 * @return
	 */
	public static List<String> findGetMethod(Class clz) {
		List<String> methList = new ArrayList<String>();
		Method[] m = clz.getMethods();
		if (m.length == 0) {
			return methList;
		}
		for (int i = 0; i < m.length; i++) {
			Method method = m[i];
			String methodName = method.getName();
			if (!methodName.startsWith("get")) {
				continue;
			}
			if (ArrayUtils.contains(excludeGet, methodName)) {
				continue;
			}
			Class[] classAry = method.getParameterTypes();
			if (classAry.length == 0) {
				methList.add(method.getName());
			}
		}
		return methList;
	}

	/***
	 * æžå°getæšæŗå¯šåēįå
	 * 
	 * @param clz
	 * @return
	 */
	public static List<String> findGetField(Class clz) {
		List<String> retList = new ArrayList<String>();
		List<String> methodList = findGetMethod(clz);
		for (String methodname : methodList) {
			String ele = methodname.substring(3);
			retList.add(ele.substring(0, 1).toLowerCase() + ele.substring(1));
		}
		return retList;
	}

	/***
	 * æBeanå¯ščąĄčŊŦä¸ēMap
	 * 
	 * @param obj
	 * @return
	 */
	public static Map<String, String> convertMapFromBean(Object obj,
			Map<String, IConvertValue> convermap, boolean allowNull) {
		Map<String, String> retmap = new HashMap<String, String>();
		if (obj == null) {
			return retmap;
		}
		List<String> fields = findGetField(obj.getClass());
		if (CollectionUtils.isNotEmpty(fields)) {
			for (String field : fields) {
				try {
					String value = null;
					if (convermap == null || !convermap.containsKey(field)) {
						value = BeanUtils.getProperty(obj, field);
					} else {
						IConvertValue convert = convermap.get(field);
						if (ReflectAsset
								.isInterface(convert.getClass(),
										"cn.rjzjh.commons.util.callback.IConvertValueDate")) {// æ¯æļé´čŊŦæĸ
							Date oriDate = (Date) PropertyUtils.getProperty(
									obj, field);
							IConvertValueDate convInst = (IConvertValueDate) convert;
							value = convInst.getStr(oriDate);
						} else {
							value = BeanUtils.getProperty(obj, field);
						}

					}
					if (!allowNull && StringUtil.isNull(value)) {// ä¸åčŽ¸ä¸ēįŠēäŊåæ¯įŠēåŧ
						continue;
					}
					if (StringUtil.isNotNull(value)
							&& value.startsWith("org.apache.openjpa.enhance")) {// įąjpaįæįå¯ščąĄä¸æžåĨ
						continue;
					}
					retmap.put(field, value);
				} catch (Exception e) {
				}
			}
		}
		return retmap;
	}

	/***
	 * å¤æ­įąģæ¯åĻåŽį°æä¸ĒæĨåŖ
	 * 
	 * @param c
	 * @param szInterface
	 * @return
	 */
	public static boolean isInterface(Class c, String szInterface) {
		Class[] face = c.getInterfaces();
		for (int i = 0, j = face.length; i < j; i++) {
			if (face[i].getName().equals(szInterface)) {
				return true;
			} else {
				Class[] face1 = face[i].getInterfaces();
				for (int x = 0; x < face1.length; x++) {
					if (face1[x].getName().equals(szInterface)) {
						return true;
					} else if (isInterface(face1[x], szInterface)) {
						return true;
					}
				}
			}
		}
		if (null != c.getSuperclass()) {
			return isInterface(c.getSuperclass(), szInterface);
		}
		return false;
	}

	/***
	 * åžå°å¯ščąĄįåąæ§æčŋ°
	 * 
	 * @param clazz
	 * @return
	 */
	public static PropertyDescriptor[] getPropertyDescriptors(Class clazz) {
		BeanInfo beanInfo = null;
		try {
			beanInfo = Introspector.getBeanInfo(clazz);
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
		return beanInfo.getPropertyDescriptors();
	}

	public static Class getClassRefType(PropertyDescriptor propertyDescriptor) {
		Field[] fields = propertyDescriptor.getClass().getSuperclass()
				.getDeclaredFields();
		if (fields == null || fields.length <= 0) {
			return null;
		} else {
			for (Field field : fields) {
				if ("classRef".equals(field.getName())) {
					try {
						field.setAccessible(true); // ä¸åŽčĻčŽžįŊŽä¸ēå¯čŽŋéŽ
						return (Class) ((Reference) field
								.get(propertyDescriptor)).get();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}

	public static Map<String, Class[]> getContextType(Class classz) {
		Field[] fs = classz.getDeclaredFields(); // åžå°ææįfields
		Map<String, Class[]> retMap = new HashMap<String, Class[]>();
		for (Field f : fs) {
			Class fieldClazz = f.getType(); // åžå°fieldįclassåįąģåå¨čˇ¯åž
			if (fieldClazz.isPrimitive())
				continue; // ã1ã //å¤æ­æ¯åĻä¸ēåēæŦįąģå
			if (fieldClazz.getName().startsWith("java.lang"))
				continue; // getName()čŋåfieldįįąģåå¨čˇ¯åžīŧ
			if (fieldClazz.isAssignableFrom(List.class)) // ã2ã
			{
				Type fc = f.getGenericType(); // åŗéŽįå°æšīŧåĻææ¯Listįąģåīŧåžå°åļGenericįįąģå
				if (fc == null)
					continue;
				if (fc instanceof ParameterizedType) // ã3ãåĻææ¯æŗååæ°įįąģå
				{
					ParameterizedType pt = (ParameterizedType) fc;
					Class genericClazz = (Class) pt.getActualTypeArguments()[0]; // ã4ã
																					// åžå°æŗåéįclassįąģåå¯ščąĄã
					retMap.put(f.getName(), new Class[] { genericClazz });
					// Map<String, Class> m1 = prepareMap(genericClazz);
					// m.putAll(m1);
				}
			} else if (fieldClazz.isAssignableFrom(Map.class)) {
				Type fc = f.getGenericType();
				if (fc == null)
					continue;
				if (fc instanceof ParameterizedType) {
					ParameterizedType pt = (ParameterizedType) fc;
					Class param0 = (Class) pt.getActualTypeArguments()[0]; // ã4ã
					Class param1 = (Class) pt.getActualTypeArguments()[1]; // ã4ã
					retMap.put(f.getName(), new Class[] { param0, param1 });
				}
			} else if (fieldClazz.isArray()) {
				retMap.put(f.getName(),
						new Class[] { fieldClazz.getComponentType() });
			}
		}
		return retMap;
	}

	public static void copyProperties(Object dest, Object orig) {
		try {
			BeanUtils.copyProperties(dest, orig);
		} catch (Exception e) {
			logger.error("å¤åļåąæ§åēé", e);
		}
	}

}
