package sango.gm.cmd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JFileChooser;

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
import pip.util.CommonFileFilter;
import pip.util.Excel;
import sango.GmConstants;
import sango.data.PGmTodoItem;
import sango.gm.GmStub;
import sango.gm.ui.GmMailReplyDialog;
import pip.util.*;
public class CmdGmHelp extends GmFunction  {
	private static final SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    GmMailReplyDialog replyDialog;
	private long lstRetriveTime = 0;
   

	/** 发送获取GM消息的请求 */
	private PGMMailGetList gmMailRetrievePkg = null;
	private JFileChooser fcExcelFile;

	public void registerPackage(PDataFactory factory) {
		factory.register(GmConstants.ADMIN_GMREQUEST_ADDED_SERVER, GmMailNew.class);
		factory.register(GmConstants.ADMIN_GMREQUEST_LIST_SERVER, PGMMailList.class);
		factory.register(1129, PGMMailList.class);
		factory.register(GmConstants.ADMIN_GMREQUEST_DELETE_SERVER, PGMMailDeleteSync.class);
		factory.register(GmConstants.ADMIN_GMREQUEST_SOLVE_SERVER, PGMMailFinished.class);
		factory.register(GmConstants.ADMIN_MULTIGMREQUEST_SOLVE_SERVER, PGMMarkGmDataRet.class);
	}
	public PDProcessor getPackageProcessor() {
		return new Processor();
	}
	
