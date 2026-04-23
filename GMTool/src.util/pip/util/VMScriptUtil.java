package pip.util;

import java.lang.reflect.Field;

public class VMScriptUtil {
	/**
	 * 根据模版生成字符串。
	 * @param obj 为模版对应的对象。
	 * @param template 为用“{#}”分割的模版序列，从第一个开始直到匹配成功
	 * @return 空，当对象为空或者需要的参数为0或者为空，否则返回第一个模版
	 * 模版中{}标注参量，其中this为引用对象本身，否则为对象中字段。仅一级。
	 */
	
	public static String genFirstString(Object obj, String template) {
		int k = 0;
		while (k < template.length()) {
			int k2 = template.indexOf("{#}", k);
			if (k2 > 0) {
				String s = genString(obj, template.substring(k, k2));
				if (s != null) {
					return s;
				}
				k = k2 + 3;
			} else {
				return genString(obj, template.substring(k));
			}
		}
		return null;
	}
	public static String genString(Object obj, String template) {
		if (obj == null) {
			return null;
		}
		while (true) {
			int k = template.indexOf('{');
			if (k < 0) {
				break;
			}
			int k2 = template.indexOf('}', k);
			if (k2 < 0) {
				break;
			}
			String key = template.substring(k + 1, k2);
			if (key.equals("this")) {
				template = template.substring(0, k) + obj + template.substring(k2 + 1);
			} else {
				String s = getStringValue(obj, key);
				if (s == null) {
					return null;
				}
				template = template.substring(0, k) + s + template.substring(k2 + 1);
			}
		}
		return template;
	}

	/**
	 * 获得对象的某一字段的字符串表达. 如果是Number,必须是大于0的. 否则返回空
	 */
	public static String getStringValue(Object obj, String field) {
		try {
			Field fld = obj.getClass().getField(field);
			Class type = fld.getType();
			if (type == byte.class) {
				byte v = fld.getByte(obj);
				if (v > 0) {
					return String.valueOf(v);
				}
			} else if (type == short.class) {
				short v = fld.getShort(obj);
				if (v > 0) {
					return String.valueOf(v);
				}
			} else if (type == boolean.class) {
				return String.valueOf(fld.getBoolean(obj));
			} else if (type == int.class) {
				int v = fld.getInt(obj);
				if (v > 0) {
					return String.valueOf(v);
				}
			} else if (type == long.class) {
				long v = fld.getLong(obj);
				if (v > 0) {
					return String.valueOf(v);
				}
			} else {
				Object v = fld.get(obj);
				if (v != null) {
					return v.toString();
				}
			}
		} catch (Exception e) {
		}
		return null;
	}

}
