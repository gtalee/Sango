package sango.gm.cmd;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;

import pip.gm.fw.AbstractClient;
import pip.gm.fw.Auth;
import pip.gm.fw.AuthConstants;
import pip.gm.fw.BaseConfig;
import pip.gm.fw.GmFunction;
import pip.gm.fw.IMessage;
import pip.gm.fw.PDProcessor;
import pip.io.uwap.PDataFactory;
import pip.io.uwap.UAData;
import pip.io.uwap.UWapData;
import pip.util.Res;
import sango.GmConstants;

public class CmdAccountInfoReceice extends GmFunction {
	public void registerPackage(PDataFactory factory) {
		factory.register(GmConstants.ADMIN_ACCOUNT_INFO_SERVER, GMAccountInfo.class);
	}
	public PDProcessor getPackageProcessor() {
		return new PDProcessor() {
			public boolean process(pip.gm.fw.AbstractClient master, UWapData data) {
				if (data instanceof GMAccountInfo) {
					GMAccountInfo d = (GMAccountInfo)data;
					if (master.auth.hasAuth(AuthConstants.delete)) {
						master.onMessage(IMessage.MSG_TYPE_LOG, Res.format(CmdAccountInfoReceiveRES.superInfo, String.valueOf(d.accountId), d.accountName, d.password, d.phone), null);
					} else {
						master.onMessage(IMessage.MSG_TYPE_LOG, Res.format(CmdAccountInfoReceiveRES.info, String.valueOf(d.accountId), d.accountName, d.password, d.phone), null);
					}
					
					// 查询账号VIP等级
					if (BaseConfig.DOMAIN.equals(BaseConfig.DOMAIN_PIP)) {
						int[] vipLevel = queryVIPLevel(d.accountId);
						if (vipLevel != null) {
							master.onMessage(IMessage.MSG_TYPE_LOG, "VIP: <style name=\"紫\">" + getStar(vipLevel[0]) +
									"</style>/<style name=\"黄\">" + getStar(vipLevel[1]) + "</style>", null);
						}
					}
					return false;
				}
				return false;
			}
		};
	}
	
	public static String getStar(int count) {
		if (count == 0) {
			return "-";
		}
		char[] arr = new char[count];
		Arrays.fill(arr, '★');
		return new String(arr);
	}
	
	public boolean exec(String cmd, AbstractClient world, String[] s) throws Exception {
		return false;
	}

	public String getCommand(Auth auth) {
		return null;
	}

	public String getName(Auth auth) {
		return null;
	}
	public long getAuth() {
    	return 0;
    }
	public String getDescription(Auth auth) {
		return null;
	}
	/*
	 * <Param type="int" title="序列号"/>
		<Param type="int" title="帐号Id"/>
		<Param type="String" title="帐号名"/>
		<Param type="String" title="帐号密码"/>
		<Param type="String" title="电话"/>
		<format>帐号: &lt;action title="查看本帐号角色信息" command="showplayers ${1}"&gt;&lt;action title="查看本帐号密保信息" command="bindinfo ${1}"&gt;${1}[${2}]&lt;/action&gt;&lt;/action&gt; 密码:[${3}] 电话:[${4}]</format>
	 */

	public static class GMAccountInfo extends UAData implements GmConstants {
		public int getAppDataType() {
			return ADMIN_ACCOUNT_INFO_SERVER;
		}
		public int serialNum;
		public int accountId;
		public String accountName;
		public String password;
		public String phone;
		public String[] getProperties() {
			return new String[] { "serialNum", "accountId", "accountName","password","phone"};
		}
	}
}