	/** 发送获取GM消息的请求 */
	public boolean retrieveGmHelps(GmStub gmStub) {
		long time = System.currentTimeMillis();
    	if (gmMailRetrievePkg == null) {
            gmMailRetrievePkg = new PGMMailGetList();
    	}
		lstRetriveTime = time;
    	gmStub.sndRequest(gmMailRetrievePkg);
    	return true;
	}
	private static final SimpleDateFormat fmtDateInput = new SimpleDateFormat("yyyy-MM-dd");
	public boolean retrieveGmMailWithin(GmStub gmStub, String time) {
		PGMMailGetList pkg = new PGMMailGetList();
        try {
			Date d = fmtDateInput.parse(time);
			pkg.date = time;
		} catch (ParseException e) {
			gmStub.onMessage(IMessage.MSG_TYPE_LOG, CmdGmHelpRES.dateFormatShouldBe, null);
			return false;
		}
    	gmStub.sndRequest(pkg);
    	return true;
	}
    public boolean exec(String cmd, AbstractClient aworld, String []s) throws Exception {
    	GmStub world = (GmStub)aworld;
        if (s != null && s.length >= 2) {
            if (cmd != null) {
                if (isCommand(aworld.auth, cmd)) {
                    if (s[1].equals("get") || s[1].equals("list")) {
                        if (s.length == 2) {
                        	retrieveGmHelps(world);
                        } else if (s.length == 3) {
                        	retrieveGmMailWithin(world, s[2]);
                        } else {
                            return false;
                        }
                    } else if (s[1].equals("uireply") && s.length == 3) { // gmsg uireply mailId 
                    	launchUi(world, Integer.parseInt(s[2]));
                    } else if (s[1].equals("mark") && s.length > 2) { // gmsg uireply mailId
                    	int []param = new int[s.length - 2];
                    	for (int i = 0; i < param.length; i++) {
                    		param[i] = Integer.parseInt(s[i + 2]);
                    	}
                    	markGmHelp(world, param);
                    } else if (s[1].equals("save") && s.length == 2) {
                    	 ArrayList<PGmTodoItem> mails = world.gameForm.sos.getMail();
                    	 if (mails.size() == 0) {
                    		 world.onMessage(IMessage.MSG_TYPE_LOG, CmdGmHelpRES.noHelpMsg, null);                 		
                    	 } else {
                    		 if (!Excel.hasF1()) {
	                    		 if (fcExcelFile == null) {
	 	                    		fcExcelFile = new JFileChooser();
	 	                    		fcExcelFile.addChoosableFileFilter(new CommonFileFilter(".csv", "Excel(*.csv)"));
		                    		fcExcelFile.setDialogType(JFileChooser.OPEN_DIALOG);
	                    		 }
	                    		 if (fcExcelFile.showOpenDialog(world.gameForm) == JFileChooser.APPROVE_OPTION) {
										try {
											File srcFile = fcExcelFile.getSelectedFile();
											String fileName = srcFile.getAbsolutePath();
			                    			 PrintStream out = new PrintStream(srcFile);
			                    			 for (int i = 0; i < CmdGmHelpRES.titles.length; i++) {
			                    				 if (i > 0) {
				                    				 out.print(",");
			                    				 }
			                    				 out.print(CmdGmHelpRES.titles[i]);
			                    			 }
			                    			 out.println();
				                    		 for (PGmTodoItem m : mails) {
				                    			 out.print(m.mailId);
				                    			 out.print(",");
				                    			 out.print(m.sourceId);
				                    			 out.print(",");
				                    			 out.print(m.author);
				                    			 out.print(",");
				                    			 out.print(m.content);
				                    			 out.print(",");
				                    			 out.print(m.getPos());
				                    			 out.print(",");
				                    			 out.print(m.getTime());
				                    			 out.print(",");
				                    			 out.println(m.status == PGmTodoItem.STAT_DELETED ? CmdGmHelpRES.deleted : m.status == PGmTodoItem.STAT_FINISHED ? CmdGmHelpRES.processed : CmdGmHelpRES.raw);
				                    		 }
				                    		 out.close();
											world.onMessage(IMessage.MSG_TYPE_LOG, Res.format(CmdGmHelpRES.savedToFile, fileName), null);        
										} catch (Exception e1) {
											e1.printStackTrace();
										}
	                    	        }
	                    		 
                    		 } else {
	                    		 Excel excel = new Excel();
	                    		 com.f1j.ss.Sheet sheet = excel.getSheet(0);
	                    		 sheet.setTopLeftText("left : " + world.config.title);
	                    		 sheet.setName(world.config.title);
	                    		 sheet.setColWidthAuto(0, 0, 10, 100, true);
	                    		 int row = 0;
	                    		 for (int i = 0; i < CmdGmHelpRES.titles.length; i++) {
	                    			 sheet.setText(0, i, CmdGmHelpRES.titles[i]);
	                    		 }
	                    		 for (PGmTodoItem m : mails) {
	                    			 row++;
	                    			 sheet.setText(row, 0, String.valueOf(m.mailId));
	                    			 sheet.setText(row, 1, String.valueOf(m.sourceId));
	                    			 sheet.setText(row, 2, m.author);
	                    			 sheet.setText(row, 3, m.content);
	                    			 sheet.setText(row, 4, m.getPos());
	                    			 sheet.setText(row, 5, m.getTime());
	                    			 sheet.setText(row, 6, m.status == PGmTodoItem.STAT_DELETED ? CmdGmHelpRES.deleted : m.status == PGmTodoItem.STAT_FINISHED ? CmdGmHelpRES.processed : CmdGmHelpRES.raw);
	                    		 }
	                    		 if (fcExcelFile == null) {
	 	                    		fcExcelFile = new JFileChooser();
	 	                    		fcExcelFile.addChoosableFileFilter(new CommonFileFilter(".xls", "Excel(*.xls)"));
		                    		fcExcelFile.setDialogType(JFileChooser.OPEN_DIALOG);
	                    		 }
	                    		 if (fcExcelFile.showOpenDialog(world.gameForm) == JFileChooser.APPROVE_OPTION) {
									try {
										File srcFile = fcExcelFile.getSelectedFile();
										String fileName = srcFile.getAbsolutePath();
										if (!fileName.endsWith(".xls") && !fileName.endsWith(".XLS")) {
											fileName = fileName + ".xls";
										}
										excel.saveAs(fileName);
										world.onMessage(IMessage.MSG_TYPE_LOG, Res.format(CmdGmHelpRES.savedToFile, fileName), null);        
									} catch (Exception e1) {
										e1.printStackTrace();
									}
                    	        }
                    		 }
                    	 }
                    } else if (s[1].equals("del")) {
                    	if (!world.auth.hasAuth(AuthConstants.deleteGmMail)) {
                    		world.onMessage(IMessage.MSG_TYPE_LOG, CmdGmHelpRES.noDelAuth, null);
                    		return true;
                    	}
                    	ArrayList<PGmTodoItem> lst = new ArrayList<PGmTodoItem>();
                    	PGMMailDelete pkg = new PGMMailDelete();
                        pkg.mailIds = new int[s.length - 2];
                        for (int i = 2; i < s.length; i++) {
                        	int id = Integer.parseInt(s[i]);
                        	pkg.mailIds[i-2] = id;
                        	PGmTodoItem m = world.gameForm.sos.getMail(id);
                            if (m != null) {
                            	m.status = PGmTodoItem.STAT_DELETED;
                            	lst.add(m);
                            	GmChatTrace.traceGm(world.config.account, world.getUniqServerId(), GmChatTrace.MODE_DELETE_MAIL, m.sourceId, m.content, world.config.title + ":");
                            } else {
                            	GmChatTrace.traceGm(world.config.account, world.getUniqServerId(), GmChatTrace.MODE_DELETE_MAIL, 0, "Unknown " + id, world.config.title + ":");
                            }
                        }
                        world.sndRequest(pkg);
                      	 world.gameForm.sos.removeTodo(lst);
                    } else if (s[1].equals("reply") && s.length == 7) {
//                    	if (!world.auth.hasAuth(AuthConstants.chat)) {
//                    		world.onMessage(IMessage.MSG_TYPE_LOG, "您没有权限回复玩家求助信息", null);
//                    		return true;
//                    	}
                    	PGMMailReply pkg = new PGMMailReply();
                        pkg.content = s[5];
                        pkg.title = s[4];
                        String sss = pkg.content;
//                        pkg.playerId = Integer.parseInt(s[3]);
                        pkg.playerId = 0; // 如果指定了玩家id，mailId将无效。
                        pkg.mailId = Integer.parseInt(s[2]);
                        world.sndRequest(pkg);
	                    GmChatTrace.traceGm(world.config.account, world.getUniqServerId(), GmChatTrace.MODE_MAIL, Integer.parseInt(s[3]), sss, world.config.title + ":" + s[6]);
                    }
                    return true;
                }
            }
        }
        return false;
    }
    private void markGmHelp(GmStub world, int []d) {
    	PGMMarkGmData pkg = new PGMMarkGmData();
    	pkg.ids = d;
    	world.sndRequest(pkg);
    	for (int id : d) {
    		PGmTodoItem mail = world.gameForm.sos.getMail(id);
    		if (mail != null && mail.status == 0) {
    			mail.status = 1;
    			GmChatTrace.traceGm(world.config.account, world.getUniqServerId(), GmChatTrace.MODE_MARK, mail.sourceId, CmdGmHelpRES.markMail, world.config.title + ":" + mail.content);    			
    		}
    	}
    }
    private void launchUi(GmStub world, int id) {
    	ArrayList<PGmTodoItem> lst = world.gameForm.sos.getMail();
    	int idx = -1;
    	PGmTodoItem mail = world.gameForm.sos.getMail(id);
    	if (mail != null) {
	    	for (int i = lst.size(); i-- > 0; ) {
	    		PGmTodoItem m2 = lst.get(i);
	    		if (m2.sourceId != mail.sourceId) {
	    			lst.remove(i);
	    		}
	    	}
	    	for (int i = lst.size(); i-- > 0; ) {
	    		if (lst.get(i).mailId == mail.mailId) {
	    			idx = i;
	    			break;
	    		}
	    	}
	    	if (idx == -1) {
	    		idx = lst.size();
	    		lst.add(mail);
	    	}
	    	if (replyDialog == null) {
	    		 replyDialog = new GmMailReplyDialog(world);
	    	}
	    	replyDialog.setMail(lst, idx);
	    	replyDialog.setVisible(true);
    	}
    }
    public long getAuth() {
    	return AuthConstants.chat;
    }
    public String getCommand(Auth auth) {
    	if (auth.hasAuth(getAuth())) {
    		return "gmsg";
    	}
    	return null;
    }
    public String getName(Auth auth) {
    	if (auth.hasAuth(getAuth())) {
    		return CmdGmHelpRES.cmdName;
    	}
    	return null;
    }
    public String getDescription(Auth auth) {
    	if (auth.hasAuth(getAuth())) {
	        return CmdGmHelpRES.cmdDesc;
    	}
    	return null;
    }

