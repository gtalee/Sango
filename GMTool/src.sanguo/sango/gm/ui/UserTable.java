package sango.gm.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import pip.util.ui.RichConsole.ParamMenuItemBuilder;
import sango.data.POnlinePlayer;
import sango.gm.GmStub;

/** 
 * 玩家的列表. 用来显示当前在线玩家的基本信息。可以通过玩家ID对玩家进行指定的操作。
 */

public class UserTable extends JTable {
	/** 游戏服务器实例 */
	GmStub client;
	/** 玩家表格的控制 */
	MyUserTableModel mdl = null;
	/** 表格中列宽的限制 */
	static final int widthOfCols[][] = {
		{48, 60, 120}, // ID
		{80, 100, 200}, // 角色名
		{24, 30, 60}, // 级别 
		{48, 60, 120}, // 位置
		{80, 100, 200}, // 帮派
		{20, 30, 60}, // 阵营
	};
	/** 针对玩家的操作通过弹出菜单实现 */
	JPopupMenu popPlayerAction;
	private ParamMenuItemBuilder paramMenuBuilder;

	public UserTable(GmStub client) {
		super(new MyUserTableModel());
		this.client = client;
		mdl = (MyUserTableModel)getModel();
		setAutoCreateRowSorter(true);
		// 设置表格的列宽
		TableColumnModel tcm = getColumnModel();
		for (int i = widthOfCols.length; i--> 0; ) {
    		TableColumn clm = tcm.getColumn(i);
    		if (widthOfCols[i][0] > 0) {
    			clm.setMinWidth(widthOfCols[i][0]);
    		}
    		if (widthOfCols[i][1] > 0) {
    			clm.setPreferredWidth(widthOfCols[i][1]);
    		}
    		if (widthOfCols[i][2] > 0) {
    			clm.setMaxWidth(widthOfCols[i][2]);
    		}
		}
		popPlayerAction = new JPopupMenu();
		addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					java.awt.Point p = e.getPoint();
			        int rowIndex = rowAtPoint(p);
			        if (rowIndex >= 0 && rowIndex <mdl.todos.size()) {
			        	rowIndex = convertRowIndexToModel(rowIndex);
			        	popupForUser(mdl.todos.get(rowIndex), e.getX(), e.getY());
			        }
				}
			}
			public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
		});
		
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
	}
	/** 设置以玩家ID为参数的菜单项制作者 */
	public void setParamMenuBuilder(ParamMenuItemBuilder b) {
		paramMenuBuilder = b;
	}

	private void popupForUser(POnlinePlayer m, int x, int y) {
        popPlayerAction.removeAll();
        boolean has = false;
        if (paramMenuBuilder != null) {
        	ArrayList<String[]> ss = paramMenuBuilder.genMenu(new String[]{String.valueOf(m.getId()), m.getName()});
        	if (ss.size() > 0) {
        		if (has) {
        			popPlayerAction.addSeparator();
	        	}
	            for (String []s : ss) {
	            	JMenuItem mi = new JMenuItem(s[0]);
	            	mi.setActionCommand(s[1]);
	            	mi.addActionListener(userActionListener);
	            	popPlayerAction.add(mi);
	            }
	            has = true;
	        }
        }
        if (popPlayerAction.getComponentCount() > 0) {
            popPlayerAction.show(this, x+1, y+1);
        }
    }
	public boolean isPaintingTitle() {
		return true;
	}
	/** 根据角色ID取得角色数据 */
	public synchronized POnlinePlayer getUser(int id) {
		for (POnlinePlayer u : mdl.todos) {
			if (u.id == id) {
				return u;
			}
		}
		return null;
	}
	public int currentNumUsersOnline = 0;
	public int getNumUsersOnline() {
		return currentNumUsersOnline;
	}
	/** 取得当前所有玩家的数据 */
	public synchronized ArrayList<POnlinePlayer> getUsers() {
		ArrayList<POnlinePlayer> savedUsers = new ArrayList<POnlinePlayer>();
		savedUsers.addAll(mdl.todos);
		return savedUsers;
	}
	/** 更新在线玩家数据 */
	public synchronized void syncUsers( POnlinePlayer[] users) {
		mdl.todos.removeAllElements();
		for (POnlinePlayer u : users) {
			mdl.todos.add(u);
		}
		mdl.fireTableDataChanged();
		client.gameForm.main.updateServeListState();
		currentNumUsersOnline = mdl.todos.size();
	}
	
	public static class MyUserTableModel extends AbstractTableModel {
		Vector<POnlinePlayer> todos = new Vector<POnlinePlayer>();
		String columnNames[] = SOSTableRES.userTableColNames;
		
		public String getColumnName(int col) {
	        return columnNames[col].toString();
	    }
	    public Class<?> getColumnClass(int columnIndex) {
	    	if (columnIndex ==1 || columnIndex == 4) {
	    		return String.class;
	    	}
	    	return Integer.class;
	    }

	    public int getRowCount() { 
	    	return todos.size(); 
	    }
	    public int getColumnCount() { 
	    	return columnNames.length; 
	    }
	    public Object getValueAt(int row, int col) {
	    	if (row < todos.size()) {
	    		POnlinePlayer t = todos.get(row);
	    		switch (col) {
		    	case 0:
		    		return new Integer(t.id);
		    	case 1:
		    		return t.name;
		    	case 2:
		    		return new Integer(t.level);
		    	case 3:
		    		return t.stageId + "[" + t.sceneName + "]";
		    	case 4:
		    		return t.tongName;
		    	case 5:
		    		return t.getCountry();
		    	}
	    	}
	    	return null;
	    }
	    public boolean isCellEditable(int row, int col) { 
	    	return false; 
	    }
	    public void setValueAt(Object value, int row, int col) {
	        todos.setElementAt((POnlinePlayer)value, row);
	        fireTableCellUpdated(row, col);
	    } 
	}
	/** 玩家操作的响应部分 */
	private ActionListener userActionListener = new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
	        String cmd = e.getActionCommand();
	        client.con.processCommand(cmd);
	    }
	};

}
