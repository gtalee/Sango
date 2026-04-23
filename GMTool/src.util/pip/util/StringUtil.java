package pip.util;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
public class StringUtil {
	public static SimpleDateFormat fmt = new SimpleDateFormat();
	public static String getDateFormat(long time, String fmtStr) {
		if (fmtStr != null) {
			fmt.applyPattern(fmtStr);
		}
		return fmt.format(new Date(time));
	}
	public static String getDurationString(long time) {
		String title[] = {"天", "小时", "分", "秒", "毫秒"};
		long data[] = new long[6];
		long scale[] = {Long.MAX_VALUE, 24L, 60L, 60L, 1000L};
		for (int i = title.length; i-- > 0 && time > 0; ) {
			data[i] = time % scale[i];
			time /= scale[i];
		}
		StringBuilder buf = new StringBuilder();
		boolean started = false;
		for (int i = 0; i < title.length; i++) {
			if (started || data[i] > 0) {
				buf.append(data[i]);
				buf.append(title[i]);
			}
		}
		return buf.toString();
	}
	public static int getDoubleStrSize(String s, boolean isRawData) {
		int ret = 0;
		if (isRawData) {
			for (char c : s.toCharArray()) {
				if (c > 0x7f) {
					ret++;
				}
				ret ++;
			}
		} else {
			int status = 0;
			for (char c : s.toCharArray()) {
				switch (status) {
				case 0:
					if (c > 0x7f) {
						ret++;
					}
					if (c == '<') {
						status = 1;
					} else {
						ret ++;
					}
					break;
				case 1:
					if (c == '>') {
						status = 0;
					}
					break;
				}
			}
		} 
		return ret;
	}
	public static String fillSpaceForDoubleStr(String s[], int n, boolean formal) {
		StringBuffer buf = new StringBuffer();
		int len = 0;
		String ns = null;
		if (formal) { // 内部无控制,将控制转化
			for (char c : s[0].toCharArray()) {
				if (c > 0x7f) {
					if (len < n - 1) {
						len += 2;
						buf.append(c);
					} else {
						break;
					}
				} else {
					if (len < n) {
						len ++;
						buf.append(c);
					} else {
						break;
					}
				}
			}
			ns = buf.toString();
			ns = formal(ns);
		} else { // 内部有控制
			int stat = 0;
			for (char c : s[0].toCharArray()) {
				switch (stat) {
				case 0:
					if (c > 0x7f) {
						if (len < n - 1) {
							len += 2;
							buf.append(c);
						} else {
							break;
						}
					} else if (c == '<') {
						buf.append(c);
						stat = 1;
					} else {
						len ++;
						buf.append(c);
					}
					break;
				case 1:
					buf.append(c);
					if (c == '>') {
						stat = 0;
					}
					break;
				}
			}
			ns = buf.toString();
		}
		buf.setLength(0);
		if (s.length > 1) {
			buf.append(s[1]);
		}
		buf.append(ns);
		if (s.length > 2) {
			buf.append(s[2]);
		}
		for (; len < n;len++) {
			buf.append(' ');
		}
		return buf.toString();
	}
	public static int getSpaceForDoubleStr(String s, int n) {
		int len = 0;
		for (char c : s.toCharArray()) {
			if (c > 0x7f) {
				if (len < n - 1) {
					len += 2;
				} else {
					break;
				}
			} else {
				if (len < n) {
					len ++;
				} else {
					break;
				}
			}
		}
		return n - len;
	}
	public static String formatMultiRows(ArrayList<String[]> items, int n, int baseSpace, boolean isRawData) {
		return formatMultiRows(null, "", items, n, baseSpace, isRawData);
	}
	public static String formatMultiRows(String title, String indent, ArrayList<String[]> items, int n, int baseSpace, boolean isRawData) {
		StringBuffer buf = new StringBuffer();
		int len = 1;
		for (String s[] : items) {
			int k = getDoubleStrSize(s[0], isRawData);
			if (len < k) {
				len = k;
			}
		}
		len += baseSpace;
		int numCol = n/len;
		if (numCol == 0) {
			numCol = 1;
		}
		int nulRow = (items.size() + numCol - 1)/numCol;
		for (int i = 0; i < nulRow; i++) {
			if (i > 0) {
				buf.append('\n');
				buf.append(indent);
			}
			for (int j = i; j < items.size(); j += nulRow) {
				// 不希望行尾有排版空格
				String tt[] = items.get(j);
				if (j + nulRow < items.size()) {
					buf.append(fillSpaceForDoubleStr(tt, len, isRawData));
				} else {
					if (tt.length > 1) {
						buf.append(tt[1]);
					}
					buf.append(isRawData ? StringUtil.formal(items.get(j)[0]) : items.get(j)[0]);
					if (tt.length > 2) {
						buf.append(tt[2]);
					}
				}
			}
		}
		StringBuffer buf2 = new StringBuffer();
		if (title != null) {
			buf2.append(title);
		}
		if (nulRow > 1) {
			buf2.append('\n');
			buf2.append(indent);
		} else {
			for (int i = 0; i < baseSpace; i++) {
				buf2.append(" ");
			}
		}
		buf2.append(buf.toString());
		return buf2.toString();
	}
	public static String replaceAll(String s, String old, String neW) {
		int lstK = 0;
		int n = old.length();
		int nn = neW.length();
		while (true) {
			int k = s.indexOf(old, lstK);
			if (k < 0) {
				break;
			}
			lstK = k + nn;
			s = s.substring(0, k) + neW + s.substring(k + n);
		}
		return s;
	}
	public static String getHex(String value, int len, String encoding) {
		byte[] b = null;
		try {
			b = value.getBytes(encoding);
		} catch (UnsupportedEncodingException e) {
			try {
				b = value.getBytes("GBK");
			} catch (UnsupportedEncodingException e1) {
			}
		}
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			buf.append(getHex(b[i], 2));
		}
		if (buf.length() > len) {
			buf.setLength(len);
		} else {
			for (int i = buf.length(); i < len; i++) {
				buf.append('0');
			}
		}
		return buf.toString();
	}
	public static String getBytesReport(String name, byte[]data, int start) {
		StringBuffer buf = new StringBuffer(name);
		for (; start < data.length; start++) {
			
			switch (start & 0x0f) {
			case 0:
				buf.append("\n0x" + getHex(start, 6) + ": ");
				break;
			case 8:
				buf.append("-");
				break;
			default:	
				buf.append(" ");
				break;
			} 
			buf.append(getHex(data[start], 2));
		}
		return buf.toString();
	}
	public static String getHex(long value, int len) {
		String ret = "0000000000" + Long.toHexString(value).toUpperCase();
		return ret.substring(ret.length() - len);
	}
	
	public static String getHex(int value, int len) {
		String ret = "0000000000" + Integer.toHexString(value).toUpperCase();
		return ret.substring(ret.length() - len);
	}
	
	public static String[] splitString(String s, char sep) {
		ArrayList<String> lst = splitStrings(s, sep);
		String[] ss = new String[lst.size()];
		return lst.toArray(ss);
	}
	public static ArrayList<String> splitStrings(String s, char sep) {
		ArrayList<String> lst = new ArrayList<String>();
		int p = 0;
		while (p < s.length()) {
			int k = s.indexOf(sep, p);
			if (k < 0) {
				k = s.length();
			}
			lst.add(s.substring(p, k));
			p = k+1;
		}
		return lst;
	}
	/** 
	 * 按命令行参数格式解释字符串，其中空格是分隔符，"\"是转义符，单引号和双引号是合并符。
	 */
    public static String[] splitLines(String s) {
        int mode = 0; // 当前状态， 0 初始状态  2 一般, 1 在引号中
        ArrayList<String> lst = new ArrayList<String>();
        int n = 0;
        int len = s.length();
        StringBuffer buf = new StringBuffer();
        char lstQ = 0;
        while (n < len) {
            char c = s.charAt(n++);
            switch (mode) {
            case 0: // 参数起始状态，忽略前导空格
                if (c == '\\') { // 转意字符，可是空格或引号
                    buf.append(s.charAt(n++));
                    mode = 2;
                } else if (c == '\'' || c == '\"') {
                    mode = 1;
                    lstQ = c;
                } else if (c == ' ') { // 忽略初始的空格
                } else {
                    buf.append(c);
                    mode = 2;
                }
                break;
            case 2: // 开始读入参数，遇到空格结束
                if (c == '\\') { // 转意字符，可是空格或引号
                    buf.append(s.charAt(n++));
                } else if (c == '\'' || c == '\"') {
                    mode = 1;
                    lstQ = c;
                } else if (c == ' ') {
                    if (buf.length() > 0) {
                        lst.add(buf.toString());
                        buf.setLength(0);
                    }
                    mode = 0;
                } else {
                    buf.append(c);
                }
                break;
            case 1: // 在引号内
                if (c == '\\') {
                    buf.append(s.charAt(n++));
                } else if (c == lstQ) {
                    mode = 2;
                } else {
                    buf.append(c);
                }
                break;
            }
        }
        if (buf.length() > 0) {
            lst.add(buf.toString());
        }
        String []ret = new String[lst.size()];
        for (int i = ret.length - 1; i >= 0; i--) {
            ret[i] = lst.get(i);
        }
        return ret;
    }


	public static String[] NUM_UNIT = { "万", "亿", "兆" };
	public static String getNumbetString(long number, int n) {
		if (n == -1) {
			n = 9;
		}
		StringBuffer ret = new StringBuffer();
		if (number < 10000) {
			ret.append(number);
		} else {
			String s = String.valueOf(number);
			int numLen = s.length();
			int secEnd = numLen % 4;
			if (secEnd == 0) {
				secEnd = 4;
			}
			int loopTimes = (numLen - 1) / 4 + 1;
			int secFrom = 0;
			while (loopTimes > 0) {
				ret.append(s.substring(secFrom, secEnd));
				if (loopTimes > 1 && loopTimes <= NUM_UNIT.length + 1) {
					ret.append(NUM_UNIT[loopTimes - 2]);
				} else if (loopTimes > 1) {
					ret.append(" ");
				}
				secFrom = secEnd;
				secEnd += 4;
				loopTimes--;
			}
		}
		int len = ret.length();
		if (n < 4 || len <= n) { // 直接取字符串
			return ret.toString();
		} else {
			int i;
			String ext = null;
			for (i = 0; i < NUM_UNIT.length && len > n; i++) {
				len -= 5;
				ext = ret.toString().substring(len + 1);
				ret.setLength(len);
			}
			int k = n - len - 1;
			if (k > 0) {
				ret.append(".");
				ret.append(ext.substring(0, k));
			}
			ret.append(NUM_UNIT[i - 1]);
			return ret.toString();
		}
	}
	public static String removeTags(String s) {
		StringBuilder buf = new StringBuilder();
		int mode = 0;
		for (char c : s.toCharArray()) {
			switch (mode) {
			case 0:
				if (c == '<') {
					mode = 1;
				} else {
					buf.append(c);
				}
				break;
			case 1:
				if (c == '>') {
					mode = 0;
				}
				break;
			}
		}
		return buf.toString();
	}
	public static String formal(String s) {
		s = replace(s, "&", "&amp;");
		s = replace(s, "<", "&lt;");
		s = replace(s, ">", "&gt;");
		return s;
	}
	public static String replace(String s, String oldStr, String newStr) {
		if (s == null) {
			return null;
		}
		StringBuffer ret = new StringBuffer();
		int n = s.length();
		int l1 = oldStr.length();
		int k = 0;
		while (k < n) {
			int kk = s.indexOf(oldStr, k);
			if (kk == -1) {
				ret.append(s.substring(k));
				break;
			}
			ret.append(s.substring(k, kk));
			ret.append(newStr);
			k = kk + l1;
		}
		return ret.toString();
	}
}
