package pip.util;

import java.io.PrintStream;
import java.io.PrintWriter;

import java.io.ByteArrayOutputStream;

public class Debug {
	public static final boolean DbgReceive = false;
	public static final boolean DbgSending = false;
	public static final boolean DEBUG = false;
	public static void debug(String s) {
		System.out.println(s);
	}
	public static void main(String s[]) {
		traceMe();
	}
	public static void traceMe() {
	    if (DEBUG) {
			try {
				throw new Exception("SS");
			} catch (Exception e) {
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				PrintStream out = new PrintStream(bout);
				e.printStackTrace(out);
				out.close();
				String s = bout.toString();
				int k = s.indexOf('\n')+1;
				k = s.indexOf('\n', k)+1;
				k = s.indexOf("at ", k)+3;
				int kk = s.indexOf('\n', k);
				if (kk > 0) {
					System.out.println(s.substring(k, kk));
				} else {
					System.out.println("No trace info");
				}
			}
		}
	}
	public static void traceMe(String msg) {
		if (DEBUG) {
			try {
				throw new Exception("SS");
			} catch (Exception e) {
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				PrintStream out = new PrintStream(bout);
				e.printStackTrace(out);
				out.close();
				String s = bout.toString();
				int k = s.indexOf('\n')+1;
				k = s.indexOf('\n', k)+1;
				k = s.indexOf("at ", k)+3;
				int kk = s.indexOf('\r', k);
				if (kk > 0) {
					System.out.println(s.substring(k, kk) + ":" + msg);
				} else {
					System.out.println("No trace info: " + msg);
				}
			}
		}
	}
    public static void reportIo(org.apache.mina.common.ByteBuffer d) {
    	d.mark();
    	int k = d.remaining();
    	byte data[] = new byte[k];
    	d.get(data);
    	d.reset();
    	System.out.println(StringUtil.getBytesReport("IO Report ", data, 0));
    }

}