    /**
	 * 获取GM请求列表
	 * serial									int
	 * 每页条数									short
	 * 页数										short
	 */
    public class PGMMailGetList extends UAData implements GmConstants {
        public int getAppDataType() {
            return ADMIN_GMREQUEST_LIST_CLIENT;
        }
        public int serial;
        public short pageSize = 256;
        public short pageNo = 0;
        public String date;
        
        public String[] getProperties() {
        	return new String[] {"serial", "pageNo", "pageSize", "date"};
        }
    }
    public class PGMMarkGmData extends UAData implements GmConstants {
        public int getAppDataType() {
            return ADMIN_MULTIGMREQUEST_SOLVE_CLIENT;
        }
        public int serial;
        public int ids[];
        public String[] getProperties() {
        	return new String[] {"serial", "ids"};
        }
    }
    public static class PGMMarkGmDataRet extends UAData implements GmConstants {
        public int getAppDataType() {
            return ADMIN_MULTIGMREQUEST_SOLVE_SERVER;
        }
        public int serial;
        public int ids[];
        public String[] getProperties() {
        	return new String[] {"serial", "ids"};
        }
    }
    /**
	 * 解决GM请求
	 * 请求Id									int 
	 * 角色Id									int(如果为0，那么请求Id有效，如果不为0，那么角色Id有效)
	 * 解决邮件Title								string
	 * 解决方案									string
	 */
    public class PGMMailReply extends UAData implements GmConstants {
        public int getAppDataType() {
            return ADMIN_GMREQUEST_SOLVE_CLIENT;
        }
        public int mailId;
        public int playerId;
        public String title;
        public String content;
        
