package sango.gm.cmd;

import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import pip.gm.fw.AbstractClient;
import pip.gm.fw.Auth;
import pip.gm.fw.AuthConstants;
import pip.gm.fw.GmFunction;
import pip.gm.fw.IMessage;
import pip.gm.fw.PDProcessor;
import pip.io.uwap.PDataFactory;
import pip.io.uwap.UAData;
import pip.io.uwap.UWapData;
import pip.util.Res;
import pip.util.StringUtil;
import pip.util.ui.LayoutUtil;
import pip.util.ui.RichConsole;
import sango.GmConstants;
import sango.data.POnlinePlayer;
import sango.gm.GmStub;
import sango.gm.GmStub.Scene;
import cwu.util.DebugUtil;
import cwu.util.sort.CompareAgent;
import cwu.util.sort.SortAgent;

/** 查询当前玩家状态命令 */
public class CmdWho extends GmFunction {
	SearchPlayerDialog dlg;
	java.text.NumberFormat percentFmt = java.text.NumberFormat.getPercentInstance();
	public CmdWho() {
		percentFmt.setMinimumFractionDigits(2);
	}
	public void registerPackage(PDataFactory factory) {
		factory.register((int)GmConstants.ADMIN_WHO_SERVER, PWhoResule.class);
	}
	public PDProcessor getPackageProcessor() {
		return new Processor();
	}

    public boolean exec(String cmd, AbstractClient aworld, String []s) throws Exception {
    	GmStub world = (GmStub)aworld;
        if (s != null && s.length >= 1) {
            if (cmd != null) {
                if (isCommand(aworld.auth, cmd)) {
                    if (s.length == 3 && "pos-near".equals(s[1])) {
                    	POnlinePlayer u = world.gameForm.users.getUser(Integer.parseInt(s[2]));
                    	if (u == null) {
                    		world.onMessage(IMessage.MSG_TYPE_LOG, CmdWhoRES.err1, null);
                    	} else {
                    		world.con.processCommand(new String[]{"m", "l", "区", String.valueOf(u.stageId)});
                    	}
                    } else if (s.length == 2 && "ui".equals(s[1])) {
                    	if (dlg == null) {
                    		dlg = new SearchPlayerDialog(world);
                    	}
                    	if (dlg.isVisible()) {
                    		dlg.toFront();
                    	} else {
                    		new Thread() {
                    			public void run() {
                            		dlg.setVisible(true);
                    			}
                    		}.start();
                    	}
                    } else {
                        world.onMessage(IMessage.MSG_TYPE_LOG, CmdWhoRES.errPara  + getDescription(aworld.auth), null);
                    }
                    return true;
                }
            }
        }
        return false;
    }
    public ActionListener searchListener = new ActionListener() {
    	public void actionPerformed(ActionEvent event) {
    		ArrayList<POnlinePlayer> lstCurrentUsers = dlg.gmStub.gameForm.users.getUsers();
    		Pattern namePattern = null;
    		Pattern gangPattern = null;
    		int minLevel = Integer.MIN_VALUE;
    		int maxLevel = Integer.MAX_VALUE;
    		int minId = Integer.MIN_VALUE;
    		int maxId = Integer.MAX_VALUE;

    		String s = dlg.tfName.getText().trim();
    		if (s.length() != 0) {
    			namePattern = Pattern.compile(s);
    		}
    		s = dlg.tfGangName.getText().trim();
     		if (s.length() != 0) {
     			gangPattern = Pattern.compile(s);
     		}
    		s = dlg.tfMinId.getText().trim();
     		if (s.length() != 0) {
     			try {
					minId = Integer.parseInt(s);
				} catch (NumberFormatException e) {
				}
     		}
    		s = dlg.tfMaxId.getText().trim();
     		if (s.length() != 0) {
     			try {
					maxId = Integer.parseInt(s);
				} catch (NumberFormatException e) {
				}
     		}
    		s = dlg.tfMinLevel.getText().trim();
     		if (s.length() != 0) {
     			try {
					minLevel = Integer.parseInt(s);
				} catch (NumberFormatException e) {
				}
     		}
    		s = dlg.tfMaxLevel.getText().trim();
     		if (s.length() != 0) {
     			try {
					maxLevel = Integer.parseInt(s);
				} catch (NumberFormatException e) {
				}
     		}
     		GmStub.Scene scene = (GmStub.Scene)dlg.cbbMap.getSelectedItem();
     		if (scene != null && scene.id == -1) {
     			scene = null;
     		}
     		
     		for (int i = lstCurrentUsers.size(); i-->0; ) {
     			POnlinePlayer u = lstCurrentUsers.get(i);
     			boolean matched = false;
     			do {
	     			if (namePattern != null) {
	     				if (!namePattern.matcher(u.name).find()) {
	     					break;
	     				}
	     			}
	     			if (gangPattern != null) {
	     				if (!gangPattern.matcher(u.tongName).find()) {
	     					break;
	     				}
	     			}
	     			if (u.level < minLevel) {
     					break;
	      			}
	     			if (u.level > maxLevel) {
     					break;
	      			}
	     			if (u.id < minId) {
     					break;
	      			}
	     			if (u.id > maxId) {
     					break;
	      			}
	     			if (scene != null) {
	     				if (u.stageId != scene.id) {
	     					break;
	     				}
	     			}
	     			matched = true;
     			} while (false);
     			if (!matched) {
     				lstCurrentUsers.remove(u);
     			}
     		}
     		POnlinePlayer aIdleUsers[] = new POnlinePlayer[lstCurrentUsers.size()];
     		lstCurrentUsers.toArray(aIdleUsers);
     		reportSearchResult(dlg.gmStub, aIdleUsers, CmdWhoRES.list , 0);
    	}
    };
    
