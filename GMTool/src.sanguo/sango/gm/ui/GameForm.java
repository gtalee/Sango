package sango.gm.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

import pip.gm.MainFrame;
import pip.gm.fw.AbstractClient;
import pip.gm.fw.AbstractGmForm;
import pip.gm.fw.Broadcastable;
import pip.gm.fw.DoubleClickZoomerAdapter;
import pip.gm.fw.GmChatTrace;
import pip.gm.fw.IMessage;
import pip.gm.fw.ReceiptListener;
import pip.util.Res;
import pip.util.UiUtil;
import pip.util.ui.CommandField;
import pip.util.ui.LayoutUtil;
import pip.util.ui.RichConsole;
import pip.util.ui.RichConsole.Message;
import cwu.util.sort.CompareAgent;
import cwu.util.sort.SortAgent;
import pip.gm.fw.BaseConfig;
import pip.io.uwap.UAData;
import pip.io.uwap.UWapData;
import sango.GmConstants;
import sango.gm.GmStub;
import sango.gm.GmStub.Scene;
import pip.gm.fw.Receiptable;
public class GameForm extends AbstractGmForm implements Broadcastable {
	LayoutUtil lu = new LayoutUtil();
	/** 连接句柄，管理所有网络请求 */
	public GmStub gmStub; 
	/** 命令输入空间 */
    public CommandField cmdFld;
    /** 支持的不同工作台名称 */
    public String[] consoleTitles = {GameFormRES.log, GameFormRES.system, GameFormRES.misc, GameFormRES.others};
    /** GM求助工作表 */
    public SOSTable sos;
    /** 当前在线所有玩家列表 */
    public UserTable users;
    DoubleClickZoomerAdapter zoomAdapterForListPane;

    public AbstractClient getUwapApp() {
    	return gmStub;
    }
    JComboBox cbbMap;
    public Scene currentMap;

    JDialog loginDialog;
	JTextField nameFld = new JTextField(12);
	JPasswordField passFld = new JPasswordField(12);

	JDialog sceneMenu;
	public String getCurrentInputBuf() {
		return cmdFld.getText();
	}
    public void setCurrentMap(int id) {
    	currentMap = gmStub.gameInfo.getScene(id);
    	cbbMap.setSelectedItem(currentMap);
    }
    public ArrayList<String[]> getActions() {
    	ArrayList<String[]> ret = new ArrayList<String[]>();
    	ret.add(new String[]{GameFormRES.whoTitle, "who", GameFormRES.whoTip});
    	ret.add(new String[]{GameFormRES.list, "gmsg list", GameFormRES.listTip});
    	ret.add(new String[]{GameFormRES.recTitle, "who rec", GameFormRES.recTip});
    	return ret;
    }

	public ArrayList<Message> getExistingMessages() {
		ArrayList<Message> lst = new ArrayList<Message>();
		for (int i = consoles.length; i-- > 1; ) {
			lst.addAll(consoles[i].getMessages());
		}
		return lst;
	}
	
	// TodoProcessor implementation
	public void processTodo(int id, String parameters) {
		gmStub.con.processCommand(parameters);
	}
	public String getSourceName() {
		return gmStub.config.title;
	}
	
