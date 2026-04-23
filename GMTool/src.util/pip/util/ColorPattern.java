package pip.util;

import java.awt.Color;

public class ColorPattern {
	private static Color colors[] = {
		new Color(0xffCC00), new Color(0xCC00ff), new Color(0x00ffCC), new Color(0xff00CC), 
		new Color(0xCCff00), new Color(0x00CCff), new Color(0xff8800), new Color(0x8800ff), 
		new Color(0x00ff88), new Color(0xff0088), new Color(0x88ff00), new Color(0x0088ff), 
		new Color(0x884400), new Color(0x440088), new Color(0x008844), new Color(0x880044), 
		new Color(0x448800), new Color(0x004488), new Color(0xCC4400), new Color(0x4400CC), 
		new Color(0x00CC44), new Color(0xCC0044), new Color(0x44CC00), new Color(0x0044CC),
	};
	/** 
	 * 根据序号分配一个区别于其他序号的颜色。多用于作图
	 */
	public static Color getColor(int index) {
		if (index < 0) {
			index = -index;
		}
		return colors[index % colors.length];
	}

}
