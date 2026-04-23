package pip.util;

import java.util.*;
import java.io.*;

import com.f1j.ss.*;
import com.f1j.util.*;

/**
 * FormulaOne µÄ°ü×°
 */
public class Excel {
	private static Boolean hasF1;
	public static boolean hasF1() {
		if (hasF1 == null) {
			Class kls = null;
			try {
				kls = Class.forName("com.f1j.ss.Sheet");
			} catch (ClassNotFoundException e) {
			}
			if (kls != null) {
				hasF1 = Boolean.TRUE;
			} else {
				hasF1 = Boolean.FALSE;
			}
		}
		return hasF1.booleanValue();
	}

    /** the inner class to record the sheet information of the book */
    public class SheetRec {
        /** the sheet name */
        public String sheetName;
        /** the sheet instance */
        public Sheet sheet;
        /** the sheet number in the book */
        public int sheetNum;

        /** records all the column names of the sheet */
        public HashMap colNames = new HashMap();
        /** records all the row names of this sheet */
        public HashMap rowNames = new HashMap();
        /** the title row number in this sheet */
        public int tRow = 0;
        /** the title col number in this sheet */
        public int tCol = 0;
    }

    /** the current sheet structure */
    public SheetRec current = null;

    /** the excel book, root element */
    public Book book;
    /** the excel file name */
    String bookName;

    /**
     * wile reference cell via its alias, if to append new column
     * while the alias can not be found.
     */
    public boolean colExpandable = true;
    /**
     * wile reference cell via its alias, if to append new line
     * while the alias can not be found.
     */
    public boolean rowExpandable = true;

    /** records all the sheet record name of the opened book via index */
    ArrayList allSheets = new ArrayList();
    /** records all the sheet record name of the opened book via name */
    HashMap sheetNames = new HashMap();

    /** the alignment right attribute */
    public static CellFormat right = new CellFormat();

