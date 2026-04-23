package sango.gm.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import  javax.swing.border.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import pip.gm.fw.BaseConfig;
import pip.gm.fw.GmChatTrace;
import pip.gm.fw.GmFunction;
import pip.gm.fw.PDProcessor;
import pip.io.uwap.UWapData;
import pip.util.ui.LayoutUtil;
import sango.data.PGmTodoItem;
import sango.gm.GmStub;
import sango.gm.cmd.CmdAccountInfoReceice;

/**
 * 呼叫GM的求助信息处理面板。
 */
public class GmMailReplyDialog extends JDialog implements PDProcessor {
	GmStub gmStub;
	
	PGmTodoItem currentRequestl;

	JTextField mailContent = new JTextField();
	JTextField playerInfo = new JTextField();
	JTextField commitTime = new JTextField();
	JTextField posInfo = new JTextField();
	JTextField deviceInfo = new JTextField();
	JTextField tfId = new JTextField();
	JTextField replyTitle = new JTextField(80);
	JTextArea replyContnet = new JTextArea(3, 80);
	/** 展示所有相关求助信息的面板 */
	JPanel tblPane = new JPanel(new BorderLayout());
	JPanel tbl2Pane = new JPanel(new BorderLayout());
	
	/** 来自同一玩家的所有当前求助信息 */
	ArrayList<PGmTodoItem> refList;
	/** 求助信息的列表展示 */
	JTable tblReferenceRequest;
	JTable tblDoneRequest;

	JCheckBox cbMarkAll;
	JCheckBox cbQuit;
	JButton btnReply;
	JButton btnCancle;

	public GmMailReplyDialog(GmStub gmStub) {
		super(gmStub.gameForm.main);
		this.gmStub = gmStub;
		layoutComponent();
		setSize(800, 600);
		setLocation(gmStub.gameForm.main.getX() + ((gmStub.gameForm.main.getWidth() - getWidth()) >> 1),
				gmStub.gameForm.main.getY() + ((gmStub.gameForm.main.getHeight() - getHeight()) >> 2));
	}
	
	public boolean process(pip.gm.fw.AbstractClient master, UWapData data) {
		if (data instanceof CmdAccountInfoReceice.GMAccountInfo) {
			CmdAccountInfoReceice.GMAccountInfo d = (CmdAccountInfoReceice.GMAccountInfo)data;
			// 查询账号VIP等级
			if (BaseConfig.DOMAIN.equals(BaseConfig.DOMAIN_PIP)) {
				int[] vipLevel = GmFunction.queryVIPLevel(d.accountId);
				if (vipLevel != null) {
					String star = GmFunction.getStar(vipLevel[0]) + "/" + GmFunction.getStar(vipLevel[1]);
					playerInfo.setText(currentRequestl.sourceId + "[" + currentRequestl.author + "][" + star + "]");
				}
			}
			gmStub.removeProcessor(this);
			return true;
		}
		return false;
	}
	
