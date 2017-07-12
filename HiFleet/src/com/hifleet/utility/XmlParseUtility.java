package com.hifleet.utility;

import java.lang.reflect.Field;

import org.w3c.dom.Element;

/**
 * @{# XmlParseUtility.java Create on 2015年4月12日 下午5:33:47
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */
public class XmlParseUtility {

	public static <T> T parse(Element element, Class<T> clazz) {
		try {
			T t = clazz.newInstance();
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
//				System.out.println("field.getName()"+field.getName()+"class"+clazz.getName());
				field.set(t, element.getAttribute(field.getName()));
			}
			return t;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
