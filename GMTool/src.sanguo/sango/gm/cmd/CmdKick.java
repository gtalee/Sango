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

public class CmdKick extends GmFunction {
	public void registerPackage(PDataFactory factory) {
	}
	public PDProcessor getPackageProcessor() {
		return null;
	}
    public void kick(AbstractClient aworld, String id, String time) throws Exception {
    	PKickRequest pkg = new PKickRequest();
    	pkg.playerId = Integer.parseInt(id);
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
    	String s = Res.format(CmdKickRES.kickMsg,id ,time);
        aworld.onMessage(IMessage.MSG_TYPE_LOG, s, new int[]{pkg.playerId});

        GmChatTrace.traceGm(aworld.getConfig().account, aworld.getUniqServerId(), GmChatTrace.MODE_KICK, pkg.playerId, s, aworld.getConfig().title + ":");
    }
    public boolean exec(String cmd, AbstractClient aworld, String []s) throws Exception {
    	if (s != null && s.length >= 1) {
			if (cmd != null) {
				if (isCommand(aworld.auth, cmd)) {
					if (s.length == 3) {
						kick(aworld, s[1], s[2]);
						return true;
					}
				}
			}
    	}
    	return false;
    }
    public long getAuth() {
    	return AuthConstants.kick;
    }
    public String getCommand(Auth auth) {
    	if (auth.hasAuth(getAuth())) {
    		return "kick";
    	}
        return null;
    }
    public String getName(Auth auth) {
    	if (auth.hasAuth(getAuth())) {
    		return CmdKickRES.cmdName;
    	}
        return null;
    }
    public String getDescription(Auth auth) {
    	if (auth.hasAuth(getAuth())) {
    		return CmdKickRES.cmdDesc;
    	}
        return null;
    }
    /**
	 * ÌßÍæ¼Ò
	 * playerId									int
	 * time										long (0 ½â³ý)
	 */
    public class PKickRequest extends UAData implements GmConstants {
		public int getAppDataType() {
			return ADMIN_KICK_CLIENT;
		}
		public int playerId;
		public long time;
		public String[] getProperties() {
			return new String[] {"playerId", "time" };
		}
	}
}