	/** 
	 * 设置要处理的求助信息内容。
	 * @param refList 来自同一玩家的所有求助信息。
	 * @param idx 当前要处理的信息。
	 */
	public void setMail(ArrayList<PGmTodoItem> refList, int idx) {
		this.refList = refList;
		int id = -1;
		// 设置列表的内容
		String objs[][] = new String[refList.size()][5];
		String title[] = {GmMailReplyDialogRES.id, 
				GmMailReplyDialogRES.time, 
				GmMailReplyDialogRES.helpMsg, 
				GmMailReplyDialogRES.pos};
		for (int i = objs.length; i-- > 0; ) {
			PGmTodoItem m = refList.get(i);
			if (id == -1) {
				id = m.sourceId;
			}
			objs[i][0] = String.valueOf(m.mailId);
			objs[i][1] = m.getTime();
			objs[i][2] = m.content;
			objs[i][3] = m.getPos();
		}
		tblReferenceRequest = new JTable(new NotEditableTableMode(objs, title));
		tblReferenceRequest.setAutoCreateRowSorter(true);
		tblReferenceRequest.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
		tblReferenceRequest.getSelectionModel().addListSelectionListener(showDetailSelectionListener); 
		tblPane.removeAll();
		tblPane.setLayout(new BorderLayout());
		tblPane.add(BorderLayout.CENTER, new JScrollPane(tblReferenceRequest));
		tblPane.repaint();
		
		tbl2Pane.removeAll();
		if (id > 0) {
			ArrayList<String[]> dd = GmChatTrace.getHistory(gmStub.getUniqServerId(), id);
			if (dd.size() > 0) {
				objs = new String[dd.size()][];
				for (int i = 0; i < dd.size(); i++) {
					objs[i] = dd.get(i);
				}
				tblDoneRequest = new JTable(new NotEditableTableMode(objs, GmMailReplyDialogRES.doneTitles));
				tblDoneRequest.setAutoCreateRowSorter(true);
				tblDoneRequest.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
				tbl2Pane.setLayout(new BorderLayout());
				tbl2Pane.add(BorderLayout.CENTER, new JScrollPane(tblDoneRequest));
				tblDoneRequest.setForeground(Color.gray);
				TableColumnModel tcm = tblDoneRequest.getColumnModel();
				for (int i = colSize.length; i-- > 0; ) {
					TableColumn firsetColumn = tcm.getColumn(i);
					if (colSize[i][0] != 0) {
						firsetColumn.setMinWidth(colSize[i][0]); 
					}
					if (colSize[i][1] != 0) {
						firsetColumn.setPreferredWidth(colSize[i][1]); 
					}
					if (colSize[i][2] != 0) {
						firsetColumn.setMaxWidth(colSize[i][2]); 
					}
				}
			}
		}
		tbl2Pane.repaint();
		
		setMail(refList.get(idx));
	}
	private int colSize[][] = {
			{80, 100, 120}, //"时间",
			{200, 300, 0}, //  "求助内容",
			{30, 40, 40}, //  "GM", 
			{30, 60, 80}, // "动作", 
			{200, 300, 0}, // "处理意见"
	};
	/** 设置当前正在处理的求助信息。 */
	private void setMail(PGmTodoItem m) {
		currentRequestl = m;
		mailContent.setText(m.content);
		playerInfo.setText(m.sourceId + "[" + m.author + "]");
		commitTime.setText(m.getTime());
		posInfo.setText(m.getPos());
		deviceInfo.setText(m.device);
		tfId.setText(String.valueOf(m.mailId));
		if (!BaseConfig.DOMAIN.equals(BaseConfig.DOMAIN_JAPAN_MOBILE)) {
			replyTitle.setText(GmMailReplyDialogRES.inProcessing);
			replyContnet.setText(GmMailReplyDialogRES.inProcessingContent);
		}
		
		// 增加查询账号信息，取得VIP等级
		gmStub.gameForm.getUwapApp().con.processCommand("showaccount " + currentRequestl.sourceId);
		gmStub.addProcessor(this);
	}
	// 当选中某条求助时，将其详细信息展示出来
	private ListSelectionListener showDetailSelectionListener = new ListSelectionListener() {
		public void valueChanged(ListSelectionEvent e) {
			 if(e.getValueIsAdjusting()) { 
				 return; 
			 }
			 if (tblReferenceRequest != null) {
				 if (!tblReferenceRequest.getSelectionModel().isSelectionEmpty()){ 
					 int k = tblReferenceRequest.getSelectedRow();
					 k = tblReferenceRequest.convertRowIndexToModel(k);
					 String v = (String)tblReferenceRequest.getModel().getValueAt(k, 0);
					 if (v != null) {
						 k = Integer.parseInt(v);
						 for (PGmTodoItem m : refList) {
							 if (m.mailId == k) {
								 setMail(m);
								GmMailReplyDialog.this.repaint(); 
							 }
						 }
					 }
				 }
			 }
		}
	};