        public String[] getProperties() {
            return new String[] {"mailId", "playerId", "title", "content"};
        }
    }
	/**
	 * 解决GM请求成功
	 * 请求Id									int
	 * 角色Id									int
	 * 解决GM名字								string
	 * 解决方案									string
	 */
    public static class PGMMailFinished extends UAData implements GmConstants {
        public int getAppDataType() {
            return ADMIN_GMREQUEST_SOLVE_SERVER;
        }
        public int mailId;
        public int playerId;
        public String gmName;
        public String content;
        
        public String[] getProperties() {
            return new String[] {"mailId", "playerId", "gmName", "content"};
        }
    }
    /**
	 * 删除GM请求
	 * 	请求Id									int［］
	 */
    public class PGMMailDelete extends UAData implements GmConstants {
        public int getAppDataType() {
            return ADMIN_GMREQUEST_DELETE_CLIENT;
        }
        public int mailIds[];
        public String[] getProperties() {
            return new String[] {"mailIds"};
        }
    }
    /**
	 * 删除GM请求
	 * 	请求Id									int［］
	 */
    public static class PGMMailDeleteSync extends UAData implements GmConstants {
        public int getAppDataType() {
            return ADMIN_GMREQUEST_DELETE_SERVER;
        }
        public int mailIds[];
        public String[] getProperties() {
            return new String[] {"mailIds"};
        }
    }
    /**
     * 	 * GM请求列表
    	 * serial									int
    	 * 每页条数									short
    	 * 页数										short
    	 * 信件的总数								int
     */
        public static class PGMMailList extends UAData implements GmConstants {
            public int getAppDataType() {
                return ADMIN_GMREQUEST_LIST_SERVER;
            }
            public int serialNo;
            public short pageSize;
            public short pageNo;
            public int total;
            public PGmTodoItem[] mail;
            public String[] getProperties() {
                return new String[] {"serialNo", "pageSize", "pageNo", "total", "mail"};
            }

        }
        /**
    	 * 新增GM请求
    	 * 	请求Id						int
    	 * 	请求玩家Id					int
    	 * 	请求玩家名					string
    	 * 	请求内容						string
    	 * 	请求状态						byte(0 未解决 1 解决)
    	 * 	解决方案						string
    	 * 	提交时间						long
    	 */
            public static class GmMailNew extends UAData implements GmConstants {
                public int getAppDataType() {
                    return ADMIN_GMREQUEST_ADDED_SERVER;
                }
                public int id;
                public byte type;
                public int playerId;
                public String playerName;
                public String content;
                public byte status;
                public String solve;
//                public long time;
                public String device;
                public short sceneId;
        	    public short x;
        	    public short y;
                public String[] getProperties() {
                    return new String[] {"id", "type", "playerId", "playerName", "content", "status", "solve",/* "time", */ "device", "sceneId", "x", "y"};
                }

            }

