package pip.util;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import java.lang.reflect.*;
import pip.util.XmlConfig.DefaultConfigHandler;

import java.util.*;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * 为简化格式输出代码，将文本信息放入xml文件，其中文本内嵌对象取值字段，格式如下：
 *   {{ 转义花括弧。
 *   {!messageKey}：直接常量信息替换
 *   {field.subField}|{field}: 用字段值替换
 *   {field.subField()}|{field()}: 用方法返回值替换
 *   {msgKey[field]}：字段值为整数，作为下标找msgKey的资源，其中各项目以"|"分隔
 *   {msgKey[field]value-0,value-1,...}：字段值为浮点数或整数类型，作为下标找msgKey的资源，其中各项目以"|"分隔。取值小于value-n时选取数组第n个值
 *   {formatKey:!field?} | {formatKey:testField?valueField}：递归调用,如果响应对象不为空，或数值不为0
 *   {formatKey:field}：递归调用
 *   {formatKey:field[]}：递归循环调用
 *   {[index]}：输入参数为数组，这个功能类似于ResourceBundle中的 {index}
 * 
 * xml文件仅有 message tag有效，message 必有属性 key 和内容。内容会忽略前后空白，如果内容以"."开始或结束，这个字符将被忽略，其作用是保留其前、后的空白。
 * 数组定义格式为 <message key="arrayName">value1|value2|....</message>
 * 
 * @author Charles
 *
 */
public class XmlResourceBundle {
	/** 是否将格式化错误信息输出到控制台.在编写资源文件时可以打开方便调试  */
	public boolean validating = false;
	
	private HashMap<String,String> messages = new HashMap<String,String>();
	
	private XmlResourceBundle(String res) throws Exception {
		init(res);
	}
	public static XmlResourceBundle getRes(String name) {
		try {
			return new XmlResourceBundle(name);
		} catch (Exception e) {
			return null;
		}
	}

	/** 获取指定文本资源。资源id为String类型。源程序中将文本输出格式内容抽取出来写入xml文件，运行中调入  */
	public String getMessage(String key) {
		return messages.get(key);
	}
	/** 
	 * 格式化输出对象。 
	 * 其中messageKey是关键字，指向格式化文本内容。系统将内容中tag用指定对象属性替换后输出。
	 */
	public String format(String messageKey, Object obj) {
		if (obj == null) {
			// 对象为空，无输出
			// dbgInfo("对象为空：format(\"" + messageKey + "\", null)");
			return "";
		}
		StringBuilder buf = new StringBuilder();
		String msgString = messages.get(messageKey);
		if (msgString == null) {
			dbgInfo("没有请求资源：format(\"" + messageKey + "\", " + obj.toString() + ")");
			return msgString;
		}
		int msgLen = msgString.length();
		int pos = 0;
		while (pos < msgLen) {
			int tagStartPos = msgString.indexOf('{', pos);
			if (tagStartPos < 0) { // 已无tag，后续内容直接输出
				buf.append(msgString.substring(pos));
				break;
			}
			buf.append(msgString.substring(pos, tagStartPos));
			tagStartPos++;
			if (tagStartPos < msgLen) {
				if (msgString.charAt(tagStartPos) == '{') { // 连续2个“{”，作为转义符
					buf.append("{");
					pos = tagStartPos+1; 
					continue;
				}
				int tagEndPos = msgString.indexOf('}', tagStartPos);
				if (tagEndPos < 0) { // 开放性tag，忽略“{”
					buf.append(msgString.substring(pos));
					dbgInfo("Open Tag Ignored：format(\"" + messageKey + "\", obj) @" + tagStartPos);
					break;
				}
				String tagInfo = msgString.substring(tagStartPos, tagEndPos);
				buf.append(getValue(tagInfo, obj));
				pos = tagEndPos+1;
			} else {
				break;
			}
		}
		return buf.toString(); 
	}
	/** 根据属性描述抽取对应子对象  */
	private Object getObject(String propertyName, Object obj) {
		try {
			if (obj == null) {
				dbgInfo("对象为空：getObject(" + propertyName + ", null)");
				return null;
			}
			if (propertyName.equals("this")) { // 代表自身，通常为 obj.toString()
				return obj;
			}
			int k = propertyName.indexOf(".");
			if (k > 0) { // 间接引用
				String fldName = propertyName.substring(0, k);
				Object subObj = null;
				if (fldName.endsWith("()")) { // 方法引用
					fldName = fldName.substring(0, fldName.length() - 2);
					Method fld = obj.getClass().getDeclaredMethod(fldName, new Class[] {});
					Class kls = fld.getReturnType();
					subObj = fld.invoke(subObj, new Object[]{}); 
				} else { // 属性引用
					Field fld = obj.getClass().getDeclaredField(fldName);
					
					Class kls = fld.getType();
					if (kls == int.class) {
						subObj = Integer.valueOf(fld.getInt(obj));
					} else if (kls == boolean.class) {
						subObj = fld.getBoolean(obj) ? Integer.valueOf(1) : Integer.valueOf(0);
					} else if (kls == byte.class) {
						subObj = Byte.valueOf(fld.getByte(obj));
					} else if (kls == char.class) {
						subObj = Character.valueOf(fld.getChar(obj));
					} else if (kls == double.class) {
						subObj = Double.valueOf(fld.getDouble(obj));
					} else if (kls == float.class) {
						subObj = Float.valueOf(fld.getFloat(obj));
					} else if (kls == long.class) {
						subObj = Long.valueOf(fld.getLong(obj));
					} else if (kls == short.class) {
						subObj = Short.valueOf(fld.getShort(obj));
					} else {
						subObj = fld.get(obj);
					}
				}
				return getObject(propertyName.substring(k+1), subObj);
			} else {
				Object subObj = null;
				if (propertyName.endsWith("()")) {
					propertyName = propertyName.substring(0, propertyName.length() - 2);
					Method fld = obj.getClass().getDeclaredMethod(propertyName, new Class[] {});
					Class kls = fld.getReturnType();
					subObj = fld.invoke(obj, new Object[]{}); 
				} else {
					Field fld = obj.getClass().getDeclaredField(propertyName);
					Class kls = fld.getType();
					if (kls == int.class) {
						subObj = Integer.valueOf(fld.getInt(obj));
					} else if (kls == boolean.class) {
						subObj = fld.getBoolean(obj) ? Integer.valueOf(1) : Integer.valueOf(0);
					} else if (kls == byte.class) {
						subObj = Byte.valueOf(fld.getByte(obj));
					} else if (kls == char.class) {
						subObj = Character.valueOf(fld.getChar(obj));
					} else if (kls == double.class) {
						subObj = Double.valueOf(fld.getDouble(obj));
					} else if (kls == float.class) {
						subObj = Float.valueOf(fld.getFloat(obj));
					} else if (kls == long.class) {
						subObj = Long.valueOf(fld.getLong(obj));
					} else if (kls == short.class) {
						subObj = Short.valueOf(fld.getShort(obj));
					} else {
						subObj = fld.get(obj);
					}
				}
				return subObj;
			}
		} catch (Exception e) {
//			e.printStackTrace();
			dbgInfo("异常：getObject(" + propertyName + ", " + obj + ") " + e.getMessage());
		}
		return null;
	}
	/**
	 *
	 * @param propertyKey 
	 *    [index] 对应obj为数组，index为整数
	 *    ArrayKey[intFieldName] obj的对应字段为整数，ArrayKey为资源索引，对应资源以"|"分隔
     *    field.subField | field : obj对应字段调用 toString()
     *    field.subField() | field(): 用方法返回值替换
     *    formatKey:field：递归调用
     *    formatKey:field[]：递归循环调用,obj对应字段为数组
	 * @param obj
	 * @return
	 */
	private String getValue(String propertyKey, Object obj) {
//		System.out.println("Key=" + propertyKey);
		if (propertyKey.startsWith("!")) { // 直接常量引用，对内容不递归解析
			return messages.get(propertyKey.substring(1));
		}
		int k = propertyKey.indexOf(":");
		if (k > 0) { // formatKey:field[] 和 formatKey:field 
			if (propertyKey.endsWith("[]")) { // 数组循环
				String ptnKeyStr = propertyKey.substring(0, k);
				Object objs = getObject(propertyKey.substring(k+1, propertyKey.length()-2), obj);
				if (objs != null) {
					Class objClass = objs.getClass(); 
					if (objClass.isArray()) {
						int num = java.lang.reflect.Array.getLength(objs);
						Class kls = objClass.getComponentType();
						Object subObj = null;
						StringBuilder buf = new StringBuilder();
						for (int i = 0; i < num; i++) {
							if (kls == int.class) {
								subObj = Integer.valueOf(java.lang.reflect.Array.getInt(objs, i));
							} else if (kls == boolean.class) {
								subObj = Boolean.valueOf(java.lang.reflect.Array.getBoolean(objs, i));
							} else if (kls == byte.class) {
								subObj = Byte.valueOf(java.lang.reflect.Array.getByte(objs, i));
							} else if (kls == char.class) {
								subObj = Character.valueOf(java.lang.reflect.Array.getChar(objs, i));
							} else if (kls == double.class) {
								subObj = Double.valueOf(java.lang.reflect.Array.getDouble(objs, i));
							} else if (kls == float.class) {
								subObj = Float.valueOf(java.lang.reflect.Array.getFloat(objs, i));
							} else if (kls == long.class) {
								subObj = Long.valueOf(java.lang.reflect.Array.getLong(objs, i));
							} else if (kls == short.class) {
								subObj = Short.valueOf(java.lang.reflect.Array.getShort(objs, i));
							} else {
								subObj = java.lang.reflect.Array.get(objs, i);
							}
							buf.append(format(ptnKeyStr, subObj));
						}
						return buf.toString();
					} else {
						dbgInfo("非数组：getObject(" + propertyKey + ", " + obj + ") " );
						return "";
					}
				} else { // 数组对象为空，无输出
					return "";
				}
			} else if (propertyKey.indexOf('?') > 0) { // 非空 或 非0验证
				int qOff = propertyKey.indexOf('?');
				String ptnKeyStr = propertyKey.substring(0, k);
				String testProp = propertyKey.substring(k+1, qOff);
				boolean isNot = testProp.startsWith("!"); 
				if (isNot) {
					testProp = testProp.substring(1);
				}
				Object subObj = getObject(testProp, obj);
				if (isNot) {
					if (subObj != null) {
						Class kls = subObj.getClass();
						if (kls == Integer.class) {
							if (((Integer)subObj).intValue() != 0) {
								return "";
							}
						} else if (kls == Boolean.class) {
							if (((Boolean)subObj).booleanValue()) {
								return "";
							}
						} else if (kls == Byte.class) {
							if (((Byte)subObj).byteValue() != 0) {
								return "";
							}
						} else if (kls == Character.class) {
							if (((Character)subObj).charValue() != 0) {
								return "";
							}				
						} else if (kls == Double.class) {
							if (((Double)subObj).doubleValue() != 0) {
								return "";
							}				
						} else if (kls == Float.class) {
							if (((Float)subObj).floatValue() != 0) {
								return "";
							}				
						} else if (kls == Long.class) {
							if (((Long)subObj).longValue() != 0) {
								return "";
							}				
						} else if (kls == Short.class) {
							if (((Short)subObj).shortValue() != 0) {
								return "";
							}				
						} else if (kls == String.class) {
							if (((String)subObj).length() > 0) {
								return "";
							}				
						}
					}
				} else {
					if (subObj == null) {
						return "";
					}
					Class kls = subObj.getClass();
					if (kls == Integer.class) {
						if (((Integer)subObj).intValue() == 0) {
							return "";
						}
					} else if (kls == Boolean.class) {
						if (!((Boolean)subObj).booleanValue()) {
							return "";
						}
					} else if (kls == Byte.class) {
						if (((Byte)subObj).byteValue() == 0) {
							return "";
						}
					} else if (kls == Character.class) {
						if (((Character)subObj).charValue() == 0) {
							return "";
						}				
					} else if (kls == Double.class) {
						if (((Double)subObj).doubleValue() == 0) {
							return "";
						}				
					} else if (kls == Float.class) {
						if (((Float)subObj).floatValue() == 0) {
							return "";
						}				
					} else if (kls == Long.class) {
						if (((Long)subObj).longValue() == 0) {
							return "";
						}				
					} else if (kls == Short.class) {
						if (((Short)subObj).shortValue() == 0) {
							return "";
						}				
					} else if (kls == String.class) {
						if (((String)subObj).length() == 0) {
							return "";
						}				
					}
				}
				if (qOff != propertyKey.length() - 1) {
					subObj = getObject(propertyKey.substring(qOff+1), obj);
				}
				return format(ptnKeyStr, subObj);
			} else { // 不是数组
				String ptnKeyStr = propertyKey.substring(0, k);
				Object subObj = getObject(propertyKey.substring(k+1), obj);
				return format(ptnKeyStr, subObj);
			}
		} 
		
		k = propertyKey.indexOf("[");
		String fldName = null;
		if (k == 0) { // [index] 格式，直接索引对象
			fldName = propertyKey.substring(1, propertyKey.length() - 1); // 必须是以“]”结尾
			int index = Integer.parseInt(fldName);
			Class objClass = obj.getClass(); 
			if (objClass.isArray()) {
				int num = java.lang.reflect.Array.getLength(obj);
				if (index < 0 || index >= num) {
					index = 0;
				}
				return java.lang.reflect.Array.get(obj, index).toString();
			}
			dbgInfo("非数组：getObject(" + propertyKey + ", " + obj + ") " );
			return "";
		}
		
		if (k > 0) { // ArrayKey[intFieldName] 和 ArrayKey[intFieldName]v1,v2,...模式
			String idxStr = propertyKey.substring(0, k);
			String mm = messages.get(idxStr);
			if (mm == null) {
				dbgInfo("无预定义数组：getObject(" + propertyKey + ", " + obj + ") " );
				return "";
			}
			String[] lst = mm.split("\\|");
			int k2 = propertyKey.indexOf(']', k);
			if (k2 < 0) {
				k2 = propertyKey.length();
			}
			String fld = propertyKey.substring(k+1, k2);
			
			String values = propertyKey.substring(k2+1);
			if (values == null || values.length() == 0) {
				int v = getInt(obj, fld);
				if (v >= 0 && v < lst.length) {
					return lst[v];
				}
			} else {
				int v = getInt(obj, fld);
				int select = 0;
				String vs[] = values.split(",");
				for (; select < vs.length; select++) {
					double vv = Double.valueOf(vs[select]).doubleValue();
					if (v < vv) {
						break;
					}
				}
				if (select < lst.length) {
					return lst[select];
				}
			}
			return "";
		} 
		
		//  field.subField, field, field.subField(), field() 模式
		Object targetObj = getObject(propertyKey, obj);
		if (targetObj != null) {
			return targetObj.toString();
		}
		dbgInfo("无对象：getObject(" + propertyKey + ", " + obj + ") " );
		return "";
	}

	private int getInt(Object obj, String fld) {
		try {
			int k = fld.indexOf('.');
			if (k < 0) {
				Field fd = obj.getClass().getDeclaredField(fld);
				Class kls = fd.getType();
				if (kls == int.class) {
					return fd.getInt(obj);
				} else if (kls == boolean.class) {
					return fd.getBoolean(obj) ? 1 : 0;
				} else if (kls == byte.class) {
					return fd.getByte(obj);
				} else if (kls == char.class) {
					return (int)fd.getChar(obj);
				} else if (kls == double.class) {
					return (int)fd.getDouble(obj);
				} else if (kls == float.class) {
					return (int)fd.getFloat(obj);
				} else if (kls == long.class) {
					return (int)fd.getLong(obj);
				} else if (kls == short.class) {
					return (int)fd.getShort(obj);
				} else {
					dbgInfo("属性非数值类型：getInt(" + obj + ", " + fld + ") " );
					return 0;
				}
			} else {
				String fieldName = fld.substring(0, k);
				Field fd = obj.getClass().getDeclaredField(fieldName);
				Object obj2 = fd.get(obj);
				return getInt(obj2, fld.substring(k+1));
			}
		} catch (Exception e) {
			dbgInfo("无属性或属性非数值类型：getInt(" + obj + ", " + fld + ") " + e.getMessage() );
		} 
		return 0;
	}
	private double getNumber(Object obj, String fld) {
		try {
			int k = fld.indexOf('.');
			if (k < 0) {
				Field fd = obj.getClass().getDeclaredField(fld);
				Class kls = fd.getType();
				if (kls == int.class) {
					return (double)fd.getInt(obj);
				} else if (kls == boolean.class) {
					return (double)(fd.getBoolean(obj) ? 1 : 0);
				} else if (kls == byte.class) {
					return (double)fd.getByte(obj);
				} else if (kls == char.class) {
					return (double)fd.getChar(obj);
				} else if (kls == double.class) {
					return fd.getDouble(obj);
				} else if (kls == float.class) {
					return (double)fd.getFloat(obj);
				} else if (kls == long.class) {
					return (double)fd.getLong(obj);
				} else if (kls == short.class) {
					return (double)fd.getShort(obj);
				} else {
					dbgInfo("属性非数值类型：getNumber(" + obj + ", " + fld + ") " );
					return 0;
				}
			} else {
				String fieldName = fld.substring(0, k);
				Field fd = obj.getClass().getDeclaredField(fieldName);
				Object obj2 = fd.get(obj);
				return getNumber(obj2, fld.substring(k+1));
			}
		} catch (Exception e) {
			dbgInfo("获取数值类型属性异常：getNumber(" + obj + ", " + fld + ") " + e.getMessage());
		} 
		return 0;
	}
	public void init(String name) throws Exception {
		InputStream in = null;
		try {
			in = String.class.getResourceAsStream(name);
			SAXParserFactory pf = SAXParserFactory.newInstance();
			pf.setValidating(false);
			pf.setNamespaceAware(false);
			SAXParser p = pf.newSAXParser();
			p.parse(in, new DefaultConfigHandler());
		} catch (Exception e) {
			e.printStackTrace();
			dbgInfo("初始化异常:" + e.getMessage());
			throw e;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (RuntimeException e) {
				}
			}
		}
	}
	private class DefaultConfigHandler extends DefaultHandler {
		// 存储当前content的内容
		StringBuffer sb = new StringBuffer();
		String key = null;
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			sb.setLength(0);
			if ("message".equals(qName)) {
				key = attributes.getValue("key");
			}
		}

		public void endElement(String uri, String localName, String qName) throws SAXException {
			if ("message".equals(qName)) {
				String s = sb.toString().trim();
				if (s.length() > 0) {
					if (key != null) {
						if (s.startsWith(".")) {
							s = s.substring(1);
						}
						if (s.endsWith(".")) {
							s = s.substring(0, s.length() - 1);
						}
						messages.put(key, s);
					}
					key = null;
				}
			}

		}

		public void characters(char ch[], int start, int length) throws SAXException {
			sb.append(new String(ch, start, length));
		}

		public void endDocument() throws SAXException {
		}
	}
	private void dbgInfo(String s) {
		if (validating) {
			System.out.println(s);
		}
	}
}
