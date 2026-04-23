package sango.gm.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import pip.util.ui.RichConsole.ParamMenuItemBuilder;
import sango.data.PGmTodoItem;
import sango.gm.GmStub;
/**
 * GM求助信息显示列表。
 * 
 */
public class SOSTable extends JTable {
	/** 网络连接引擎 */
	public GmStub gmStub;
	/** 表格Model,用来访问数据 */
	MyTableModel mdl = null;
	/** 右键弹出菜单,处理求助信息 */
	JPopupMenu jpop = new JPopupMenu();
	/** 当前焦点求助信息 */
	JMenuItem fixMenus[] = new JMenuItem[4];
	/** 已经处理的邮件 */
	HashSet<PGmTodoItem> mailProcessed = new HashSet<PGmTodoItem>();
	/** 选中的所有邮件的行号 */
	int rowIndex[];

	public SOSTable(GmStub stub) {
		super(new MyTableModel());
		this.gmStub = stub;
		mdl = (MyTableModel)getModel();
		setAutoCreateRowSorter(true);
		JMenuItem mi = new JMenuItem(SOSTableRES.reply);
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jpop.setVisible(false);
				if (rowIndex.length == 1) {
					PGmTodoItem m =  mdl.todos.get(rowIndex[0]);
					gmStub.con.processCommand(new String[]{"gmsg", "uireply", String.valueOf(m.mailId)});
				}
			}
		});
		fixMenus[0]= mi;
		mi = new JMenuItem(SOSTableRES.ignore); 
		mi.setToolTipText(SOSTableRES.ignoreTip);
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jpop.setVisible(false);
				ArrayList<PGmTodoItem> mails = new ArrayList<PGmTodoItem>();
				for (int i = 0; i < rowIndex.length; i++) {
					PGmTodoItem m = mdl.todos.get(rowIndex[i]);
					if (m != null) {
						mails.add(m);
					}
				}
				removeTodo(mails);
			}
		});
		fixMenus[1]= mi;
		mi = new JMenuItem(SOSTableRES.markDone);
		mi.setToolTipText(SOSTableRES.markDoneTip);
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jpop.setVisible(false);
				ArrayList<PGmTodoItem> mails = new ArrayList<PGmTodoItem>();
				for (int i = 0; i < rowIndex.length; i++) {
					PGmTodoItem m = mdl.todos.get(rowIndex[i]);
					if (m != null) {
						if (m.status != PGmTodoItem.STAT_DELETED) {
							m.status = PGmTodoItem.STAT_FINISHED;
							gmStub.con.processCommand(new String[]{"gmsg", "mark", String.valueOf(m.mailId)});
						}
						mails.add(m);
					}
				}
//				removeTodo(mails);
			}
		});
		fixMenus[2]= mi;
		mi = new JMenuItem(SOSTableRES.del);
		mi.setToolTipText(SOSTableRES.delTip);
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jpop.setVisible(false);
				ArrayList<PGmTodoItem> mails = new ArrayList<PGmTodoItem>();
				for (int i = 0; i < rowIndex.length; i++) {
					PGmTodoItem m = mdl.todos.get(rowIndex[i]);
					if (m != null) {
						if (m.status != PGmTodoItem.STAT_DELETED) {
							m.status = PGmTodoItem.STAT_DELETED;
							gmStub.con.processCommand(new String[]{"gmsg", "del", String.valueOf(m.mailId)});
						}
						mails.add(m);
					}
				}
