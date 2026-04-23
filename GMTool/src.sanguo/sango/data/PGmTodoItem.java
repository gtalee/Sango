package sango.data;

import java.util.Date;

import pip.io.uwap.UAData;
import sango.GmConstants;
import java.text.*;
/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
	/**
	 * GM请求列表
	 * 	请求Id						int
	 *  请求类型						byte(暂时都为0，以后分各种问题组)
	 * 	请求玩家Id					int
	 * 	请求玩家名					string
	 * 	请求内容						string
	 * 	请求状态						byte(0 未解决 1 解决)
	 * 	解决方案						string
	 * 	提交时间						long
	 *  玩家机型						string
	 *  玩家mapId					short
	 *  玩家x坐标					short
	 *  玩家y坐标					short
	 */

public class PGmTodoItem extends UAData implements GmConstants {
	public static DateFormat fmt = new SimpleDateFormat("yy-MM-dd HH:mm");
	public static final byte STAT_NEW = 0;
	public static final byte STAT_FINISHED = 1;
	public static final byte STAT_DELETED = 2;
    public int getAppDataType() {
        return 0;
    }
    public int mailId;
    public byte type;
    public int sourceId;
    public String author;
    public String content;
    public byte status; // (0 未解决 1 解决 2 删除)
    public String solution;
    public long postTime;
    public String device;
    public short sceneId;
    public short x;
    public short y;
    
    public String time;
    public String getTime() {
    	if (time == null && postTime != 0) {
    		time = fmt.format(new Date(postTime));
//    		time = new Date(postTime).toString();
    	}
    	return time;
    }
    public String getPos() {
    	return sceneId + "(" + x + "," + y + ")";
    }

    public String[] getProperties() {
        return new String[] {"mailId", "type", "sourceId", "author", "content", "status", "solution", "postTime", "device", "sceneId", "x", "y"}; 
    }
}