    private void reportSearchResult(GmStub world,POnlinePlayer[] aIdleUsers, String title, int n) {
        SortAgent.sort(aIdleUsers, new CompareAgent() {
            public int compare(Object object, Object object1) {
                POnlinePlayer u1 = (POnlinePlayer)object;
                POnlinePlayer u2 = (POnlinePlayer)object1;
                if (u1 == null || u2 == null) {
                    return 1;
                }
                return u1.id - u2.id;
            }
        }, 0);

        ArrayList<String[]> lstDisplayMsg = new ArrayList<String[]>();
        for (POnlinePlayer player : aIdleUsers) {
        	String tt = player.id + "[" + StringUtil.formal(player.name) + "]";
        	String t[] = RichConsole.genPlayerHyperString(player.name, player.id);
        	if (t != null) {
        		lstDisplayMsg.add(new String[]{tt, t[0], t[1]});
        	} else {
        		lstDisplayMsg.add(new String[]{tt});
        	}
        }
        StringBuilder buf = new StringBuilder();
        buf.append(StringUtil.formatMultiRows(title, "  ", lstDisplayMsg, 120, 1, true));
        buf.append("\n");
        buf.append(DebugUtil.getDate());
        buf.append(Res.format(CmdWhoRES.totalNum , Integer.valueOf(aIdleUsers.length)));
        if (n > 0) {
            String ss = percentFmt.format(((double)aIdleUsers.length) / ((double)n));
            buf.append(Res.format(CmdWhoRES.numPercent , Integer.valueOf(n), ss));
        }
        world.onMessage(IMessage.MSG_TYPE_LOG, buf.toString(), null);
    }
    public long getAuth() {
    	return AuthConstants.who;
    }
    public String getCommand(Auth auth) {
    	if (auth.hasAuth(getAuth())) {
    		return "search";
    	} 
    	return null;
    }
    public String getName(Auth auth) {
    	if (auth.hasAuth(getAuth())) {
    		return CmdWhoRES.cmdName ;
    	}
    	return null;
    }
    public String getDescription(Auth auth) {
    	if (auth.hasAuth(getAuth())) {
        return CmdWhoRES.cmdDesc;
    	}
    	return null;
    }
    
	public static class PWhoResule extends UAData implements GmConstants {
		public int getAppDataType() {
			return ADMIN_WHO_SERVER;
		}
		public int serialNum;
		public POnlinePlayer[] players;
		public String[] getProperties() {
			return new String[] { "serialNum", "players" };
		}
	}
	