//				removeTodo(mails);
			}
		});
		fixMenus[3]= mi;
		addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					int k = getSelectedRowCount();
					if (k == 0) {
						java.awt.Point p = e.getPoint();
						rowIndex = new int[1];
						rowIndex[0] = rowAtPoint(p);
					} else { 
						rowIndex = getSelectedRows();
					} 
					for (int i = 0; i < rowIndex.length; i++) {
						rowIndex[i] = convertRowIndexToModel(rowIndex[i]);
					}
			        	
		        	jpop.removeAll();
		        	for (int i = 0; i < fixMenus.length; i++) {
		        		if (i != 0 || rowIndex.length == 1) {
		        			jpop.add(fixMenus[i]);
		        		}
		        	}
		        	if (rowIndex.length == 1 && paramMenuBuilder != null) {
		        		PGmTodoItem focusingMail = mdl.todos.get(rowIndex[0]);
		        		ArrayList<String[]> ss = paramMenuBuilder.genMenu(new String[]{String.valueOf(focusingMail.sourceId), focusingMail.author});
		        		if (ss.size() > 0) {
		        			jpop.addSeparator();
		        			for (String []s : ss) {
		        				JMenuItem mi = new JMenuItem(s[0]);
		        				mi.setActionCommand(s[1]);
		        				mi.addActionListener(userActionListener);
		        				jpop.add(mi);
		     	            }
		     	        }
		             }
					jpop.show(SOSTable.this, e.getX(), e.getY());
				}
			}
			public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
		});
		TableColumnModel tcm = getColumnModel();
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
	public void setParamMenuBuilder(ParamMenuItemBuilder b) {
		paramMenuBuilder = b;
	}
	private ParamMenuItemBuilder paramMenuBuilder;
	
	public boolean isPaintingTitle() {
		return true;
	}
	public void removeTodo(ArrayList<PGmTodoItem> mails) {
		if (mails != null) {
			synchronized (mailProcessed) {
				for (PGmTodoItem m:  mails) {
					if (!showDoneMail) {
						mdl.todos.remove(m);
						mailProcessed.add(m);
					}
					gmStub.gameForm.main.todoTbl.removeTodo(gmStub.gameForm, m.mailId);
				}
			}
			mdl.fireTableDataChanged();
		}
	}
	boolean showDoneMail = false;
	public void updateMode() {
		boolean b = gmStub.config.getBooleanProperty("showProcessedGmsg"); 
		if (showDoneMail != b) {
			showDoneMail = b;
			if (b) {
				mdl.todos.addAll(mailProcessed);
			} else {
				for (int i = mdl.todos.size(); i-- > 0; ) {
					PGmTodoItem m = mdl.todos.get(i);
					if (m.status != PGmTodoItem.STAT_NEW) {
						mdl.todos.remove(m);	
					}
				}
			}
			mdl.fireTableDataChanged();
		}
	}

	public void addSos(PGmTodoItem t) {
		mdl.todos.add(t);
		this.doLayout();
		mdl.fireTableDataChanged();
	}
	public ArrayList<PGmTodoItem> getMail() {
		ArrayList<PGmTodoItem> ret = new ArrayList<PGmTodoItem>();
		ret.addAll(mdl.todos);
		if (!showDoneMail) {
			ret.addAll(mailProcessed);
		}
		return ret;
	}
	public PGmTodoItem getMail(int id) {
		for (PGmTodoItem m : getMail()) {
			if (m.mailId == id) {
				return m;
			}
		}
		return null;
	}
	public void addDoneSos(ArrayList<PGmTodoItem> t) {
		synchronized (mailProcessed) {
			mailProcessed.addAll(t);
		}
	}
	public void addSos(ArrayList<PGmTodoItem> t) {
		mdl.todos.addAll(t);
		this.sizeColumnsToFit(false);
		this.doLayout();
		mdl.fireTableDataChanged();
	}
	private static int [][]colSize = {
		{50, 60, 80}, // mail id
		{50, 60, 80}, // id
		{50, 80, 120}, // name
		{200, 300, 0}, // content
		{50, 60, 100}, // time
		{16, 40, 50}, // status
		{50, 80, 100}, // pos
	};
	public static class MyTableModel extends AbstractTableModel {
		Vector<PGmTodoItem> todos = new Vector<PGmTodoItem>();
		String columnNames[] = SOSTableRES.colNames;
		public String getColumnName(int col) {
	        return columnNames[col].toString();
	    }
	    public int getRowCount() { 
	    	return todos.size(); 
	    }
	    public int getColumnCount() { 
	    	return columnNames.length; 
	    }
	    public Object getValueAt(int row, int col) {
	    	if (row < todos.size()) {
	    		PGmTodoItem m = todos.get(row);
		    	switch (col) {
		    	case 0:
		    		return m.mailId;
		    	case 1:
		    		return m.sourceId;
		    	case 2:
		    		return m.author;
		    	case 3:
		    		return m.content;
		    	case 4:
		    		return m.getTime();
		    	case 5:
		    		return m.status == PGmTodoItem.STAT_DELETED ? SOSTableRES.statDel : m.status == PGmTodoItem.STAT_FINISHED ? 
		    				SOSTableRES.statDone: 
		    					SOSTableRES.statNew;
		    	case 6:
		    		return m.getPos();
		    	}
	    	}
	        return null;
	    }
	    public boolean isCellEditable(int row, int col) { 
	    	return false; 
	    }
	    public void setValueAt(Object value, int row, int col) {
	        todos.setElementAt((PGmTodoItem)value, row);
	        fireTableCellUpdated(row, col);
	    } 
	}

	private ActionListener userActionListener = new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
	        Object obj = e.getSource();
	        String cmd = e.getActionCommand();
	        gmStub.con.processCommand(cmd);
	    }
	};
}
