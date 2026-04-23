package sango.gm.cmd;

import pip.gm.fw.AbstractClient;
import pip.gm.fw.Auth;
import pip.gm.fw.AuthConstants;
import pip.gm.fw.GmChatTrace;
import pip.gm.fw.GmFunction;
import pip.gm.fw.IMessage;
import pip.gm.fw.PDProcessor;
import pip.io.uwap.PDataFactory;
import pip.io.uwap.UAData;
import pip.io.uwap.UWapData;
import sango.GmConstants;
import sango.gm.GmStub;
import sango.gm.cmd.CmdPlayerInfo.PPlayerDataReceived;

public class CmdLogin extends GmFunction {
	public void registerPackage(PDataFactory factory) {
		factory.register(GmConstants.ADMIN_LOGIN_SERVER, GMPLoginSucceed.class);
	}
	public PDProcessor getPackageProcessor() {
		return new PDProcessor() {
			public boolean process(pip.gm.fw.AbstractClient master, UWapData data) {
				if (data instanceof GMPLoginSucceed) {
					master.registerLoginPkg(pkg);
					master.onMessage(IMessage.MSG_TYPE_LOG, CmdLoginRES.loginSucceed, null);
					return true;
				}
				return false;
			}
		};
	}
	GMPLogin pkg;
	private void login(AbstractClient world, String name, String pass) {
		long t = GmChatTrace.getAuth(name, pass, world.getUniqServerId());
		world.onMessage(IMessage.MSG_TYPE_LOG, CmdLoginRES.serverId + "[" + world.getUniqServerId() + "]", null);
		if (t != 0) {
			((GmStub)world).setAuth(t);
			world.getConfig().setConfig("account", name, true);
			world.getConfig().setConfig("password", pass, true);
			pkg = new GMPLogin();
			pkg.userName = name;
			pkg.password = GmChatTrace.getInstancePassword("sanguo", world.getUniqServerId());
			world.sndRequest(pkg);
		} else {
			world.onMessage(IMessage.MSG_TYPE_LOG, CmdLoginRES.accountErr, null);
			((GmStub)world).setAuth(0);
		}
	}
	public boolean exec(String cmd, AbstractClient world, String[] s) throws Exception {
		if (s != null && s.length >= 1) {
			if (cmd != null) {
				if (isCommand(world.auth, cmd)) {
					if (s.length == 1) {
						login(world, world.getConfig().getStringProperty("account"), world.getConfig().getStringProperty("password"));
						return true;
					} else if (s.length == 2 && s[1].equals("off")) {
						world.close();
						return true;
					} else if (s.length == 3) {
						login(world, s[1], s[2]);
						return true;
					} else {
						world.onMessage(IMessage.MSG_TYPE_LOG, CmdLoginRES.wrongPara + getDescription(world.auth), null);
						return true;
					}
				}
			}
		}
		return false;
	}

	public String getCommand(Auth auth) {
		return "gmlogin";
	}

	public String getName(Auth auth) {
		return CmdLoginRES.cmdName;
	}
	public long getAuth() {
    	return 0;
    }
	public String getDescription(Auth auth) {
		return CmdLoginRES.cmdDesc;
	}
	/**
	 *  µ«¬º’ ∫≈ Direct£∫Client->Server Command£∫LOGIN = (byte) 231 
	 * 1 UserName ”√ªß√˚ String 
	 * 2 Password √‹¬Î String
	 */
	public static class GMPLogin extends UAData implements GmConstants {
		public int getAppDataType() {
			return ADMIN_LOGIN_CLIENT;
		}
		public int serialNum;
		public String userName;
		public String password;

		public String[] getProperties() {
			return new String[] { "serialNum", "userName", "password" };
		}
	}
	public static class GMPLoginSucceed extends UAData implements GmConstants {
		public int getAppDataType() {
			return ADMIN_LOGIN_SERVER;
		}
		public int serialNum;
		public String[] getProperties() {
			return new String[] { "serialNum",};
		}
	}
}
