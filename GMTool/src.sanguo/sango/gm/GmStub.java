package sango.gm;

/**
 * @todo
 * 。 GM说暴露GM名称问题。
 *  搜寻特定玩家。（界面操作）
 */
import java.awt.Window;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import pip.gm.cmd.CmdPassword;
import pip.gm.cmd.CmdSchedule;
import pip.gm.cmd.CmdScript;
import pip.gm.cmd.GeneralFunction;
import pip.gm.fw.Auth;
import pip.gm.fw.AuthConstants;
import pip.gm.fw.BaseConfig;
import pip.gm.fw.GameInfo;
import pip.gm.fw.GmChatTrace;
import pip.gm.fw.IMessage;
import pip.gm.fw.IdleTask;
import pip.gm.fw.TextProcesser;
import pip.io.uwap.UWapData;
import pip.util.Res;
import pip.util.StringUtil;
import sango.GmConstants;
import sango.gm.cmd.AccountBindingInfo;
import sango.gm.cmd.CmdAccountInfoReceice;
import sango.gm.cmd.CmdChatting;
import sango.gm.cmd.CmdConfig;
import sango.gm.cmd.CmdGmHelp;
import sango.gm.cmd.CmdLogin;
import sango.gm.cmd.CmdWho;
import sango.gm.ui.GameForm;
public class GmStub extends pip.gm.fw.AbstractClient implements TextProcesser, 
	pip.util.ui.RichConsole.ParamMenuItemBuilder {
	public GameForm gameForm;
	public BaseConfig config;
	public SangoGameInfo gameInfo = new SangoGameInfo(); 
	/** 当系统空闲时可以执行的命令。心跳已经处理，这里有可能是随时同步在线玩家功能 */
	public HashSet<IdleTask> idleTasks = new HashSet<IdleTask>();
	private pip.io.uwap.UAData bpPkg = new BEEP();


	public GmStub(GameForm wd, BaseConfig cfg) {
		auth = new Auth(AuthConstants.authStrings); // GM 的权限
		gameForm = wd;
		config = cfg;
		String s = config.getStringProperty("sharpId");
		if (s != null) {
			if (s.toLowerCase().startsWith("0x")) {
				sharpId = Long.parseLong(s.substring(2), 16);
			} else {
				sharpId = Long.parseLong(s);
			}
		}
		loadPlayerActions(config.getSet("playerActions"));
		gameInfo.loadScenes(config.getSet("map"));
		gameInfo.loadItems(config.getStringProperty("dataDir"));
		
		//////////////
		addProcessor(new GeneralFunction.GeneralProcessor());
		loadConfigFunction();

		addFunction(new CmdSchedule());
		addFunction(new CmdScript());

		addFunction(new CmdConfig());
		addFunction(new CmdLogin());
		addFunction(new CmdWho());
		addFunction(new CmdChatting());
		addFunction(new sango.gm.cmd.CmdPlayerInfo());
		addFunction(new sango.gm.cmd.CmdMute());
		addFunction(new sango.gm.cmd.CmdKick());
		addFunction(new sango.gm.cmd.CmdSaveClientBBS());
		addFunction(new CmdGmHelp());
		addFunction(new CmdPassword());
		addFunction(new AccountBindingInfo());
		addFunction(new CmdAccountInfoReceice());
		addFunction(new GmChatTrace());  // special
		
		this.host = config.host;
		this.port = config.port;
	}
	
	public GameInfo getGameInfo() {
		return gameInfo;
	}

	public Window getUiContainer() {
		return gameForm.main;
	}

	public pip.gm.fw.BaseConfig getConfig() {
		return config;
	}
	// TextProcessor 部分
	public void processText(String type, String s) {
		if (type.equals("系统")) {
			con.processCommand(new String[]{"m", "s", s});
		} else if (type.equals("GM")) {
			if (gameForm.currentMap != null && gameForm.currentMap.id > 0) {
				con.processCommand(new String[]{"m", "b", String.valueOf(gameForm.currentMap.id), s});
			}
		} else if (type.equals("GM")) {
			con.processCommand(new String[]{"m", "b", s});
		} else if (type.startsWith("私:")) {
			int k = type.indexOf('[');
			type = type.substring(2, k);
			con.processCommand(new String[]{"m", "t", type, s});
		}
	}
	public String[] getTypes() {
		ArrayList<String> types = new ArrayList<String>();
		types.add("系统");
		types.add("GM");
		if (auth.hasAuth(AuthConstants.overhear)) {
			types.add("区");
		}
		String ret[] = new String[types.size()];
		types.toArray(ret);
		return ret;
	}

	public void setAuth(long s) {
		if (s != 0) {
			auth.setAuth(s);
		} else {
			auth.setAuth(0);
		}
		gameForm.main.updateServeListState();
		gameForm.cmdFld.resetConfig();
	}
	
	public void beatBeep() {
		try {
			boolean sent = false;
			
			for (IdleTask idleTask : idleTasks) {
				sent |= idleTask.processIdleTask(this);
			}
			if (!sent) {
				sndRequest(bpPkg);	
			}
		} catch (Exception e) {
			e.printStackTrace();
			gameForm.onMessage(IMessage.MSG_TYPE_LOG, Res.format(GmStubRES.broken, e.getMessage()), null);
		}
	}
	public void sessionClosed(int sessionId) {
		try {
			UWapData pkg = loginPackage;
			close();
			if (pkg != null) {
				sndRequest(pkg);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public void exceptionCaught(int sessionId, Throwable e) {
		e.printStackTrace();
		try {
			close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public void onMessage(int type, String msg, int param[]) {
		gameForm.onMessage(type, msg, param);
	}

    //// ParamMenuItemBuilder 实现部分
    public String genCommand(String param[]) {
    	if (param != null && param.length > 1 && param[1] != null) {
    		return "goto player " + param[0];
    	}
    	return null;
    }
    public ArrayList<String[]> genMenu(String para[]) {
    	ArrayList<String[]> ret = new ArrayList<String[]>();
    	for (String tmps[][] : actionTmps) {
    		String title = genTemplateString(tmps[0], para);
    		String action = genTemplateString(tmps[1], para);
    		if (title != null && action != null) {
    			ret.add(new String[]{title, action});
    		}
		}
    	return ret;	
	}
    private String genTemplateString(String []tmp, String para[]) {
    	try {
	    	for (String template : tmp) {
	    		int n = template.length();
	    		int k = 0;
	    		while (true) {
	    			int k2 = template.indexOf('{', k);
	    			if (k2 == -1) {
	    				if (template.trim().length() > 0) {
	    					return template;
	    				} else {
	    					break;
	    				}
	    			}
	    			int k3 = template.indexOf('}', k2);
	    			if (k3 == -1) {
	    				break;
	    			}
	    			int idx = Integer.parseInt(template.substring(k2+1, k3));
	    			if (idx < 0 || idx >= para.length || para[idx] == null) {
	    				break;
	    			}
	    			k = k2 + para[idx].length();
	    			template = template.substring(0, k2) + para[idx] + template.substring(k3+1);
	    		}
	    	}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return null;
    }
    ArrayList<String[][]> actionTmps = new ArrayList<String[][]>();
    public void loadPlayerActions(ArrayList<String> lst) {
    	if (lst != null) {
	    	for (String s: lst) {
	    		String [][]data = new String[2][];
	    		ArrayList<String> tmp = new ArrayList<String>(); 
	    		int k = s.indexOf('|');
	    		String s1 = s.substring(0, k);
	    		int k1 = 0;
	    		while (true) {
	    			int k2 = s1.indexOf("{#}", k1);
	    			if (k2 >= 0) {
	    				tmp.add(s1.substring(k1, k2));
	    			} else {
	    				tmp.add(s1.substring(k1));
	    				break;
	    			}
	    			k1 = k2 + 3;
	    		}
	    		data[0] = new String[tmp.size()];
	    		tmp.toArray(data[0]);
	    		for (int i = 0; i < data[0].length; i++) {
	    			String ts = data[0][i];
	    			ts = StringUtil.replace(ts, "{playerName}", "{1}");
	    			ts = StringUtil.replace(ts, "{name}", "{1}");
	    			ts = StringUtil.replace(ts, "{this}", "{1}");
	    			ts = StringUtil.replace(ts, "{id}", "{0}");
	    			data[0][i] = ts;
	    		}
	    		tmp.clear();
	    		s1 = s.substring(k + 1);
	    		k1 = 0;
	    		while (true) {
	    			int k2 = s1.indexOf("{#}", k1);
	    			if (k2 >= 0) {
	    				tmp.add(s1.substring(k1, k2));
	    			} else {
	    				tmp.add(s1.substring(k1));
	    				break;
	    			}
	    			k1 = k2 + 3;
	    		}
	    		data[1] = new String[tmp.size()];
	    		tmp.toArray(data[1]);
	    		for (int i = 0; i < data[1].length; i++) {
	    			String ts = data[1][i];
	    			ts = StringUtil.replace(ts, "{playerName}", "{1}");
	    			ts = StringUtil.replace(ts, "{name}", "{1}");
	    			ts = StringUtil.replace(ts, "{this}", "{1}");
	    			ts = StringUtil.replace(ts, "{id}", "{0}");
	    			data[1][i] = ts;
	    		}
	    		if (data[0].length > 0 && data[1].length > 0) {
	    			actionTmps.add(data);
	    		}
	    	}
    	}
    }
	public void close() throws Exception {
		super.close();
		loginPackage = null;
		if (gameForm != null) {
			gameForm.main.updateServeListState();
		}
	}
   
    public static class Scene {
    	public String name;
    	public int id;
    	public boolean blurMap;
    	public int level;
    	public int reviveMap;
    	public int reviveX;
    	public int reviveY;
    	public String toString() {
    		return   name + "[" + (id>>4) + "-" + id + "]";
    	}
    }
	public class SangoGameInfo extends GameInfo {
		/** 根据ID查找场景。这些场景信息是通过配置文件加载的。 */
	    public Scene getScene(int id) {
	    	Integer key = Integer.valueOf(id);
	    	Scene scene = maps.get(key);
	    	if (scene == null) {
	    		scene = new Scene();
	    		scene.id = id;
	    		scene.name = GmStubRES.unknown;
	    	}
	    	return scene;
	    }

		/** 场景数据。从配置文件中获得 */
	    public HashMap<Integer,Scene> maps = new HashMap<Integer,Scene>();
		public String getSceneName(int sceneId) {
			Scene scene = getScene(sceneId);
			if (scene != null) {
				return scene.name;
			}
			return GmStubRES.unknown;
		}
		public boolean isBlurMap(int sceneId) {
			Scene scene = getScene(sceneId);
			if (scene != null) {
				return scene.blurMap;
			}
			return false;
		}
		public String getItemName(int itemId) {
			return GmStubRES.unknown;
		}
		/** 读取配置文件中的地图信息,最后一个将作为当前地图 */
		public void loadScenes(ArrayList<String> lst) {
			Scene scene = null;
			for (String s : lst) {
				String ss[] = s.split(",");
				scene = new Scene();
				if (ss.length > 0)  scene.id = Integer.parseInt(ss[0]);
				if (ss.length > 1)  scene.name = ss[1];
				if (ss.length > 2)  scene.blurMap = !ss[2].equals("1");
				if (ss.length > 3)  scene.level = Integer.parseInt(ss[3]);
				if (ss.length > 6) {
					scene.reviveMap = Integer.parseInt(ss[4]);
					scene.reviveX = Integer.parseInt(ss[5]);
					scene.reviveY = Integer.parseInt(ss[6]);
				}
				maps.put(Integer.valueOf(scene.id), scene);
			}
		}
		private void loadItems(String dir) {
			File fDir;
			if (dir == null || dir.length() == 0) {
				dir = config.game;
			}
			if (dir.indexOf(':') < 0 && !dir.startsWith("/")) { // 未指定绝对目录
				fDir = new File(System.getProperty("user.dir"));
				fDir = new File(fDir, dir);
			} else {
				fDir = new File(dir);
			}
			if (fDir.exists()) {
				/** @TODO 加载物品列表 */
			}
			// 没有数据文件不提示，
		}
	}

	public static class BEEP extends pip.io.uwap.UAData {
		 public int getAppDataType() {
		        return GmConstants.ADMIN_ERROR;
		    }
		 	public int serial;
		 	public short errorCode = GmConstants.ADMIN_ERROR;
		 	public String msg = GmStubRES.beep;
		    public String[] getProperties() {
		    	return new String[]{"serial", "errorCode", "msg"};
		   }
	}
}
