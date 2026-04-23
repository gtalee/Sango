package sango.gm.cmd;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import org.apache.mina.common.ByteBuffer;

import pip.gm.fw.AbstractClient;
import pip.gm.fw.Auth;
import pip.gm.fw.AuthConstants;
import pip.gm.fw.BaseConfig;
import pip.gm.fw.GmChatTrace;
import pip.gm.fw.GmFunction;
import pip.gm.fw.IMessage;
import pip.gm.fw.PDProcessor;
import pip.io.uwap.PDataFactory;
import pip.io.uwap.UAData;
import pip.io.uwap.UWapData;
import pip.util.StringUtil;
import pip.util.ui.ChatRmFld;
import pip.util.ui.RichConsole;
import sango.GmConstants;
import sango.gm.GmStub;
import pip.util.*;
public class CmdChatting extends GmFunction {
	public static final int TYPE_WORLD = 0; // 世界
	public static final int TYPE_FACTION = 1; 
	public static final int TYPE_SCENE = 2;
	public static final int TYPE_HOMETOWN = 3;
	public static final int TYPE_GUILD = 4;
	public static final int TYPE_TEAM = 5;
	public static final int TYPE_PRIVATE = 6;
	public static final int TYPE_SYS = 7;
	private static String channelCode = CmdChattingRES.channelCode;
	
	/** 已经加入的聊天频道 */
    public int []overhearIds = new int[IMessage.MSG_TYPE_COMMAND + 1];