	public static ImageIcon openIcon[] = new ImageIcon[2];
	static {
		openIcon[0]   =  UiUtil.getIcon("/sango0.png");
		openIcon[1]   =  UiUtil.getIcon("/sango1.png");
	}
	public ImageIcon getIcon() {
		if (gmStub.conn == null) {
			return openIcon[0];
		}
		return openIcon[1];
	}


	
	public String getServerState() {
		if (users != null) {
			return "(" + users.getNumUsersOnline() + ")";
		}
		return "(000)";
	}
    public void init(MainFrame fm, String xml) {
	  try {
	      main = fm;
	      initConsoles();
	      File configFile = new File(xml);
	      onMessage(IMessage.MSG_TYPE_LOG, Res.format(GameFormRES.loadingConfig, configFile.getAbsolutePath()), null);
	      BaseConfig config = new BaseConfig();
	      try {
	    	  config.init(configFile.getAbsolutePath());
	      } catch (Exception ex) {
	    	  ex.printStackTrace();
	      }
	      gmStub = new GmStub(this, config);
	      cmdFld = new CommandField(this, gmStub);
	      users = new UserTable(gmStub);
	      users.setParamMenuBuilder(gmStub);
	      int n = gmStub.gameInfo.maps.size();
	      Scene[] maps = new Scene[n+1];
	      if (n > 0) {
	    	  System.arraycopy(gmStub.gameInfo.maps, 0, maps, 0, n);
	      }
	      maps[n] = new Scene();
	      maps[n].id = -1;
	      maps[n].level = -1;
	      maps[n].name = "－";
	      
	      SortAgent.sort(maps, new  CompareAgent(){
	    	  public int compare(Object a, Object b) {
	    		  if (a == null || b == null) {
	    			  return 0;
	    		  }
	    		  int k = ((Scene)a).level - ((Scene)b).level;
	    		  if (k == 0) {
	    		  	k = ((Scene)a).id - ((Scene)b).id;
	    		  }
	    		  return k;
	    	  }
	      }, 0);
	      cbbMap = new JComboBox(maps);
	      cbbMap.setSelectedItem(null);
	      sceneMenu = new JDialog(main, GameFormRES.sceneSelect);
	      JPanel p = new JPanel(new GridBagLayout());
	      int k = 0;
	      for (Scene m : maps) {
	    	  JButton mi = new JButton(m.name);
	    	  mi.setName("m l 区 " + m.id);
	    	  mi.addActionListener(sceneListener);
	    	  p.add(mi, lu.getConstrains(0, k, 1, 1));
	    	  p.add(new JLabel(Res.format(GameFormRES.sceneLevelInfo, (m.id>>4) + "." + m.id, m.level)), lu.getConstrains(1, k, 1, 1));
	    	  k++;
	      }
	      sceneMenu.add(BorderLayout.CENTER, new JScrollPane(p));
	      sceneMenu.setSize(300, 400);
	      for (String s : config.getSet("cmd-his")) {
	    	  cmdFld.appendHistory(s);
	      }
	      setLayout(new BorderLayout());
	      add(BorderLayout.SOUTH, cmdFld);
	      add(BorderLayout.CENTER, getCenterPanel()); 
	      for (RichConsole c : consoles) {
	    	  c.setParamMenuBuilder(gmStub);
	    	  c.addConsoleActionListener(gmStub.con);
	      }
	      add(BorderLayout.NORTH, getMenuPanel()); // 菜单条要最后初始化,因为其中要用到 zoomAdapterForListPane.
	      // 初始化配置
	  } catch (Exception exception) {
	      exception.printStackTrace();
	  }
    }
    public void postInitial() {
    	gmStub.config.account = GmChatTrace.loginedUser;
		gmStub.config.password = GmChatTrace.loginedUserPass;
		gmStub.con.processCommand("gmlogin " + GmChatTrace.loginedUser + " " + GmChatTrace.loginedUserPass);
		
//    	SwingUtilities.invokeLater(new Runnable() {
//    		public void run() {
//    	    	 popLoginDialog();	
//    		}
//    	});
    }
    
    private JComponent getMenuPanel() {
    	JPanel jp = new JPanel();
    	jp.setLayout(new BorderLayout());
    	JMenuBar jmb = new JMenuBar();
    	JMenu menu = new JMenu(GameFormRES.system);
    	JMenuItem mi;
    	
    	mi = new JMenuItem(GameFormRES.login);
    	mi.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			postInitial();
    		}
    	});
    	menu.add(mi);
    	menu.addSeparator();
    	
    	mi = new JMenuItem(GameFormRES.close);
    	ActionListener closeListener = new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			SwingUtilities.invokeLater(new Runnable() {
    				public void run() {
    					try {
    						gmStub.quit();
    					} catch (Exception e) {
    						e.printStackTrace();
    					}
    			    	main.unregistServer(GameForm.this);
    				}
    			});
    		}
    	}; 
    	mi.addActionListener(closeListener);
    	menu.add(mi);
    	jmb.add(menu);
    	
    	menu = new JMenu(GameFormRES.set);
      	JCheckBox scrollScreen = new JCheckBox(GameFormRES.scrool, true);
      	scrollScreen.addActionListener(new ActionListener() {
      	    public void actionPerformed(ActionEvent e) {
      	    	JCheckBox c = (JCheckBox)e.getSource();
      	    	boolean b = c.isSelected();
      	    	for (RichConsole rc : consoles) {
      	    		rc.scrollingMode = b;
      	    	}
      	    }
      	});
      	menu.add(scrollScreen);
      	
      	menu.addSeparator();
      
