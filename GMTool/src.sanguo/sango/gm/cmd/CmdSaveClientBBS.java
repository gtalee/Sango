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
import pip.gm.fw.ArrayOfParameters;

public class CmdSaveClientBBS extends GmFunction implements ArrayOfParameters {
	public void registerPackage(PDataFactory factory) {
	}
	public PDProcessor getPackageProcessor() {
		return null;
	}
    public void kick(AbstractClient aworld, String ss[]) throws Exception {
    	PSaveClientBBSRequest pkg = new PSaveClientBBSRequest();
    	pkg.minLevel = Integer.parseInt(ss[1]);
    	pkg.maxLevel = Integer.parseInt(ss[2]);
    	pkg.message = ss[3];
    	int n = ss.length - 4;
    	pkg.data = new PSaveClientBBSTask[n];
    	for (int i = 0; i < n; i++) {
    		pkg.data[i] = new PSaveClientBBSTask();
        	String s = ss[i + 4];
    		int k = s.indexOf(":");
    		if (k > 0) {
	    		pkg.data[i].activeitem = s.substring(0, k);
	    		pkg.data[i].detail = s.substring(k+1);
    		}
    	}
    	aworld.sndRequest(pkg);
    	String s = Res.format(CmdSaveClientBBSRES.saveMsg, pkg.minLevel ,pkg.maxLevel, pkg.message, n);

    	aworld.onMessage(IMessage.MSG_TYPE_LOG, s, new int[]{});
        GmChatTrace.traceGm(aworld.getConfig().account, aworld.getUniqServerId(), GmChatTrace.MODE_DELETE_MAIL,0, s, aworld.getConfig().title + ":");
    }
    public boolean exec(String cmd, AbstractClient aworld, String []s) throws Exception {
    	if (s != null && s.length >= 1) {
			if (cmd != null) {
				if (isCommand(aworld.auth, cmd)) {
					if (s.length > 3) {
						kick(aworld, s);
						return true;
					}
				}
			}
    	}
    	return false;
    }
    public long getAuth() {
    	return AuthConstants.add;
    }
    public String getCommand(Auth auth) {
    	if (auth.hasAuth(getAuth())) {
    		return "set-tips";
    	}
        return null;
    }
    public String getName(Auth auth) {
    	if (auth.hasAuth(getAuth())) {
    		return CmdSaveClientBBSRES.cmdName;
    	}
        return null;
    }
    public String getDescription(Auth auth) {
    	if (auth.hasAuth(getAuth())) {
    		return CmdSaveClientBBSRES.cmdDesc;
    	}
        return null;
    }
	/**
	 * 保存客服公告到xml文件
	 * serial                   int
	 * minLevel                 int                 等级下限
	 * maxLevel                 int                 等级上限
	 * textexplation            String            文字说明
	 * 循环N次
	 * size                     short             
	 *    	activeitem               String            活动名称
	 * 		detail                   String            更新具体内容
	 */
    public class PSaveClientBBSRequest extends UAData implements GmConstants {
		public int getAppDataType() {
			return ADMIN_SAVECLIENTBBS_CLIENT;
		}
		public int serialNum = 1;
		public int minLevel;
		public int maxLevel;
		public String message;
		public PSaveClientBBSTask[] data;
		public String[] getProperties() {
			return new String[] {"serialNum", "minLevel", "maxLevel", "message", "data"};
		}
	}
    public class PSaveClientBBSTask extends UAData implements GmConstants {
    	public String activeitem;
    	public String detail;
    	public String[] getProperties() {
			return new String[] {"activeitem", "detail" };
		}
    }
    String paras[][][] = CmdSaveClientBBSRES.paras;
    public String getParameterSetName(int n) {
    	if (n >= 0 && n < paras.length) {
    		return paras[n][0][0];
    	}
    	return null;
    }
	public int getNumOfParameterSet() {
		return paras.length;
	}
	public int getNumOfParameters(int n) {
		if (n >= 0 && n < paras.length) {
			return paras[n].length - 1;
		}
		return 0;
	}
	public String getParameterTitle(int n, int i) {
		if (n >= 0 && n < paras.length) {
			if (i >= 0 &&  i < paras[n].length - 1) {
				return paras[n][i + 1][0];
			}
		}
		return null;
	}
	public String getParameterTips(int n, int i) {
		if (n >= 0 && n < paras.length) {
			if (i >= 0 &&  i < paras[n].length - 1) {
				return paras[n][i + 1][1];
			}
		}
		return null;
	}
}