	public void registerPackage(PDataFactory factory) {
		factory.register((int)GmConstants.CHAT_SERVER, ClinetChatPkg.class);
		factory.register((int)GmConstants.ADMIN_CHAT_SERVER, SendChatMsgRetPkg.class);
	}
	public PDProcessor getPackageProcessor() {
		return new Processor();
	}
	 public void onGotClientMessage(GmStub m, ClinetChatPkg d) {
	        StringBuffer buf = new StringBuffer();
	        String appender = "";
	        String[] sourcer = null;
			if (d.fromId > 0) {
				sourcer = RichConsole.genPlayerHyperString(d.fromName, d.fromId);
			}
			
			String type;
			int typeId = IMessage.MSG_TYPE_PRIVATE;
	        switch (d.saidType) {
	        case TYPE_WORLD:
	        	type = String.valueOf(CmdChattingRES.channelCodes[0]);
	        	typeId = IMessage.MSG_TYPE_WORLD;
	        	break;
	        case TYPE_FACTION:
	        	type = String.valueOf(CmdChattingRES.channelCodes[1]);
	        	typeId = IMessage.MSG_TYPE_COUNTRY;
	        	if ("chat_wei".equals(d.channelName)) {
	        		appender = CmdChattingRES.countries[0];
	        	} else if ("chat_shu".equals(d.channelName)) {
	        		appender = CmdChattingRES.countries[1];
	        	} else if ("chat_wu".equals(d.channelName)) {
	        		appender = CmdChattingRES.countries[2];
	        	}
	        	break;
	        case TYPE_SCENE:
	        	type = String.valueOf(CmdChattingRES.channelCodes[2]);
	        	typeId = IMessage.MSG_TYPE_LOCAL;
	        	if (d.channelName.startsWith("chat_area")) {
	        		appender = d.channelName.substring(9);
	        	}
	        	break;
	        case TYPE_HOMETOWN:
	        	type = String.valueOf(CmdChattingRES.channelCodes[3]);
	        	typeId = IMessage.MSG_TYPE_NATIONAL;
	        	if (d.channelName.startsWith("chat_area")) {
	        		appender = d.channelName.substring(9);
	        	}
	        	if (d.channelName.startsWith("chat_native")) {
	        		appender = d.channelName.substring(11);
	        	}
	        	break;
	        case TYPE_GUILD:
	        	type = String.valueOf(CmdChattingRES.channelCodes[4]);
	        	typeId = IMessage.MSG_TYPE_GANG;
	        	if (d.channelName.startsWith("chat_guild")) {
	        		appender = d.channelName.substring(10);
	        	}
	        	break;
	        case TYPE_TEAM:
	        	type = String.valueOf(CmdChattingRES.channelCodes[5]);
	        	typeId = IMessage.MSG_TYPE_TEAM;
	        	break;
	        default:
	        	type = String.valueOf(CmdChattingRES.channelCodes[6]);
	        	typeId = IMessage.MSG_TYPE_PRIVATE;
	        	break;
	        }
	        String s = String.valueOf(d.fromId);
	        for (int j = s.length(); j < 6; j++) {
	        	buf.append(' ');
	        }
	        if (sourcer != null) {
	        	buf.append(sourcer[0]);
	        }
	        buf.append(s);
	        buf.append("[");

	        
	        if (d.fromId == -1) {
	        	if ("".equals(d.fromName)) {
	        		type = String.valueOf(CmdChattingRES.channelCodes[7]);
	        		buf.append(type);
	        	} else {
	        		// 将世界\地区频道的系统聊天放到其他栏目中.
	        		buf.append(StringUtil.formal(d.fromName));
	        		if (typeId != IMessage.MSG_TYPE_SYSTEM && typeId != IMessage.MSG_TYPE_GM) {
	        			typeId = IMessage.MSG_TYPE_OTHER;
	        		}
	        		if (typeId == IMessage.MSG_TYPE_LOCAL) {
	        			GmStub.Scene scene = m.gameInfo.getScene(overhearIds[ IMessage.MSG_TYPE_LOCAL]);
	        			if (scene != null) {
	        				buf.append(scene.name);
	        			}
	        		}
	        	}
	        } else {
	            buf.append(StringUtil.formal(d.fromName));
	        }
	        buf.append("] {");
	        buf.append(StringUtil.formal(d.msg));
	        buf.append("}");
	        buf.append(appender);
	        if (sourcer != null) {
	        	buf.append(sourcer[1]);
	        }
	        // 相关的ID
	        int iIds[] = null;
	        if (d.fromId > 0) {
	        	iIds = new int[]{d.fromId};
	        }
	        m.onMessage(typeId, genChatMessageAttach(buf.toString(), d.attachment), iIds);
	    }
	 public static final String qulityStr[] = {"白","绿","蓝","紫","黄","金"};
	 public static String genChatMessageAttach(String s, byte attachment[]) {
	        int kk = s.indexOf("/-1");
	        DataInputStream din = null;
	        try {
				if (kk >= 0 && attachment != null) {
					din = new DataInputStream(new ByteArrayInputStream(attachment));
					switch (din.read()) {
					case 1: // Goods
						int itemId = din.readInt();
						int itemInstanceId = din.readInt();
						String itemName = din.readUTF();
						
						if(BaseConfig.CVS_BRANCH >= BaseConfig.FixVersion_2012_05_29){
						    byte showImage = din.readByte();
						}
						
						byte showType = din.readByte();
						byte quanlity = din.readByte();
						return s.substring(0, kk) + "<style name=\"" + qulityStr[quanlity] + "\">[" + itemId + ":" + itemName + "]</style>" + s.substring(kk + 3);
					case 2: // Task
						int taskId = din.readInt();
						String desc = din.readUTF();
						return s.substring(0, kk) + Res.format(CmdChattingRES.taskInfo, Integer.valueOf(taskId), desc) + s.substring(kk + 3);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (din != null) {
					try {
						din.close();
					} catch (IOException e) {
					}
				}
			}
			return s;
	 }
	 
    private void execDialog(GmStub world, String pidStr) {
    	if (world.gameForm.chatDlg.size() > 3) {
    		world.onMessage(IMessage.MSG_TYPE_LOG, CmdChattingRES.tooManyWin, null);
    	} else {
			int id = Integer.parseInt(pidStr);
    		for (ChatRmFld ccf: world.gameForm.chatDlg) {
    			if (ccf.tracingUserId == id) {
    				world.onMessage(IMessage.MSG_TYPE_LOG, CmdChattingRES.alreadyOpened, null);
    				ccf.toFront();
    				return;
    			}
    		}
    		Integer pid = Integer.valueOf(id);
    		if (pid <= 0) {
    			world.onMessage(IMessage.MSG_TYPE_LOG, CmdChattingRES.normalPlayerOnly, null);
    		} else {
    			if (id > 0) {
    				ChatRmFld cf = new ChatRmFld(world.gameForm, id);
    				world.gameForm.chatDlg.add(cf);
        			cf.setSize(500, 309);
        			cf.setLocation(300 + 20 * world.gameForm.chatDlg.size(), 200 - 20 * world.gameForm.chatDlg.size());
        			SwingUtilities.invokeLater(cf);
    			}
    		}
    	}
    }
    private void execBrocasting(GmStub world, String msg) {
    	if (!world.auth.hasAuth(AuthConstants.brocast)) {
			world.onMessage(IMessage.MSG_TYPE_LOG, CmdChattingRES.authFail, null);
			return;
		}
        sendMsg(world, 0, world.config.getStringProperty("account"), TYPE_WORLD, -1, msg);
    }
    private void execPrivateConvesation(GmStub world, String playerIdStr, String msg) {
    	if (!world.auth.hasAuth(AuthConstants.chat)) {
			world.onMessage(IMessage.MSG_TYPE_LOG, CmdChattingRES.chatFail, null);
			return;
		}
        int tid = Integer.parseInt(playerIdStr);
        sendMsg(world, 0, world.config.getStringProperty("account"), TYPE_PRIVATE, tid, msg);
    }
    private void execAreaBroadcast(GmStub world, String msg) {
    	if (!world.auth.hasAuth(AuthConstants.brocast)) {
			world.onMessage(IMessage.MSG_TYPE_LOG, CmdChattingRES.authFail, null);
			return;
		}
        if (overhearIds[IMessage.MSG_TYPE_LOCAL] <= 0) {
			world.onMessage(IMessage.MSG_TYPE_LOG, CmdChattingRES.noArea, null);
			return;
    	}
        sendMsg(world, 0, world.config.getStringProperty("account"), TYPE_SCENE, overhearIds[ IMessage.MSG_TYPE_LOCAL], msg);
    }
    private void execSystemBroadcast(GmStub world, String msg) {
    	if (!world.auth.hasAuth(AuthConstants.brocast)) {
			world.onMessage(IMessage.MSG_TYPE_LOG, CmdChattingRES.authFail, null);
			return;
		}
    	SendChatMsgPkg pkg = new SendChatMsgPkg();
    	pkg.channel = TYPE_SYS;
        pkg.destId = -1;
        pkg.message = msg;
        world.sndRequest(pkg);
    }
    private void execTeamBroadcast(GmStub world, String msg) {
    	if (!world.auth.hasAuth(AuthConstants.overhear) || !world.auth.hasAuth(AuthConstants.brocast)) {
			world.onMessage(IMessage.MSG_TYPE_LOG, CmdChattingRES.teamAuthFail, null);
			return;
		}
    	if (overhearIds[IMessage.MSG_TYPE_TEAM] <= 0) {
			world.onMessage(IMessage.MSG_TYPE_LOG, CmdChattingRES.noteam, null);
			return;
    	}
        sendMsg(world, 0, world.config.getStringProperty("account"), TYPE_TEAM , overhearIds[IMessage.MSG_TYPE_TEAM], msg);
    }
    private void extcBroadcast(GmStub world, String msg) {
    	if (!world.auth.hasAuth(AuthConstants.overhear) || !world.auth.hasAuth(AuthConstants.brocast)) {
			world.onMessage(IMessage.MSG_TYPE_LOG, CmdChattingRES.gangAuthFail, null);
			return;
		}
    	if (overhearIds[IMessage.MSG_TYPE_GANG] <= 0) {
			world.onMessage(IMessage.MSG_TYPE_LOG, CmdChattingRES.noGang, null);
			return;
    	}
        sendMsg(world, 0, world.config.getStringProperty("account"), TYPE_GUILD,  overhearIds[TYPE_GUILD], msg);
    }
    private void overHear(GmStub world, int channel, int id, String param) {
    	if (channel >= 0 && channel < channelCode.length()) {
    		if (channel >= 3) {
    			if (!world.auth.hasAuth(AuthConstants.overhear)) {
    				world.onMessage(IMessage.MSG_TYPE_LOG, CmdChattingRES.overhearAuthFail, null);
    				return;
    			}
    		}
    		overhearIds[channel] = id; // 记录本地侦听频道
    		JoinChatChannelPkg pkg = new JoinChatChannelPkg();
    		pkg.channel = channel;
    		pkg.targetId = id;
    		pkg.hometown = param; 
    		world.sndRequest(pkg);
    		if (channel == TYPE_SCENE) { // 地区
    			world.gameForm.setCurrentMap(id);
    		}
    	}
    }
    public long getAuth() {
    	return AuthConstants.chat;
    }
    public boolean exec(String cmd, AbstractClient aworld, String []s) throws Exception {
    	GmStub world = (GmStub)aworld;
        if (s != null && s.length >= 1) {
            if (cmd != null) {
                if (isCommand(aworld.auth, cmd)) {
                    if (s.length == 3 && "dialog".equals(s[1])) {
                    	execDialog(world, s[2]);
                    	return true;
                    } else if (s.length > 2 && "b".equals(s[1])) { // 世界频道
                    	 StringBuffer buf = new StringBuffer(s[2]);
                         for (int i = 3; i < s.length; i++) {
                             buf.append(" ");
                             buf.append(s[i]);
                         }
                         execBrocasting(world, buf.toString());
                        return true;
                    } else if (s.length > 3 && "t".equals(s[1])) { // 私聊
                        StringBuffer buf = new StringBuffer(s[3]);
                        for (int i = 4; i < s.length; i++) {
                            buf.append(" ");
                            buf.append(s[i]);
                        }
                    	execPrivateConvesation(world, s[2], buf.toString());
                        return true;
                    } else if (s.length > 3 && "c".equals(s[1])) { // 地区频道
                    	StringBuffer buf = new StringBuffer(s[3]);
                        for (int i = 4; i < s.length; i++) {
                            buf.append(" ");
                            buf.append(s[i]);
                        }
                        execAreaBroadcast(world, buf.toString());
                    	return true;
                    } else if (s.length > 2 && "s".equals(s[1])) { // 系统广播
                        StringBuffer buf = new StringBuffer(s[2]);
                        for (int i = 3; i < s.length; i++) {
                            buf.append(" ");
                            buf.append(s[i]);
                        }
                        execSystemBroadcast(world, buf.toString());
                        return true;
                    } else if (s.length > 2 && "team".equals(s[1])) { // 队伍广播
                    	 StringBuffer buf = new StringBuffer(s[2]);
                         for (int i = 3; i < s.length; i++) {
                             buf.append(" ");
                             buf.append(s[i]);
                         }
                         execTeamBroadcast(world, buf.toString());
                        return true;
                    } else if (s.length > 2 && "gang".equals(s[1])) { // 帮派广播
                    	 StringBuffer buf = new StringBuffer(s[2]);
                         for (int i = 3; i < s.length; i++) {
                             buf.append(" ");
                             buf.append(s[i]);
                         }
                         execTeamBroadcast(world, buf.toString());
                        return true;
                    } else if ((s.length == 3 || s.length == 4) && "ln".equals(s[1])) {
                    	int id = Integer.parseInt(s[2]); // player id
                    	sango.data.POnlinePlayer u = world.gameForm.users.getUser(id);
                    	if (u == null) {
                    		world.onMessage(IMessage.MSG_TYPE_LOG, CmdChattingRES.playerNotInList, null);
                    	} else {
                        	overHear(world, TYPE_SCENE, u.stageId, null);
                    	}
                    	return true;
                    } else if ((s.length == 3 || s.length == 4) && "l".equals(s[1])) {
                    	int channel = TYPE_PRIVATE;
                    	int id = 0;
                    	if (s.length == 3) {
                    		try {
                				id = Integer.parseInt(s[2]);
                			} catch (Exception e) {
                				channel = TYPE_HOMETOWN; // 同乡
                			}
                    	} else {
                    		channel = channelCode.indexOf(s[2].charAt(0));
                    		id = Integer.parseInt(s[3]);
                    	}
                    	overHear(world, channel, id, s[2]);
                    	return true;
                    } else {
                        world.onMessage(IMessage.MSG_TYPE_LOG, CmdChattingRES.wrongPara + getDescription(world.auth), null);
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public void sendMsg(GmStub wd, int id, String name, int channel, int target, String msg) {
        try {
            SendChatMsgPkg pkg = new SendChatMsgPkg();
            pkg.destId = target;
            pkg.channel = channel;
            pkg.message = msg;
            if (channel ==TYPE_WORLD) {
                wd.onMessage(IMessage.MSG_TYPE_GM, StringUtil.formal(Res.format(CmdChattingRES.broadcastMsg, name, msg)), null);
            } else if (channel ==  TYPE_PRIVATE) {
            	sango.data.POnlinePlayer u = wd.gameForm.users.getUser(target);
                wd.onMessage(IMessage.MSG_TYPE_GM, StringUtil.formal(
                		Res.format(CmdChattingRES.chatMsg, name, u == null ?  String.valueOf(target) : u.name, msg)), new int[]{channel});
                wd.gameForm.cmdFld.getTarUser(u == null ? new sango.data.POnlinePlayer(channel)  : u);
            }
            wd.sndRequest(pkg);
            GmChatTrace.traceGm(wd.config.account, wd.getUniqServerId(), channel ==  TYPE_PRIVATE ? GmChatTrace.MODE_CHAT : GmChatTrace.MODE_BROADCAST, channel, msg, wd.config.title + ":");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public String getCommand(Auth auth) {
    	if (auth.hasAuth(getAuth())) {
    		return "m";
    	} 
    	return null;
    }
    public String getName(Auth auth) {
    	if (auth.hasAuth(getAuth())) {
    		return CmdChattingRES.cmdName;
    	}
    	return null;
    }
    public String getDescription(Auth auth) {
    	if (auth.hasAuth(getAuth())) {
			StringBuilder sb = new StringBuilder(CmdChattingRES.cmdDesc);
			if (auth.hasAuth(AuthConstants.overhear)) {
				sb.append(CmdChattingRES.overhearCmdDEsc);
			}
			return sb.toString();
    	} 
    	return null;
    }

    ////// 独用类
	/**
	 * 发送信息
	 * channel									int
	 * targetId									int
	 * nativeString								string
	 * message									string
	 */
    public static class SendChatMsgPkg extends UAData implements GmConstants {
        public int getAppDataType() {
            return ADMIN_CHAT_CLIENT;
        }
        public int channel;
        public int destId;
        public String nativeString;
        public String message;
        public int serialId;
        public int getProtocolSerialNumber() {
        	return serialId;
    	}
    	public void setProtocolSerialNumber(int ser) {
    		super.setProtocolSerialNumber(ser);
    		serialId = ser;
    	}
        public String[] getProperties() {
            return new String[] {"channel", "destId",  "nativeString", "message"}; //, "serialId"};
        }
    }
    
    /**
	 * 聊天信息
	 * channel						byte
	 * sourceId						int 
	 * name							string 
	 * message						string
	 * attachment					byte[] 如果是物品{01(byte),itemId,instanceId(int),name(string),showType(byte),quality(byte)},如果是任务{02{byte},questId(int,name(string)}
	 * 
	 */
	public static class ClinetChatPkg extends UAData implements GmConstants {
        public int getAppDataType() {
            return CHAT_SERVER;
        }
        public byte saidType;
        public int fromId;
        public byte faction;
        public String fromName;
        public String msg;
        public byte[] attachment;
        public String channelName;

        private String props1[] = {"saidType", "fromId"};
        private String props2[] = {"fromName", "msg", "attachment", "?channelName"};
        
        public void read_01(ByteBuffer data) throws Exception {
    		read(data, props1);
    		if (saidType == 0) { // peony.game.ChatOption.WORLD
    			readField(data, "faction");
    		}
    		read(data, props2);
    	}
        
        /**
         * 大于7月26日版本添加以下改动
         */
        public void read(ByteBuffer data) throws Exception {
        	if(BaseConfig.CVS_BRANCH > BaseConfig.FixVersion_2011_10_25){
        		read_02(data);
        	}else{
        		read_01(data);
        	}
    	}
        
        public boolean isKing;
        public boolean isOfficer;
        public byte tongLevel;
        public String destName;
        public int destId;
        public String dutyName;
        
        public static final int WORLD = 0;
    	public static final int FACTION = 1;
    	public static final int AREA = 2;
    	public static final int NATIVE = 3;
    	public static final int GUILD = 4;
    	public static final int PARTY = 5;
    	public static final int PRIVATE = 6;
    	public static final int SYSTEM = 7;
    	
    	public static final int WORLD_SHOUT = 10;
    	public static final int FACTION_SHOUT = 11;
    	public static final int AREA_SHOUT = 12;
    	public static final int NATIVE_SHOUT = 13;
    	public static final int GUILD_SHOUT = 14;
    	public static final int PARTY_SHOUT = 15;
    	public static final int PRIVATE_SHOUT = 16;
    	
        private String props_head[] = {"saidType", "fromId"};
        private String props_body[];
        
        public void read_02(ByteBuffer data) throws Exception {
    		read(data, props_head);
    		isKing = (((saidType >> 7) & 0x1) == 0x1);
    		isOfficer = (((saidType >> 6) & 0x1) == 0x1);
    		saidType &= 0x3F;
    		
    		List<String> bodyList = new ArrayList<String>();
    		bodyList.add("tongLevel");
    		if(saidType == WORLD){
    			bodyList.add("faction");
			} else if(saidType == PRIVATE){
				bodyList.add("destName");
				bodyList.add("destId");
			}  else if(saidType == GUILD && (isOfficer | isKing)){
				bodyList.add("dutyName");
			}
    		bodyList.add("fromName");
    		bodyList.add("msg");
    		bodyList.add("attachment");
    		bodyList.add("?channelName");
    		
    		props_body = new String[bodyList.size()];
    		bodyList.toArray(props_body);

    		read(data, props_body);
    	}
    }
  	public static class SendChatMsgRetPkg extends UAData implements GmConstants {
        public int getAppDataType() {
            return ADMIN_CHAT_SERVER;
        }
  		 public int serialId;
         public int getProtocolSerialNumber() {
         	return serialId;
     	}
     	public void setProtocolSerialNumber(int ser) {
     		super.setProtocolSerialNumber(ser);
     		serialId = ser;
     	}
         public String[] getProperties() {
             return new String[] {"serialId"};
         }
  	}

	/**
	 * 进入聊天频道
	 * serial									int
	 * channel									int (0 世界 1 国家 2 地区 3 家乡 4 帮派 7 系统)
	 * targetId									int (如果是国家:1 魏 2 蜀 3 吴;如果是地区:mapId;如果是帮派:帮派id)
	 * 家乡名称									string
	 */
	
	public class JoinChatChannelPkg  extends UAData implements GmConstants {
	    public int getAppDataType() {
	        return ADMIN_JOIN_CHATCHANNEL_CLIENT;
	    }

	    public int serial;
	    public int channel;
	    public int targetId;
	    public String hometown;
	    public String[] getProperties() {
	        return new String[] {"serial", "channel", "targetId", "hometown"};
	    }
	}
	public class Processor implements PDProcessor {
	    public boolean process(pip.gm.fw.AbstractClient master, UWapData data) {
	      if (data instanceof SendChatMsgPkg) {
	    	  SendChatMsgPkg d = (SendChatMsgPkg)data;
	        	master.onMessage(IMessage.MSG_TYPE_LOG, Res.format(CmdChattingRES.onGotGmMsg, d.message), null);
	        } else if (data instanceof ClinetChatPkg) {
	        	onGotClientMessage((GmStub)master, (ClinetChatPkg)data);
	        } else {
	            return false;
	        }
	        return true;
	    }
	}
}