	public class Processor implements PDProcessor {
	    public boolean process(pip.gm.fw.AbstractClient master, UWapData data) {
	        if (data instanceof PWhoResule) {
	        	PWhoResule d = (PWhoResule)data;
	        	((GmStub)master).gameForm.users.syncUsers(d.players);
	        	StringBuffer buf = new StringBuffer();
	        	int n = d.players.length;
	        	buf.append(Res.format(CmdWhoRES.totalOnline , Integer.valueOf(n)));
	        	if (n > 0) {
	        		HashMap<Integer,Integer> sceneNum = new HashMap<Integer,Integer>(); 
	        		HashMap<Integer,Integer> levelNum = new HashMap<Integer,Integer>(); 
		        	int country[] = new int[3];
		        	int careers[] = new int[4];
		        	int gender[] = new int[2];
		        	int levels[] = new int[120/LEVEL_SCALE];
		        	int maxLevel = 0;
		        	for (POnlinePlayer p : d.players) {
		        		p.getCountry(); // 确保国家职业属性被正确保存。
		        		country[p.getCountryId()-1]++;
		        		careers[p.career]++;
		        		gender[p.gender]++;
		        		Integer m = sceneNum.get(p.stageId);
		        		if (m != null) {
		        			sceneNum.put(p.stageId, m.intValue() + 1);
		        		} else {
		        			sceneNum.put(p.stageId, 1);
		        		}
		        		m = levelNum.get((int)p.level);
		        		if (m != null) {
		        			levelNum.put((int)p.level, m.intValue() + 1);
		        		} else {
		        			levelNum.put((int)p.level, 1);
		        		}
		        		int k =(p.level-1)/LEVEL_SCALE; 
		        		levels[k]++;
		        		if (maxLevel <= k) {
		        			maxLevel = k+1;
		        		}
		        	}
		        	buf.append(Res.format(CmdWhoRES.onlineCountry,
		        			Integer.valueOf(country[0]), percentFmt.format(((double)country[0] / ((double)n))),
		        			Integer.valueOf(country[1]), percentFmt.format(((double)country[1] / ((double)n))),
		        			Integer.valueOf(country[2]), percentFmt.format(((double)country[2] / ((double)n)))));
		        	buf.append(Res.format(CmdWhoRES.onlineGender,
		        			Integer.valueOf(gender[0]), percentFmt.format(((double)gender[0] / ((double)n))),
		        			Integer.valueOf(gender[1]), percentFmt.format(((double)gender[1] / ((double)n)))));
		        	if (careers[0] < n) {
			        	buf.append(Res.format(CmdWhoRES.onlineCareer,
			        			Integer.valueOf(careers[0]), percentFmt.format(((double)careers[0] / ((double)n))),
			        			Integer.valueOf(careers[1]), percentFmt.format(((double)careers[1] / ((double)n))),
			        			Integer.valueOf(careers[2]), percentFmt.format(((double)careers[2] / ((double)n))),
			        			Integer.valueOf(careers[3]), percentFmt.format(((double)careers[3] / ((double)n)))));
		        	}
	        		buf.append(CmdWhoRES.onlineLevel); 
		        	for (int i = 0; i < maxLevel; i++) {
		        		buf.append(Res.format(CmdWhoRES.onlineLevelData,
			        			Integer.valueOf(i * LEVEL_SCALE + 1),
			        			Integer.valueOf(i * LEVEL_SCALE + LEVEL_SCALE),
			        			Integer.valueOf(levels[i]),
			        			percentFmt.format(((double)levels[i] / ((double)n)))));
		        	}
		        	// 前10个密集场景区域
		        	int numData[][] = new int[sceneNum.size()][2];
		        	int k = 0;
		        	for (int stageId : sceneNum.keySet()) {
		        		numData[k][0] = stageId;
		        		numData[k][1] = sceneNum.get(stageId).intValue();
		        		k++;
		        	}
		        	SortAgent.sort(numData, new CompareAgent() {
		                public int compare(Object object, Object object1) {
		                    int[] u1 = (int[])object;
		                    int[] u2 = (int[])object1;
		                    return u2[1] - u1[1];
		                }
		            }, 0);
		        	buf.append(CmdWhoRES.onlineTopScenes);
		        	int max = numData[0][1];
		        	for (int i = 0; i < 10 && i < numData.length; i++) {
		        		int dn[] = numData[i]; 
		        		double p = ((double)dn[1]) / ((double)n);
		        		buf.append(Res.format(CmdWhoRES.onlineTopSceneData,
		        				dn[0], dn[1], percentFmt.format(p), genBar(dn[1], max, 12)));
		        	}
		        	// 前10个密集级别分布
		        	numData = new int[levelNum.size()][2];
		        	k = 0;
		        	for (int level : levelNum.keySet()) {
		        		numData[k][0] = level;
		        		numData[k][1] = levelNum.get(level).intValue();
		        		k++;
		        	}
		        	SortAgent.sort(numData, new CompareAgent() {
		                public int compare(Object object, Object object1) {
		                    int[] u1 = (int[])object;
		                    int[] u2 = (int[])object1;
		                    return u2[1] - u1[1];
		                }
		            }, 0);
		        	buf.append(CmdWhoRES.onlineTopLevel);
		        	max = numData[0][1];
		        	for (int i = 0; i < 10 && i < numData.length; i++) {
		        		int dn[] = numData[i]; 
		        		double p = ((double)dn[1]) / ((double)n);
		        		buf.append(Res.format(CmdWhoRES.onlineTopLevelData,
		        				dn[0], dn[1], percentFmt.format(p), genBar(dn[1], max, 12)));
		        	}
	        	}
	        	master.onMessage(IMessage.MSG_TYPE_LOG, buf.toString(), null);
	        } else {
	            return false;
	        }
	        return true;
	    }
	}
	public String genBar(int v, int maxv, int len) {
		StringBuilder b = new StringBuilder();
		for (int i = v * len / maxv; i-- > 0;) {
			b.append("=");
		}
		return b.toString();
	}
	public static final int LEVEL_SCALE = 5;
	class SearchPlayerDialog extends JDialog {
		GmStub gmStub;
		JTextField tfName;
		JTextField tfMinId;
		JTextField tfMaxId;
		JTextField tfMinLevel;
		JTextField tfMaxLevel;
		JTextField tfGangName;
		JComboBox cbbMap;
		LayoutUtil lu = new LayoutUtil();
		public SearchPlayerDialog(GmStub world) {
			super(world.gameForm.main);
			gmStub = world;
			int i = gmStub.gameInfo.maps.size();
		      Scene[] maps = new Scene[i + 1];
		      if (i > 0) {
		    	  System.arraycopy(gmStub.gameInfo.maps, 0, maps, 0, i);
		      }
		      maps[i] = new Scene();
		      maps[i].level = -1;
		      maps[i].id = -1;
		      maps[i].name = CmdWhoRES.allScene;
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
		      layoutComponents();
		      pack();
		      setLocation(gmStub.gameForm.main.getX() + ((gmStub.gameForm.main.getWidth() - getWidth()) >> 1),
		    		  gmStub.gameForm.main.getY() + ((gmStub.gameForm.main.getHeight() - getHeight()) >> 2));
		}
		private void layoutComponents() {
			setLayout(new GridBagLayout());
			int row = 0;

			tfName = new JTextField(20);
			JLabel lbl = new JLabel(CmdWhoRES.playerName);
			lbl.setToolTipText(CmdWhoRES.playerNameTip);
			add(lu.getRightAlignComponent(lbl), lu.getConstrains(0, row, 1, 1));
			add(tfName, lu.getConstrains(1, row, 3, 1));
			
			row ++;
			tfMinId = new JTextField(8);
			tfMaxId = new JTextField(8);
			add(lu.getRightAlignText(CmdWhoRES.idRange), lu.getConstrains(0, row, 1, 1));
			add(tfMinId, lu.getConstrains(1, row, 1, 1));
			add(new JLabel("-"), lu.getConstrains(2 , row, 1, 1));
			add(tfMaxId, lu.getConstrains(3, row, 1, 1));
			
			row ++;
			tfMinLevel = new JTextField(8);
			tfMaxLevel = new JTextField(8);
			add(lu.getRightAlignText(CmdWhoRES.levelRange), lu.getConstrains(0, row, 1, 1));
			add(tfMinLevel, lu.getConstrains(1, row, 1, 1));
			add(new JLabel("-"), lu.getConstrains(2, row, 1, 1));
			add(tfMaxLevel, lu.getConstrains(3, row, 1, 1));
			
			row++;
			tfGangName = new JTextField(20);
			lbl = new JLabel(CmdWhoRES.gangName);
			lbl.setToolTipText(CmdWhoRES.gangNameTip);
			add(lu.getRightAlignComponent(lbl), lu.getConstrains(0, row, 1, 1));
			add(tfGangName, lu.getConstrains(1, row, 3, 1));

			row++;
			add(lu.getRightAlignText(CmdWhoRES.scenePos), lu.getConstrains(0, row, 1, 1));
			add(cbbMap, lu.getConstrains(1, row, 3, 1));
			row++;
			JPanel p = new JPanel(new FlowLayout());
			JButton btn = new JButton(CmdWhoRES.hideOption);
			btn.setToolTipText(CmdWhoRES.hidOptionTip);
			btn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dlg.setVisible(false);
				}
			});
			p.add(btn);
			
			btn = new JButton(CmdWhoRES.search);
			btn.setToolTipText(CmdWhoRES.searchTip);
			btn.addActionListener(searchListener);
			p.add(btn);
			add(p, lu.getConstrains(0, row, 4, 1));
		}
	}
}
