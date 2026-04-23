package pip.util;

import java.util.*;
import java.text.*;

public class Res {
	public static String format(String template, Object ... arguments) {
		return MessageFormat.format(template, arguments);
	}
}