        	public class Processor implements PDProcessor {
        	    public boolean process(pip.gm.fw.AbstractClient master, UWapData data) {
        	        if (data == null) {
        	        } else if (data instanceof PGMMailFinished) {
        	        	GmStub wd = ((GmStub)master);
        	        	PGMMailFinished d = (PGMMailFinished)data;
        	        	ArrayList<PGmTodoItem> lst = new ArrayList<PGmTodoItem>();
        	        	PGmTodoItem m2 = wd.gameForm.sos.getMail(d.mailId);
                    	if (m2 != null) {
                    		m2.status = PGmTodoItem.STAT_FINISHED;
                    		lst.add(m2);
                         	 wd.gameForm.sos.removeTodo(lst);
//            	        	wd.gameForm.sos.addDoneSos(lst);
                    	}
        	        } else if (data instanceof PGMMailDeleteSync) {
        	        	GmStub wd = ((GmStub)master);
        	        	ArrayList<PGmTodoItem> lst = new ArrayList<PGmTodoItem>();
        	        	for (int k : ((PGMMailDeleteSync)data).mailIds) {
        	        		PGmTodoItem m2 = wd.gameForm.sos.getMail(k);
        	            	if (m2 != null) {
        	            		m2.status = PGmTodoItem.STAT_DELETED;
        	            		lst.add(m2);
        	            	}
        	        	}
        	        	 wd.gameForm.sos.removeTodo(lst);
//        	        	wd.gameForm.sos.addDoneSos(lst);
        	        } else if (data instanceof PGMMarkGmDataRet) {
        	        	GmStub wd = ((GmStub)master);
        	        	ArrayList<PGmTodoItem> lst = new ArrayList<PGmTodoItem>();
        	        	for (int k : ((PGMMarkGmDataRet)data).ids) {
        	        		PGmTodoItem m2 = wd.gameForm.sos.getMail(k);
        	            	if (m2 != null) {
        	            		m2.status = PGmTodoItem.STAT_FINISHED;
        	            		lst.add(m2);
        	            	}
        	        	}
        	        	 wd.gameForm.sos.removeTodo(lst);
        	        } else if (data instanceof GmMailNew) {
        	        	 ArrayList<PGmTodoItem> lst = new ArrayList<PGmTodoItem>();
        	        	GmMailNew d = (GmMailNew)data;
        	        	if (d.type == 1) { // 举报挂机邮件
        	        		GmStub wd = ((GmStub)master);
	        	        	 wd.gameForm.main.todoTbl.addTodo(wd.gameForm, 0, CmdGmHelpRES.issueRaise + ":" + d.playerId + d.content , new String[][] {
	        	        			 {CmdGmHelpRES.processKick, "!kick " + d.playerId + " 30"},
	        	        	 });
        	        	} else { // d.type should be 0
	        	        	PGmTodoItem m = new PGmTodoItem();
	        	        	 m.mailId = d.id;
	        	        	 m.type = d.type;
	        	        	 m.sourceId = d.playerId;
	        	        	 m.author = d.playerName;
	        	        	 m.content = d.content;
	        	        	 m.status = d.status; // (0 未解决 1 解决 2 删除)
	        	        	 m.solution = "";
	        	        	 m.postTime = System.currentTimeMillis();
	        	        	 m.device = d.device;
	        	        	 m.sceneId = d.sceneId;
	        	        	 m.x = d.x;
	        	        	 m.y = d.y;
	        	        	 lst.add(m);
	        	        	 ((GmStub)master).gameForm.sos.addSos(lst);
	        	        	 GmStub wd = ((GmStub)master);
	        	        	 wd.gameForm.main.todoTbl.addTodo(wd.gameForm, m.mailId, m.sourceId + "[" + m.author + "]", new String[][] {
	        	        			 {CmdGmHelpRES.process, "gmsg uireply " + m.mailId},
	        	        	 });
        	        	}
        	        } else if (data instanceof PGMMailList) {
        	        	PGMMailList d = (PGMMailList)data;
        	        	GmStub wd = ((GmStub)master);
        	            boolean hasNew = false;
        	            ArrayList<PGmTodoItem> lst = new ArrayList<PGmTodoItem>();
        	            ArrayList<PGmTodoItem> lst2 = new ArrayList<PGmTodoItem>();
        	            for (PGmTodoItem mail : d.mail) {
        	            	PGmTodoItem m2 = wd.gameForm.sos.getMail(mail.mailId);
        	            	if (m2 != null) {
        	            		m2.status = mail.status;
        	            	} else { 
        	            		if ( mail.status == PGmTodoItem.STAT_NEW) {
        	            			if (mail.type == 1) {
        		        	        	 wd.gameForm.main.todoTbl.addTodo(wd.gameForm, 0, CmdGmHelpRES.issueRaise + ":" + mail.sourceId + mail.content , new String[][] {
        		        	        			 {CmdGmHelpRES.processKick, "!kick " + mail.sourceId + " 30"},
        		        	        	 });
        	            			} else {
	        	            			wd.gameForm.main.todoTbl.addTodo(wd.gameForm, mail.mailId, mail.sourceId + "[" + mail.author + "]", new String[][] {
	        	    	            			{CmdGmHelpRES.process, "gmsg uireply " + mail.mailId},
	        	    	            			});
	        	    	            	lst.add(mail);
        	            			}
        	            		} else {
        	            			if (mail.type != 1) {
	        	            			lst2.add(mail);
	        	            			if ("1".equals(wd.config.getStringProperty("showProcessedGmsg"))) {
	        	            				lst.add(mail);
	        	            			}
        	            			}
        	            		}
        	                }
        	            }
        	            if (lst.size() > 0) {
        	            	wd.gameForm.sos.addSos(lst);
        	            }
        	            if (lst2.size() > 0) {
        	            	wd.gameForm.sos.addDoneSos(lst2);
        	            }
        	        } else {
        	            return false;
        	        }
        	        return true;
        	    }
        	}
}
