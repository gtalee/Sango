package sango.gm.cmd;

import pip.gm.fw.AbstractClient;
import pip.gm.fw.Auth;
import pip.gm.fw.AuthConstants;
import pip.gm.fw.GmFunction;
import pip.gm.fw.IMessage;
import pip.gm.fw.PDProcessor;
import pip.io.uwap.PDataFactory;
import sango.data.POnlinePlayer;
import sango.gm.GmStub;
import pip.util.*;
public class CmdConfig extends GmFunction {
	public void registerPackage(PDataFactory factory) {
	}
	public PDProcessor getPackageProcessor() {
		return null;
	}
    public boolean exec(String cmd, AbstractClient awd, String []s) throws Exception {
    	GmStub world = (GmStub)awd;
        if (s != null && s.length >= 2) {
            if (cmd != null) {
                if (isCommand(awd.auth, cmd)) {
                    if ("addcmd".equals(s[1]) && awd.auth.hasAuth(AuthConstants.root)) {
                        for (int i = 2; i < s.length; i++) {
                            world.con.addCommand("sango.gm.cmd." + s[i]);
                        }
                        return true;
                    } else if ("delcmd".equals(s[1]) && awd.auth.hasAuth(AuthConstants.root)) {
                        for (int i = 2; i < s.length; i++) {
                            world.con.delCommand("sango.gm.cmd." + s[i]);
                        }
                        return true;
                    } else if ("chatTarget".equals(s[1]) && s.length == 3) {
                    	int k = s[2].indexOf('[');
                    	int id;
                    	if (k < 0) {
                    		id = Integer.parseInt(s[2]);
                    	} else {
                    		id = Integer.parseInt(s[2].substring(0, k));
                    	}
                    	POnlinePlayer u = world.gameForm.users.getUser(id);
                    	if (u == null) {
                    		u = new POnlinePlayer(id);
                    		if (k > 0) {
                    			u.name = s[2].substring(k+1, s[2].length() - 1);
                    		}
                    	}
                    	world.gameForm.cmdFld.setChattingTarget(u);
                    	return true;
                    } else if ("inputText".equals(s[1]) && s.length > 2) {
                    	StringBuffer buf = new StringBuffer();
                    	for (int i = 2; i < s.length; i++) {
                    		buf.append(s[i]);
                    		buf.append(" ");
                    	}
                    	String ss = buf.toString();
                    	if (ss.startsWith("/")) {
                    		world.gameForm.cmdFld.insertTxt(0, buf.toString());
                    	} else {
                    	world.gameForm.cmdFld.insertTxt(buf.toString());
                    	}
                    	world.gameForm.cmdFld.fld.requestFocus();
                    	return true;
                    } else if ("shortcut".equals(s[1])) {
                        if (s.length == 2) {
                            world.onMessage(IMessage.MSG_TYPE_LOG, world.gameForm.cmdFld.getShortCuts(), null);
                        } else {
                            StringBuffer buf = new StringBuffer(s[2]);
                            for (int i = 3; i < s.length; i++) {
                                buf.append(" ");
                                buf.append(s[i]);
                            }
                            world.gameForm.cmdFld.addShortCut(buf.toString());
                        }
                        return true;
                    } else if ("title".equals(s[1]) ) {
                        StringBuffer buf = new StringBuffer(s[2]);
                        for (int i = 3; i < s.length; i++) {
                            buf.append(" ");
                            buf.append(s[i]);
                        }
                        world.gameForm.main.setTitle(buf.toString());
                        return true;
                    } else if ("server".equals(s[1]) && s.length == 3) {
                        int k = s[2].indexOf(':');
                        if (k > 0) {
                            world.config.port = Integer.parseInt(s[2].substring(k + 1));
                            world.config.host = s[2].substring(0, k);
                            world.onMessage(IMessage.MSG_TYPE_LOG, Res.format(CmdConfigRES.serverSetSucceed, world.config.host, Integer.valueOf(world.config.port)), null);
                            return true;
                        }
                    } else {
                        for (int i = 1; i < s.length; i++) {
                            boolean forceExtra = false;
                            String ps = s[i];
                            // 当配置以！开始时，为强制更新
                            if (ps.startsWith("!")) {
                                forceExtra = true;
                                ps = ps.substring(1);
                            }
                            int k = ps.indexOf('=');
                            if (k > 0) {
                                String key = ps.substring(0, k);
                                if (world.config.setConfig(key, ps.substring(k + 1), forceExtra)) {
                                    world.onMessage(IMessage.MSG_TYPE_LOG,  Res.format(CmdConfigRES.envSetSucceed, key), null);
                                } else {
                                    world.onMessage(IMessage.MSG_TYPE_LOG, Res.format(CmdConfigRES.envNameErr, key), null);
                                }
                            } else {
                                String v = world.config.getStringProperty(ps);
                                if (v == null) {
                                    world.onMessage(IMessage.MSG_TYPE_LOG, Res.format(CmdConfigRES.noEnv, ps), null);
                                } else {
                                    world.onMessage(IMessage.MSG_TYPE_LOG, Res.format(CmdConfigRES.envListOk, ps, v), null);
                                }
                            }
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public String getCommand(Auth auth) {
    	return "set";
    }
    public String getName(Auth auth) {
        return CmdConfigRES.cmdName;
    }
    public long getAuth() {
    	return 0;
    }
    public String getDescription(Auth auth) {
    	return CmdConfigRES.description;
    }
}
