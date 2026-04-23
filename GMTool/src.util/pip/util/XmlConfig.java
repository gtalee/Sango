package pip.util;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlConfig {
	/** XML 中的某一内容的集合。<messge>这些内容将被存入以“message”为key的ArrayList中</message> */
	public HashMap<String,ArrayList<String>> propSets = new HashMap<String,ArrayList<String>>();
	/** 一些扩展的属性 */
	public Properties extraProperties = new Properties();

	public String getStringProperty(String key) {
		Class kls = getClass();
		try {
			Field fld = kls.getField(key);
			if (fld != null) {
				if (fld.getType() == int.class) {
					return Integer.toString(fld.getInt(this));
				} else if (fld.getType() == boolean.class) {
					return Boolean.toString(fld.getBoolean(this));
				} else if (fld.getType() == byte.class) {
					return Byte.toString(fld.getByte(this));
				} else if (fld.getType() == char.class) {
					return Character.toString(fld.getChar(this));
				} else if (fld.getType() == double.class) {
					return Double.toString(fld.getDouble(this));
				} else if (fld.getType() == float.class) {
					return Float.toString(fld.getFloat(this));
				} else if (fld.getType() == long.class) {
					return Long.toString(fld.getLong(this));
				} else if (fld.getType() == short.class) {
					return Short.toString(fld.getShort(this));
				} else {
					return fld.get(this).toString();
				}
			}
		} catch (IllegalAccessException ex) {
		} catch (IllegalArgumentException ex) {
		} catch (SecurityException ex) {
		} catch (NoSuchFieldException ex) {
		}
		return extraProperties.getProperty(key);
	}

	public boolean getBooleanProperty(String key) {
		String v = getStringProperty(key);
		if (v == null) {
			return false;
		}
		v = v.toLowerCase();
		return (v.equals("t") || v.equals("true") || v.equals("1") || v.equals("y")
				|| v.equals("yes"));
	}
	
	private String rootPath;

	protected void extractRootPath(String cfgName) {
		int index = cfgName.lastIndexOf(File.separator);
		rootPath = cfgName.substring(0, index);
		System.out.println("rootpath:"+rootPath);
	}
	
	/** 从xml文件中初始化参数 */
	public void init(String cfgName) throws Exception {
		FileInputStream in = null;
		try {
			if(rootPath == null) {
				extractRootPath(cfgName);
			}
			in = new FileInputStream(cfgName);
			SAXParserFactory pf = SAXParserFactory.newInstance();
			pf.setValidating(false);
			pf.setNamespaceAware(false);
			SAXParser p = pf.newSAXParser();
			p.parse(in, new DefaultConfigHandler());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (RuntimeException e) {
				}
			}
		}
	}
	
	public ArrayList<String> getSet(String key) {
		ArrayList<String> ret = propSets.get(key);
		if (ret == null) {
			ret = new ArrayList<String>();
		}
		return ret;
	}

    public boolean setConfig(String key, String value, boolean extra) {
        try {
            Class kls = getClass();
            Field fld = null;
            try {
                fld = kls.getField(key);
            } catch (Exception ex1) {
            }
            if (fld != null) {
                if (fld.getType() == int.class) {
                    if (value.startsWith("0X") ||value.startsWith("0x")) {
                        fld.setInt(this, Integer.parseInt(value.substring(2), 16));
                    } else {
                            fld.setInt(this, Integer.parseInt(value));
                    }
                } else if (fld.getType() == boolean.class) {
                    value = value.toLowerCase();
                    fld.setBoolean(this, value.equals("true") ||
                                   value.equals("t") ||
                                   value.equals("y") ||
                                   value.equals("yes") ||
                                   value.equals("on") ||
                                   value.equals("ok") ||
                                   value.equals("1"));
                } else if (fld.getType() == byte.class) {
                    if (value.startsWith("0X") ||value.startsWith("0x")) {
                        fld.setByte(this, Byte.parseByte(value.substring(2), 16));
                    } else {
                        fld.setByte(this, Byte.parseByte(value));
                    }
                } else if (fld.getType() == char.class) {
                    if (value.startsWith("\\U") ||value.startsWith("\\u")) {
                        fld.setChar(this, (char)Integer.parseInt(value.substring(2), 16));
                    } else {
                        fld.setChar(this, value.charAt(0));
                    }
                } else if (fld.getType() == double.class) {
                    fld.setDouble(this, Double.parseDouble(value));
                } else if (fld.getType() == float.class) {
                    fld.setFloat(this, Float.parseFloat(value));
                } else if (fld.getType() == long.class) {
                    if (value.startsWith("0X") ||value.startsWith("0x")) {
                        fld.setLong(this, Long.parseLong(value.substring(2), 16));
                    } else {
                        fld.setLong(this, Long.parseLong(value));
                    }
                } else if (fld.getType() == short.class) {
                    if (value.startsWith("0X") ||value.startsWith("0x")) {
                        fld.setShort(this, Short.parseShort(value.substring(2), 16));
                    } else {
                        fld.setShort(this, Short.parseShort(value));
                    }
                } else {
                    fld.set(this, value);
                }
                return true;
            } else if (extra) {
                extraProperties.setProperty(key ,value);
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
//	public boolean setConfig(String key, String value, boolean extra) {
//		try {
//			Class kls = getClass();
//			Field fld = null;
//			try {
//				fld = kls.getField(key);
//			} catch (Exception ex1) {
//			}
//			if (fld != null) {
//				if (fld.getType() == int.class) {
//					fld.setInt(this, Integer.parseInt(value));
//				} else if (fld.getType() == boolean.class) {
//					value = value.toLowerCase();
//					fld.setBoolean(this, value.equals("true")
//							|| value.equals("t") || value.equals("y")
//							|| value.equals("yes") || value.equals("on")
//							|| value.equals("ok") || value.equals("1"));
//				} else if (fld.getType() == byte.class) {
//					fld.setByte(this, Byte.parseByte(value));
//				} else if (fld.getType() == char.class) {
//					fld.setChar(this, value.charAt(0));
//				} else if (fld.getType() == double.class) {
//					fld.setDouble(this, Double.parseDouble(value));
//				} else if (fld.getType() == float.class) {
//					fld.setFloat(this, Float.parseFloat(value));
//				} else if (fld.getType() == long.class) {
//					fld.setLong(this, Long.parseLong(value));
//				} else if (fld.getType() == short.class) {
//					fld.setShort(this, Short.parseShort(value));
//				} else {
//					fld.set(this, value);
//				}
//				return true;
//			} else if (extra) {
//				extraProperties.setProperty(key, value);
//				return true;
//			}
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//		return false;
//	}
    private int maxInclude = 5;
	class DefaultConfigHandler extends DefaultHandler {
		// 存储当前content的内容
		StringBuffer sb = new StringBuffer();

		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			sb.setLength(0);
			if ("property".equals(qName)) {
				setConfig(attributes.getValue("key"), attributes.getValue("value"), true);
			} else if ("include".equals(qName)) {
				if (maxInclude > 0) {
					try {
						String fileName = attributes.getValue("file");
						File f = new File(rootPath + File.separator + fileName);
						if (!f.exists()) {
							f = new File(System.getProperty("user.home")+ File.separator + pip.gm.fw.BaseConfig.configFileDirName, fileName);
						}
						if (f.exists() && f.isFile()) {
							maxInclude--;
							init(f.getAbsolutePath());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			if (!"property".equals(qName)) {
				String s = sb.toString().trim();
				if (s.length() > 0) {
					ArrayList<String> lst = propSets.get(qName);
					if (lst == null) {
						lst = new ArrayList<String>();
						propSets.put(qName, lst);
					}
					lst.add(s);
				}
			}

		}

		public void characters(char ch[], int start, int length) throws SAXException {
			sb.append(new String(ch, start, length));
		}

		public void endDocument() throws SAXException {
		}
	}

}