//    	JCheckBox check = new JCheckBox("刷新求助");
//    	check.setToolTipText("设定系统是否自动定时取求助信息。");
//    	check.addActionListener(new ActionListener() {
//    		public void actionPerformed(ActionEvent e) {
//    			JCheckBox check = (JCheckBox)e.getSource();
//    			boolean b = check.isSelected();
//    			if (b) {
//    				
//    				gmStub.config.setConfig("autoRetrieveGmMail", "t", true);
//    				gmStub.con.processCommand(new String[]{"gmsg", "get"});
//    			} else {
//    				gmStub.config.setConfig("autoRetrieveGmMail", "t", true);
//    			}
//    		}
//    	});
//    	menu.add(check);
    	JCheckBox check = new JCheckBox(GameFormRES.disDone);
    	check.setToolTipText(GameFormRES.disDoneTip);
    	check.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			JCheckBox check = (JCheckBox)e.getSource();
    			gmStub.config.setConfig("showProcessedGmsg", check.isSelected() ? "1" : "0", true); 
    			sos.updateMode();
    		}
    	});
    	menu.add(check);

      	jmb.add(menu);

      	menu = new JMenu(GameFormRES.command);
      	mi = new JMenuItem(GameFormRES.syncPlayer);
      	mi.setToolTipText(GameFormRES.syncPlayerTip);
      	mi.setName("who");
      	mi.addActionListener(commandListener);
      	menu.add(mi);

      	mi = new JMenuItem(GameFormRES.numPlayer);
      	mi.setToolTipText(GameFormRES.numPlayerTip);
      	mi.setName("who get");
      	mi.addActionListener(commandListener);
      	menu.add(mi);
      	
      	mi = new JMenuItem(GameFormRES.findPlayer);
      	mi.setToolTipText(GameFormRES.findPlayerTip);
      	mi.setName("who search");
      	mi.addActionListener(commandListener);
      	menu.add(mi);
      	
      	mi = new JMenuItem(GameFormRES.recRef);
      	mi.setToolTipText(GameFormRES.recRefTip);
      	mi.setName("who rec");
      	mi.addActionListener(commandListener);
      	menu.add(mi);
      	
      	mi = new JMenuItem(GameFormRES.idle);
      	mi.setToolTipText(GameFormRES.idleTip);
      	mi.setName("who idle");
      	mi.addActionListener(commandListener);
      	menu.add(mi);
      	
      	menu.addSeparator();
      	
      	mi = new JMenuItem(GameFormRES.history);
      	mi.setToolTipText(GameFormRES.historyTip);
      	mi.setName("gmsg get");
      	mi.addActionListener(commandListener);
      	menu.add(mi);

      	mi = new JMenuItem(GameFormRES.save);
      	mi.setToolTipText(GameFormRES.saveTip);
      	mi.setName("gmsg save");
      	mi.addActionListener(commandListener);
      	menu.add(mi);
      	
      	menu.addSeparator();
      	
      	mi = new JMenuItem(GameFormRES.timer);
      	mi.setName("job ui");
      	mi.addActionListener(commandListener);
      	menu.add(mi);
      	
      	jmb.add(menu);

      	ArrayList<String> menus = gmStub.config.getSet("menu");
      	if (menus != null && menus.size() > 0) {
      		for (String ms : menus) {
      			String mms[] = ms.split(",");
      			if (mms.length == 3 || mms.length == 2) {
      				mi = new JMenuItem(mms[0].trim());
      				if (mms.length == 3) {
      					mi.setToolTipText(mms[2].trim());
      				}
      				mi.setName(mms[1].trim());
      				mi.addActionListener(commandListener);
      				jmb.add(mi);
      			} else if (mms.length > 3 && ((mms.length % 3) == 1)) {
      				menu = new JMenu(mms[0].trim());
      				for (int j = 1; j < mms.length; j += 3) {
      					String menuItemTitle = mms[j].trim();
      					if (menuItemTitle.equals("-")) {
      						menu.addSeparator();
      					} else {
	          				mi = new JMenuItem(menuItemTitle);
	          				String menuTip = mms[j + 2].trim();
	          				if (!menuTip.equals("-")) {
	          					mi.setToolTipText(menuTip);
	          				}
	          				mi.setName(mms[j + 1].trim());
	          				mi.addActionListener(commandListener);
	          				menu.add(mi);
      					}
      				}
      				jmb.add(menu);
      			}
      		}
      	}
      	
      	jmb.add(new JLabel("   "));
      	cbbMap.addActionListener(mapChangeListener);
      	jmb.add(cbbMap);
      	
      	jp.add(BorderLayout.WEST, jmb);

      	JPanel jp2 = new JPanel(new FlowLayout());
      	JLabel jl = new JLabel(gmStub.config.title);
      	jl.setForeground(Color.BLUE);
      	jp2.add(jl);
      	jp.add(BorderLayout.CENTER, jp2);
      	
      	JButton btn = new JButton(UiUtil.getIcon("/close.png"));
      	btn.addActionListener(closeListener);
      	jp.add(BorderLayout.EAST, btn);
      	return jp;
    }
    private void popLoginDialog() {
    	if (loginDialog == null) {
    		
    		nameFld.setText(gmStub.config.account);
        	passFld.setText(gmStub.config.password);

    		loginDialog = new JDialog(main, GameFormRES.login + gmStub.config.title, true);
    		loginDialog.setLayout(new BorderLayout(10, 10));
    		// 输入区域
    		JPanel p = new JPanel();
    		p.setLayout(new GridBagLayout());
    		
    		JPanel p1 = new JPanel(new BorderLayout());
    		p1.add(BorderLayout.EAST, new JLabel(GameFormRES.gmAccount));
    		p.add(p1, lu.getConstrains(2, 2, 1, 1));
    		p.add(nameFld, lu.getConstrains(3, 2, 1, 1));
    		
    		p1 = new JPanel(new BorderLayout());
    		p1.add(BorderLayout.EAST, new JLabel(GameFormRES.gmPass));
    		p.add(p1, lu.getConstrains(2, 3, 1, 1));
    		p.add(passFld, lu.getConstrains(3, 3, 1, 1));
    		passFld.setEchoChar('*');
    		passFld.addKeyListener(loginKeyListener);
    		nameFld.addKeyListener(loginKeyListener);
    		loginDialog.add(BorderLayout.CENTER, p);
    		
    		// 控制
    		loginDialog.add(BorderLayout.NORTH, new JLabel(""));
    		loginDialog.add(BorderLayout.EAST, new JLabel("    "));
    		loginDialog.add(BorderLayout.WEST, new JLabel("   "));
    		
    		// 下部 Button 条
    		p = new JPanel();
    		p.setLayout(new FlowLayout());
    		JButton btn = new JButton(GameFormRES.login);
    		btn.addActionListener(new ActionListener() {
    			public void actionPerformed(ActionEvent e) {
    				loginFromUi();
        		}
    		});
    		p.add(btn);

        	btn = new JButton(GameFormRES.cancel);
    		btn.addActionListener(new ActionListener() {
    			public void actionPerformed(ActionEvent e) {
    				loginDialog.setVisible(false);
        		}
    		});
    		p.add(btn);
    		
    		loginDialog.add(BorderLayout.SOUTH, p);
    		loginDialog.pack();
    		loginDialog.setLocation(main.getX() + ((main.getWidth() - loginDialog.getWidth()) >> 1),
    				main.getY() + ((main.getHeight() - loginDialog.getHeight()) >> 2));
    	}
    	loginDialog.setVisible(true);
    }
    private  java.awt.event.KeyListener loginKeyListener = new  java.awt.event.KeyListener() {
    	public void keyTyped(KeyEvent e) {
    	}
        public void keyPressed(KeyEvent e) {
    		if (e.getKeyCode() ==  java.awt.event.KeyEvent.VK_ENTER) {
    			Component com = e.getComponent();
    			if (com instanceof JPasswordField) {
    				loginFromUi();
    			} else {
    				com.transferFocus();
    			}
    		}
        }
        public void keyReleased(KeyEvent e) {
        }
    };
    private void loginFromUi() {
    	String s1 = nameFld.getText();
		String s2 = passFld.getText();
		gmStub.config.account = s1;
		gmStub.config.password = s2;
		gmStub.con.processCommand("gmlogin " + s1 + " " + s2);
		loginDialog.setVisible(false);
    }
    private JComponent getCenterPanel() {
        JSplitPane jsp;
        JSplitPane jsp2;
    	JPanel jp = new JPanel();
    	jp.setLayout(new BorderLayout());
    	JPanel logp = new JPanel();
    	logp.setLayout(new BorderLayout());
    	JComponent logPane = getLogPane();
    	logp.add(BorderLayout.CENTER, logPane);
    	JPanel outp = new JPanel();
    	outp.setLayout(new BorderLayout());
    	JComponent outPane = getOutPane();
    	outp.add(BorderLayout.CENTER, outPane);
    	jsp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, logp, outp);
    	
    	jsp.setDividerSize(5);
    	jsp.setOneTouchExpandable(true);
		jsp.setDividerLocation(main.getHeight()/2);
		Component listPane = getListPane();
    	jsp2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, jsp, listPane);
    	jsp2.setDividerSize(5);
    	jsp2.setOneTouchExpandable(true);
	    jsp2.setDividerLocation(main.getWidth()*7/10);
    	jp.add(BorderLayout.CENTER, jsp2);
    	// 增加双击放大事件处理
    	logPane.addMouseListener(new DoubleClickZoomerAdapter(jp, logPane));
    	for (int i = 1; i < consoles.length; i++) {
    		consoles[i].addMouseListener(new DoubleClickZoomerAdapter(jp, logPane));
    	}
		consoles[0].addMouseListener(new DoubleClickZoomerAdapter(jp, outPane));
		zoomAdapterForListPane =new DoubleClickZoomerAdapter(jp, listPane); 
		listPane.addMouseListener(zoomAdapterForListPane);
    	return jp;
    }
    public JComponent getListPane() {
    	JTabbedPane jtb = new JTabbedPane();
    	sos = new SOSTable(gmStub);
    	sos.setParamMenuBuilder(gmStub);
    	jtb.add(GameFormRES.playerList, new JScrollPane(users));
    	jtb.add(GameFormRES.todoList, new JScrollPane(sos));
    	return jtb;
    }
    public JComponent getLogPane() {
    	JTabbedPane jtp = new JTabbedPane();
    	jtp.setTabPlacement(JTabbedPane.BOTTOM);
    	for (int i = 1; i < consoles.length; i++) {
    		jtp.add(consoleTitles[i], new JScrollPane(consoles[i]));
    	}
    	return jtp;
    }
    public JComponent getOutPane() {
    	return new JScrollPane(consoles[0]);
    }
    
    /** 初始化控制台信息，包括其接收的信息，显示风格等。 */
    private void initConsoles() {
    	// 本节点提供的所有输出控制台,包括标准输出,系统,综合以及其他
    	consoles = new RichConsole[4]; // 0 console 1 system 2 general, 3 other
    	// 设置各个控制台接收的消息类型
        consoleTypes = new int[] {
        		(1 << IMessage.MSG_TYPE_COMMAND) | (1 << IMessage.MSG_TYPE_LOG),
        		(1 << IMessage.MSG_TYPE_SYSTEM) | (1 << IMessage.MSG_TYPE_GM)| (1 << IMessage.MSG_TYPE_PRIVATE),
        		(1 << IMessage.MSG_TYPE_WORLD) | (1 << IMessage.MSG_TYPE_LOCAL) | (1 << IMessage.MSG_TYPE_GANG) | 
        		(1 << IMessage.MSG_TYPE_GROUP) | (1 << IMessage.MSG_TYPE_TEAM)| (1 << IMessage.MSG_TYPE_CIRCLE) ,
        		(1 << IMessage.MSG_TYPE_OTHER)
        		};
        // 输出控制台中各种信息的显示风格
        HashMap<String,Color> colorSet = new HashMap<String,Color>();
        colorSet.put("", Color.black);
        colorSet.put("世", Color.white);
        colorSet.put("区", new Color(0xD59D7C)); // 场 
        colorSet.put( "帮", new Color(0x00FF12)); // 会
        colorSet.put("世", Color.white);
        colorSet.put("世", Color.white);
    	colorSet.put("团", new Color(0xF6FF00));
    	colorSet.put("队", new Color(0xFF9400));
    	colorSet.put("圈", new Color(0x000000));
    	colorSet.put("系", Color.yellow); // new Color(0xFF0900));
    	colorSet.put("密", new Color(0xFF0072)); // 私
    	colorSet.put("GM", new Color(0xFF0900));
    	colorSet.put("log", Color.LIGHT_GRAY); // 情?
    	colorSet.put("余", Color.white); // 其他

    	colorSet.put("灰", Color.gray);
    	colorSet.put("白", Color.white);
    	colorSet.put("绿", Color.green);
    	colorSet.put("蓝", Color.blue);
    	colorSet.put("黄", Color.yellow);
    	colorSet.put("紫", new Color(0xB6589D)); 
    	colorSet.put("红", Color.red);
    	for (int i = 0; i < consoles.length; i++) {
    		RichConsole c = new RichConsole(null);
    		consoles[i] = c;
    		for (String key : colorSet.keySet()) {
    			Style style = c.addStyle(key, null);
    			StyleConstants.setFontFamily(style, "Monospaced");
		        StyleConstants.setBackground(style, Color.BLACK);
		        StyleConstants.setForeground(style, colorSet.get(key));
		        StyleConstants.setFontSize(style, 12);
    		}
    	}
    }

	ActionListener mapChangeListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			JComboBox box = (JComboBox)e.getSource();
			Object obj = box.getSelectedItem();
			if (obj != null && obj != currentMap) {
				gmStub.con.processCommand(new String[]{"m", "l", "区", String.valueOf(((Scene)obj).id)});
			}
		}
	};

  	ActionListener commandListener = new ActionListener() {
  		public void actionPerformed(ActionEvent e) {
  			Object obj = e.getSource();
  			if (obj instanceof JMenuItem) {
  				gmStub.con.processCommand(((JMenuItem)obj).getName());
  			} else if (obj instanceof JButton) {
  				gmStub.con.processCommand(((JButton)obj).getName());
  			}
  		}
  	};

  	ActionListener sceneListener = new ActionListener() {
  		public void actionPerformed(ActionEvent e) {
  			sceneMenu.setVisible(false);
  			JButton mi = (JButton)e.getSource();
  			gmStub.con.processCommand(mi.getName());
  		}
  	};
  	public UWapData genBroadCastInfo(String message, ReceiptListener receiptListener) {
  		return new SendChatMsgPkg(message, receiptListener);
  	}
  	public static class SendChatMsgPkg extends UAData implements GmConstants, Receiptable {
        public int getAppDataType() {
            return ADMIN_CHAT_CLIENT;
        }
        public ReceiptListener receiptListener;
  	    public SendChatMsgPkg(String msg, ReceiptListener l) {
  	    	this.receiptListener = l;
  	    	this.message = msg;
  	    }
   	    public ReceiptListener getReceiptListener() {
  		  return receiptListener;
  	    }
        public int channel = sango.gm.cmd.CmdChatting.TYPE_SYS;
        public int destId = -1;
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
            return new String[] {"channel", "destId",  "nativeString", "message", "serialId"};
        }
    }
}