    static {
        try {
            right.setHorizontalAlignment(Format.eHorizontalAlignmentRight);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructor without parameters, it will create a new excel file
     * with one sheet in it.
     */
    public Excel() {
        this(1);
    }

    /**
     * Constructor with the number of the sheet specified.
     * @param n the number of the sheets
     */
    public Excel(int n) {
        try {
            // bind a temparory file name for the created file
            File f = File.createTempFile("Unamed", "xls");
            bookName = f.getName();
            // need not to really creat it.
            f.delete();

            book = new BookImpl();
            book.initBook();
            // set the sheet number
            book.setNumSheets(n);

            // get every sheet names and fill the structures
            for (int i = 0; i < n; i++) {
                getSheet(i);
            }
            // set sheet 0 as the current sheet.
            getSheet(0);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructor with the excel file name specified.
     * @param name the excel file name
     */
    public Excel(String name) {
        try {
            book = new BookImpl();
            // read the book from file
            book.read(name);
            bookName = name;
            int n = book.getSheetCount();

            // get every sheet names and fill the structures
            for (int i = 0; i < n; i++) {
                getSheet(i);
            }
            // set the sheet 0 as the current opened sheet
            getSheet(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructor with excel stream.
     * @param in the stream of the excel file
     */
    public Excel(InputStream in) {
        try {
            // bind a temparory file name for the created file
            File f = File.createTempFile("Unamed", "xls");
            bookName = f.getName();
            f.delete();

            book = new BookImpl();
            // read the book from steam
            book.read(in);
            int n = book.getSheetCount();

            // get every sheet names and fill the structures
            for (int i = 0; i < n; i++) {
                getSheet(i);
            }
            // set the sheet 0 as the current opened sheet
            getSheet(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * close the book and release the memory. After this invocation
     * this excel can not be accessed.
     */
    public void close() {
        book = null;
        bookName = null;
        current = null;
        sheetNames.clear();
        allSheets.clear();
    }

    /**
     * get the a specified sheet on the book.
     * @param n the sheet number.
     * @return the real sheet matched the number n
     *   null: if the sheet number bigger than that of the book has.
     */
    public Sheet getSheet(int n) {
        // if the book has no enough sheets
        if (n >= book.getSheetCount()) {
            return null;
        }

        // if the current is not the request
        if (current == null || current.sheetNum != n) {
            if (n < allSheets.size()) {
                current = (SheetRec)allSheets.get(n);
            } else {
                // load all the necessary sheets
                for (int i = allSheets.size(); i <= n; i++) {
                    current = new SheetRec();
                    current.sheet = book.getSheet(i);
                    current.sheetName = current.sheet.getName();
                    current.sheetNum = i;
                    sheetNames.put(current.sheetName, current);
                    allSheets.add(current);
                }
            }
        }
        return current.sheet;
    }

    /**
     * get the a specified sheet on the book.
     * @param n the sheet name.
     * @return the real sheet matched the name n
     *   null: if the sheet does not exist.
     */
    public Sheet getSheet(String n) {
        // get the sheet from catch (ArrayList) first
        if (current == null || !n.equals(current.sheetName)) {
            current = (SheetRec)sheetNames.get(n);
        }

        // if the current is not registered.
        if (current == null) {
            if (allSheets.size() < book.getSheetCount()) {
                // load all the necessary sheets
                for (int i = allSheets.size(); i <= book.getSheetCount(); i++) {
                    current = new SheetRec();
                    current.sheet = book.getSheet(i);
                    current.sheetName = current.sheet.getName();
                    current.sheetNum = i;
                    sheetNames.put(current.sheetName, current);
                    allSheets.add(current);
                    if (n.equals(current.sheetName)) {
                        return current.sheet;
                    }
                }
            }
            return null;
        }
        return current.sheet;
    }
    public void setSheetName(String name) {
    	if (current != null) {
    		try {
				current.sheet.setName(name);
	    		current.sheetName = name;
	    		sheetNames.put(current.sheetName, current);
			} catch (F1Exception e) {
				e.printStackTrace();
			}
    	}
    }

    /**
     * To save the current excel file.
     *
     * If the book name is not specified, or the current sheet is null,
     * this method will do nothing.
     */
    public void save() throws Exception {
        if (bookName != null && current != null) {
            //try {
                book.write(current.sheet, bookName, book.eFileExcel97);
            //} catch(Exception e) {
            //    Msg.showThrowable(e);
            //}
        }
    }
    /**
     * Save with new book name.
     * @param fileName the changed file name.
     */
    public void saveAs(String fileName) throws Exception {
        bookName = fileName;
        save();
    }

    /**
     * Set the alias (title) starting position.
     * @param i the start line of the title
     * @param j the start col of the title
     */
    public void setTitleStart(int i, int j) {
        current.tRow = i;
        current.tCol = j;
    }

    /**
     * Set the cell's string value.
     *
     * If the column alias does not exist and the colExpandable is false,
     * this method will do nothing. But if the colExpandable is true, it will
     * append a new column.
     *
     * @param row the line number of the current sheet (from 0)
     * @param col the title of the cell
     * @see colExpandable
     */
    public void setText(int row, String col, String val) {
        try {
            int i = getCol(col);
            if (i >= 0) {
                current.sheet.setText(row + current.tRow, i, val);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setText(int row, int col, String val) {
        try {
        	current.sheet.setText(row + current.tRow, col + current.tCol, val);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Calculate the column index in excel format, such as A, B, ..., BA, ..
     * @param n the column number
     * @return the 26 radix string with AB...Z
     */
    public static String getColName(int n) {
        String s = "";
        if (n == 0) {
            return "A";
        } else {
            while (n > 0) {
                int k = n % 26;
                s = "" + ((char)('A' + k)) + s;
                n -= k;
            }
        }
        return s;
    }

    /**
     * Get the absolute column number of the title col.
     * @param col the column alias (title), if the col is empty string (not null)
     *    then the first empty column number will be returned.
     * @return the absolute column number
     *   -1 if the alias does not found.
     */
    public int getCol(String col) {
        try {
            if (col != null) {
                col = col.trim();
                // find from catch first
                Integer off = (Integer)current.colNames.get(col);
                if (off == null) {
                    int i = 0;

                    // expand the catch
                    while (true) {
                        String s = current.sheet.getText(current.tRow, current.tCol + i);
                        if (s == null || s.length() == 0) {
                            if (colExpandable) {
                                // expand only if the col is not empty
                                if (col.length() > 0) {
                                    current.sheet.setText(current.tRow, current.tCol + i, col);
                                } else {
                                    return i + current.tCol;
                                }
                            } else {
                                // expand only if the col is not empty
                                if (col.length() > 0) {
                                    return -1;
                                } else {
                                    return i + current.tCol;
                                }
                            }
                            break;
                        } else if (s.trim().equals(col)) {
                            break;
                        }
                        i++;
                    }
                    off = new Integer(i);
                    current.colNames.put(col, off);
                }
                return current.tCol + off.intValue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Get the absolute line number of the alias row.
     * @param row the line alias (title), if the alias is empty string (not null)
     *   then the first empty line will be returned.
     * @return the absolute line number
     *   -1 if the alias has not been found.
     */
    public int getRow(String row) {
        try {
            if (row != null) {
                // find from catch (array list) first.
                Integer off = (Integer)current.rowNames.get(row.trim());
                if (off == null) {
                    int i = 0;
                    // expand the catch
                    while (true) {
                        String s = current.sheet.getText(current.tRow + i, current.tCol);
                        if (s == null || s.length() == 0) {
                            if (rowExpandable) {
                                if (row.length() > 0) {
                                    current.sheet.setText(current.tRow + i, current.tCol, row);
                                } else {
                                    return current.tRow + i;
                                }
                            } else {
                                if (row.length() > 0) {
                                    return -1;
                                } else {
                                    return current.tRow + i;
                                }
                            }
                            break;
                        } else if (s.trim().equals(row.trim())) {
                            break;
                        }
                        i++;
                    }
                    off = new Integer(i);
                    current.rowNames.put(row, off);
                }
                return current.tRow + off.intValue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Set the cell formula with line number and column alias
     *
     * If the column alias does not exist and the colExpandable is false,
     * this method will do nothing. But if the colExpandable is true, it will
     * append a new column.
     *
     * @param row the line number
     * @param col the column alias
     * @param val the formula
     */
    public void setFormula(int row, String col, String val) {
        try {
            int i = getCol(col);
            if (i >= 0) {
                current.sheet.setFormula(row + current.tRow, i, val);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Set the cell format with line number and column alias
     *
     * If the column alias does not exist and the colExpandable is false,
     * this method will do nothing. But if the colExpandable is true, it will
     * append a new column.
     *
     * @param row the line number
     * @param col the column alias
     * @param fmt the format
     */
    public void setFormat(int row, String col, CellFormat fmt) {
        setFormat(row, getCol(col), fmt);
    }
    /**
     * Set the cell format with line number and column alias
     *
     * If the line number or col number is little than 0, it will do nothing.
     *
     * @param row the line number
     * @param col the column number
     * @param fmt the format
     */
    public void setFormat(int row, int col, CellFormat fmt) {
        try {
            if (row >= 0 && col >= 0) {
                fmt.setCellFormats(current.sheet, row, col, row, col);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}