	/** GM求助的详细信息 */
	private JComponent getDetailPane() {
		
		commitTime.setEditable(false);
		playerInfo.setEditable(false);
		mailContent.setEditable(false);
		deviceInfo.setEnabled(false);
		posInfo.setEditable(false);
		tfId.setEditable(false);
		mailContent.setBackground(Color.white);
		mailContent.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, Color.WHITE.darker(), Color.GRAY.brighter()));
		LayoutUtil lu = new LayoutUtil();
		int row = 0;
		JPanel p = new JPanel();
		p.setLayout(new GridBagLayout());
		p.add(lu.getRightAlignText(GmMailReplyDialogRES.playerId), lu.getConstrains(0,row, 1, 1));
		p.add(playerInfo, lu.getConstrains(1,row, 1, 1, 10, 1));
		p.add(lu.getRightAlignText(GmMailReplyDialogRES.time), lu.getConstrains(2,row, 1, 1));
		p.add(commitTime, lu.getConstrains(3,row, 1, 1, 10, 1));
		row++;
		p.add(lu.getRightAlignText(GmMailReplyDialogRES.content), lu.getConstrains(0,row, 1, 1));
		p.add(mailContent, lu.getConstrains(1,row, 5, 1));
		row++;
		p.add(lu.getRightAlignText(GmMailReplyDialogRES.id), lu.getConstrains(0,row, 1, 1));
		p.add(tfId, lu.getConstrains(1,row, 1, 1, 10, 1));
		p.add(lu.getRightAlignText(GmMailReplyDialogRES.device), lu.getConstrains(2,row, 1, 1));
        p.add(deviceInfo, lu.getConstrains(3,row, 1, 1, 10, 1));
		p.add(lu.getRightAlignText(GmMailReplyDialogRES.pos), lu.getConstrains(4,row, 1, 1));
		p.add(posInfo, lu.getConstrains(5,row, 1, 1, 10, 1));
		
		p.setBorder(new javax.swing.border.TitledBorder(GmMailReplyDialogRES.detail));
		return p;
	}
	private JComponent getAllReferenceMailPane() {
		JPanel p = new JPanel(new GridLayout(2,1));
		p.add(getReferenceMailPane(), 0);
		p.add(getDoneMailPane(), 1);
		return p;
//		JSplitPane jRefSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, getReferenceMailPane(), getDoneMailPane());
//		return jRefSplitPane;
	}
	/** GM求助的参考信息列表 */
	private JComponent getReferenceMailPane() {
		JPanel p = new JPanel(new BorderLayout());
		p.add(BorderLayout.CENTER, tblPane);
		TitledBorder tb = new javax.swing.border.TitledBorder(GmMailReplyDialogRES.samePlayer);
		p.setBorder(tb);
		return p;
	}
	/** 已经处理GM求助的参考信息列表 */
	private JComponent getDoneMailPane() {
		JPanel p = new JPanel(new BorderLayout());
		p.add(BorderLayout.CENTER, tbl2Pane);
		TitledBorder tb = new javax.swing.border.TitledBorder(GmMailReplyDialogRES.samePlayerDone);
		tb.setTitleColor(Color.gray);
		p.setBorder(tb);
		return p;
	}
	JDialog confirmDlg = new JDialog();
	private void initConfirmDialog() {
		confirmDlg.setModal(true);
		Container container = confirmDlg.getContentPane();
		container.setLayout(new BorderLayout());
		container.add(BorderLayout.CENTER, new JLabel("Confirm sending reply to the player?"));//mengjie modify
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout());
		JButton okBtn = new JButton(GmMailReplyDialogRES.reply);
		JButton cancelBtn =  new JButton(GmMailReplyDialogRES.cancel);
		p.add(okBtn);
		p.add(cancelBtn);
		cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				confirmDlg.setVisible(false);
			}
		});
		okBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				// 回复玩家
				gmStub.con.processCommand(new String[]{"gmsg", "reply", String.valueOf(currentRequestl.mailId), String.valueOf(currentRequestl.sourceId), replyTitle.getText(), replyContnet.getText(), currentRequestl.content});
				//mengjie add
				replyTitle.setText("");
				replyContnet.setText("");
				// 标注已经处理
				if (cbMarkAll.isSelected()) {
					String []param = new String[refList.size() +2];
					for (int i = 0; i < refList.size(); i++) {
						PGmTodoItem m = refList.get(i);
						param[i+2] = String.valueOf(m.mailId);
						m.status = PGmTodoItem.STAT_FINISHED;
					}
					param[0] = "gmsg";
					param[1] = "mark";
					gmStub.con.processCommand(param);
				} else {
					gmStub.con.processCommand(new String[]{"gmsg", "mark", String.valueOf(currentRequestl.mailId)});
					currentRequestl.status = PGmTodoItem.STAT_FINISHED;
				}
				confirmDlg.setVisible(false);
				// 退出选项
				if (cbQuit.isSelected()) {
					GmMailReplyDialog.this.setVisible(false);
				}
			}
		});
		container.add(BorderLayout.SOUTH, p);
		confirmDlg.pack();
		centerWindow(confirmDlg);
	}
	
	/**
     * To place a component at the center of the screen.
     */
    public static void centerWindow(Window w) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension d = toolkit.getScreenSize();
        Dimension size = w.getSize();
        int x = (d.width - size.width) / 2;
        int y = (d.height - size.height) / 2;
        w.setLocation(x, y);
    }
    
	/** GM回复面板 */
	private JComponent getReferenceReplylPane() {
		initConfirmDialog();
		LayoutUtil lu = new LayoutUtil();
		JPanel outP = new JPanel();
		outP.setLayout(new BorderLayout());
		
		JPanel p = new JPanel();
		p.setLayout(new GridBagLayout());
		p.add(lu.getRightAlignText(GmMailReplyDialogRES.title), lu.getConstrains(0,0, 1, 1));
		p.add(replyTitle, lu.getConstrains(1,0, 1, 1, 10, 1));
		p.add(lu.getRightAlignText(GmMailReplyDialogRES.content), lu.getConstrains(0,1, 1, 1));
		p.add(replyContnet, lu.getConstrains(1,1, 1, 1, 10, 1));
		p.setBorder(new javax.swing.border.TitledBorder(GmMailReplyDialogRES.qa));
		
		outP.add(BorderLayout.CENTER, p);
		
		JPanel midP = new JPanel(new BorderLayout());
		p = new JPanel(new FlowLayout());
		
		cbMarkAll = new JCheckBox(GmMailReplyDialogRES.markAll);
		cbMarkAll.setToolTipText(GmMailReplyDialogRES.markAllTip);
		p.add(cbMarkAll);
		cbQuit = new JCheckBox(GmMailReplyDialogRES.quitAfter);
		cbQuit.setToolTipText(GmMailReplyDialogRES.quitAfterTip);
		cbQuit.setSelected(true);
		p.add(cbQuit);
		midP.add(BorderLayout.WEST, p);
		
		p = new JPanel(new FlowLayout());
		
		btnReply = new JButton(GmMailReplyDialogRES.reply);
		btnReply.setToolTipText(GmMailReplyDialogRES.replyTip);
		p.add(btnReply);
//		btnReply.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				confirmDlg.pack();
//				confirmDlg.setVisible(true);
//				LayoutUtil.setWindowCentrallize(confirmDlg, GmMailReplyDialog.this);
//
//			}
//		});
		btnReply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 回复玩家
				gmStub.con.processCommand(new String[]{"gmsg", "reply", String.valueOf(currentRequestl.mailId), String.valueOf(currentRequestl.sourceId), replyTitle.getText(), replyContnet.getText(), currentRequestl.content});
				// 标注已经处理
				if (cbMarkAll.isSelected()) {
					String []param = new String[refList.size() +2];
					for (int i = 0; i < refList.size(); i++) {
						PGmTodoItem m = refList.get(i);
						param[i+2] = String.valueOf(m.mailId);
						m.status = PGmTodoItem.STAT_FINISHED;
					}
					param[0] = "gmsg";
					param[1] = "mark";
					gmStub.con.processCommand(param);
				} else {
					gmStub.con.processCommand(new String[]{"gmsg", "mark", String.valueOf(currentRequestl.mailId)});
					currentRequestl.status = PGmTodoItem.STAT_FINISHED;
				}
				// 退出选项
				if (cbQuit.isSelected()) {
					GmMailReplyDialog.this.setVisible(false);
				}
			}
		});
		btnCancle = new JButton(GmMailReplyDialogRES.cancel);
		btnCancle.setToolTipText(GmMailReplyDialogRES.cancelTip);
		btnCancle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GmMailReplyDialog.this.setVisible(false);
				//mengjie add
				replyTitle.setText("");
				replyContnet.setText("");
			}
		});
		p.add(btnCancle);
		
		JButton btn = new JButton(GmMailReplyDialogRES.fullScr);
		btn.setToolTipText(GmMailReplyDialogRES.fullScrTip);
		btn.addActionListener(zoomAction);
		midP.add(BorderLayout.EAST, btn);
		
		midP.add(BorderLayout.CENTER, p);
		
		outP.add(BorderLayout.SOUTH, midP);
		return outP;
	}

	private void layoutComponent() {
		setLayout(new BorderLayout());
		JPanel jp = new JPanel(new BorderLayout());
		jp.add(BorderLayout.NORTH, getDetailPane());
		jp.add(BorderLayout.CENTER, getAllReferenceMailPane());
		jp.add(BorderLayout.SOUTH, getReferenceReplylPane());
		add(BorderLayout.WEST, new JLabel(" "));
		add(BorderLayout.EAST, new JLabel(" "));
		add(BorderLayout.CENTER, jp);
	}
	private ActionListener zoomAction = new ActionListener() {
		Point lastPoint;
		Dimension lastDimension;
		public void actionPerformed(ActionEvent e) {
			if (lastPoint != null && lastDimension != null) {
				GmMailReplyDialog.this.setLocation(lastPoint);
				GmMailReplyDialog.this.setSize(lastDimension);
				lastPoint = null;
			} else {
				lastPoint = GmMailReplyDialog.this.getLocation();
				lastDimension = GmMailReplyDialog.this.getSize();
				Point p = gmStub.gameForm.main.getLocation();
				GmMailReplyDialog.this.setLocation(0, 0);
				 Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				GmMailReplyDialog.this.setSize(screenSize.width, screenSize.height - 30);
			}
		 }
	};
	private class NotEditableTableMode extends  AbstractTableModel {
		String objs[][];
		String title[];
		public NotEditableTableMode(String objs[][], String title[]) {
			this.objs = objs;
			this.title = title;
		}
        public String getColumnName(int column) { return title[column]; }
        public int getRowCount() { return objs.length; }
        public int getColumnCount() { return title.length; }
        public Object getValueAt(int row, int col) { return objs[row][col]; }
        public boolean isCellEditable(int row, int column) { return false; }
        public void setValueAt(Object value, int row, int col) {
        	objs[row][col] = (String)value;
            fireTableCellUpdated(row, col);
        }
	}
}
