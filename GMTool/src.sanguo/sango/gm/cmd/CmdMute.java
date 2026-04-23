package sango.gm.cmd;

import java.util.Date;

import pip.gm.fw.AbstractClient;
import pip.gm.fw.Auth;
import pip.gm.fw.AuthConstants;
import pip.gm.fw.GmChatTrace;
import pip.gm.fw.GmFunction;
import pip.gm.fw.IMessage;
import pip.gm.fw.PDProcessor;
import pip.io.uwap.PDataFactory;
import pip.io.uwap.UAData;
import pip.util.Res;
import sango.GmConstants;

public class CmdMute extends GmFunction {
	public void registerPackage(PDataFactory factory) {
	}
	public PDProcessor getPackageProcessor() {
		return null;
	}
    public void forbid(AbstractClient aworld, String id, String channel, String time) throws Exception {
    	PMuteRequest pkg = null;
    	int k = PMuteRequest.channelChars.indexOf(channel.charAt(0));
    	if (k < 0) {
    		throw new Exception("不支持禁言频道：" + channel + "。有效频道为：" + PMuteRequest.channelChars);
    	}
    	if (k == 5) { // 飞鸽
    		pkg = new PForbidMailRequest();
    	} else {
    		pkg = new PMuteRequest();
    	}
    	pkg.playerId = Integer.parseInt(id);
    	pkg.flag = 1 << k;
    	long t = 1000L * 60L;
    	int n = time.length();
    	if (time.endsWith(CmdKickRES.minuteFull)) {
    		time = time.substring(0, n - CmdKickRES.minuteFull.length());
    	} else if (time.endsWith(CmdKickRES.minute)) {
    		time = time.substring(0, n - CmdKickRES.minute.length());
    	} else if (time.endsWith(CmdKickRES.hour)) {
    		time = time.substring(0, n - CmdKickRES.hour.length());
    		t = 1000L * 60L * 60L;
    	} else if (time.endsWith(CmdKickRES.hourFull)) {
    		time = time.substring(0, n - CmdKickRES.hourFull.length());
    		t = 1000L * 60L * 60L;
    	} else if (time.endsWith(CmdKickRES.day)) {
    		time = time.substring(0, n - CmdKickRES.day.length());
    		t = 1000L * 60L * 60L * 24;
    	} 
    	t *= Long.parseLong(time);
    	if (t > 0) {
    		t += new Date().getTime();
    	}
    	pkg.time = t;
    	aworld.sndRequest(pkg);
    	String s = Res.format(CmdKickRES.muteMsg,id ,time);
        aworld.onMessage(IMessage.MSG_TYPE_LOG, s, new int[]{pkg.playerId});

        GmChatTrace.traceGm(aworld.getConfig().account, aworld.getUniqServerId(), GmChatTrace.MODE_KICK, pkg.playerId, s, aworld.getConfig().title + ":");

    }
    public boolean exec(String cmd, AbstractClient aworld, String []s) throws Exception {
    	if (s != null && s.length >= 1) {
			if (cmd != null) {
				if (isCommand(aworld.auth, cmd)) {
					if (s.length == 4) {
						forbid(aworld, s[1], s[2], s[3]);
						return true;
					}
				}
			}
    	}
    	return false;
    }
    public long getAuth() {
    	return AuthConstants.mute;
    }
    public String getCommand(Auth auth) {
    	if (auth.hasAuth(getAuth())) {
    		return "mute";
    	}
        return null;
    }
    public String getName(Auth auth) {
    	if (auth.hasAuth(getAuth())) {
    		return CmdKickRES.muteName;
    	}
        return null;
    }
    public String getDescription(Auth auth) {
    	if (auth.hasAuth(getAuth())) {
    		return CmdKickRES.muteDesc;
    	}
        return null;
    }
    public static class PMuteRequest extends UAData implements GmConstants {
    	public static String channelChars = "世国场籍私信";
		public int getAppDataType() {
			return ADMIN_CHAT_FORBID_CLIENT;
		}
		public int serialNum = 1;
		public int playerId;
		public int flag;
		public long time;
		public String[] getProperties() {
			return new String[] { "serialNum", "playerId", "flag", "time" };
		}
	}
    /**
	 * 禁飞鸽/解禁
	 * serial									int
	 * playerId									int
	 * time										long(如果时间是0，那么就是解封)
	 */
    public static class PForbidMailRequest extends PMuteRequest {
		public int getAppDataType() {
			return ADMIN_MAIL_FORBID_CLIENT;
		}
		public String[] getProperties() {
			return new String[] { "serialNum", "playerId", "time" };
		}
	}
}
