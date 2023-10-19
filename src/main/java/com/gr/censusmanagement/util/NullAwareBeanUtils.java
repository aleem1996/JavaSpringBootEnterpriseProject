package com.gr.censusmanagement.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import org.apache.commons.beanutils.BeanUtilsBean;

public class NullAwareBeanUtils {
	private static final NullAwareBeanUtilsBean nullAwareBeanUtilsBean = new NullAwareBeanUtilsBean();

	public static void copyProperties(final Object dest, final Object orig) throws IllegalAccessException, InvocationTargetException {
		nullAwareBeanUtilsBean.copyProperties(dest, orig);
	}
}

class NullAwareBeanUtilsBean extends BeanUtilsBean {

	@Override
	public void copyProperty(Object dest, String name, Object value) throws IllegalAccessException, InvocationTargetException {
		if (value == null)
			return;
		else {
			if (value instanceof Integer) {

				if ((int) value == 0) {
					return;
				}
			} else if (value instanceof Double || value instanceof Long) {
				if ((double) value == 0.0) {
					return;
				}
			} else if (value instanceof Collection<?>) {
				if (((Collection<?>) value).isEmpty()) {
					return;
				}
			}
		}
		super.copyProperty(dest, name, value);
	}

}
