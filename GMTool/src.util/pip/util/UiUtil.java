package pip.util;

import java.io.IOException;
import java.io.InputStream;

import javax.swing.ImageIcon;

public class UiUtil {
	public static ImageIcon getIcon(String url) {
		try {
			InputStream in = UiUtil.class.getResourceAsStream(url);
			byte buf[] = readFull(in);
			return new ImageIcon(buf);
		} catch (IOException e) {
		}
		return null;
	}
	public static byte[] readFull(InputStream in) throws IOException {
		byte []buf = new byte[1024];
		int n = 0;
		while (true) {
			if (buf.length == n) {
				byte tmp[] = new byte[n << 1];
				System.arraycopy(buf, 0, tmp, 0, n);
				buf = tmp;
			}
			int k = in.read(buf, n, buf.length - n);
			if (k > 0) {
				n+= k;
			} else {
				break;
			}
		}
		byte []ret = new byte[n];
		System.arraycopy(buf, 0, ret, 0, n);
		return ret;
	}

}
