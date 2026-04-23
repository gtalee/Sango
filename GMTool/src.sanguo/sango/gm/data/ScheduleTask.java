package sango.gm.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import cwu.util.DebugUtil;

public class ScheduleTask {
    public static int SERNUM = 0;
    public Calendar alertTime;
    public int loopTimes;
    public long loopDeltaTime;
    public String [][]commands;
    public int chances[];
    public int id;
    public String name;

    static Random r = new Random();

    // start time,
    public String getInfo() {
        return "" + id + ". " + name + " " + DebugUtil.getDate(alertTime.getTime()) +
                " x " + loopTimes;
    }
    public String[] getCommand() {
        if (commands != null && commands.length > 0) {
            int k = r.nextInt();
            if (k < 0) {
                k = -k;
            }
            return commands[k % commands.length];
        }
        return null;
    }
    public ScheduleTask(String s[], int pos) {
        this.id = SERNUM++;
        name = s[pos++];
        alertTime = parseToDate(s[pos++]);

        setRepeat(s[pos++]);
        ArrayList<ArrayList<String>> lst = new ArrayList<ArrayList<String>>();
        ArrayList<String> subLst = new ArrayList<String>();
        while (pos < s.length) {
            if (s[pos].equals("|")) {
                if (subLst.size() > 0) {
                    lst.add(subLst);
                    subLst = new ArrayList<String>();
                }
            } else {
                subLst.add(s[pos]);
            }
            pos++;
        }
        if (subLst.size() > 0) {
            lst.add(subLst);
        }
        commands = new String[lst.size()][];
        for (int i = commands.length - 1; i >= 0; i--) {
            subLst = lst.get(i);
            commands[i] = new String[subLst.size()];
            for (int j = commands[i].length - 1; j >= 0; j--) {
                commands[i][j] = subLst.get(j);
            }
        }
    }
    public void next() {
        if (loopTimes > 0) {
            loopTimes--;
            alertTime.add(Calendar.MILLISECOND, (int)loopDeltaTime);
        }
    }

    public void setRepeat(String s) {
        if (s.startsWith("-")) {
            loopTimes = 1;
            return;
        }
        int k = s.indexOf("-");
        if (k > 0) {
            loopDeltaTime = parseToDuration(s.substring(0, k));
            Calendar endTime = parseToDate(s.substring(k + 1));
            long k1 = endTime.getTimeInMillis();
            long k2 = alertTime.getTimeInMillis();
            loopTimes = (int) ((k2 - k1) / loopDeltaTime);
            if (loopTimes < 1) {
                loopTimes = 1;
            }
            return;
        }
        k = s.indexOf("*");
        if (k > 0) {
            loopDeltaTime = parseToDuration(s.substring(0, k));
            loopTimes = Integer.parseInt(s.substring(k + 1));
            if (loopTimes < 1) {
                loopTimes = 1;
            }
            return;
        }
        loopDeltaTime = parseToDuration(s);
        loopTimes = -1;
    }
    public long parseToDuration(String dStr) {
        String[] s = dStr.split("/");
        int i = s.length - 1;
        long ret = 0;
        if (i >= 0) {
            try {
                ret = Long.parseLong(s[i]) * 1000L;
            } catch (NumberFormatException ex) {
            }
            i--;
        }
        if (i >= 0) {
            try {
                ret += Long.parseLong(s[i]) * 1000L * 60L;
            } catch (NumberFormatException ex) {
            }
            i--;
        }
        if (i >= 0) {
            try {
                ret += Long.parseLong(s[i]) * 1000L * 60L * 60L;
            } catch (NumberFormatException ex) {
            }
            i--;
        }
        if (i >= 0) {
            try {
                ret += Long.parseLong(s[i]) * 1000L * 60L * 60L * 24L;
            } catch (NumberFormatException ex) {
            }
            i--;
        }
        return ret;
    }
    public static Calendar parseToDate(String dStr) {
        Calendar c = Calendar.getInstance();
        if (dStr.equals("-")) {
            return c;
        }
        String[] s = dStr.split("/");
        int i = s.length - 1;
        if (i >= 0) {
            try {
                c.set(Calendar.SECOND, Integer.parseInt(s[i]));
            } catch (NumberFormatException ex) {
            }
            i--;
        }
        if (i >= 0) {
            try {
                c.set(Calendar.MINUTE, Integer.parseInt(s[i]));
            } catch (NumberFormatException ex) {
            }
            i--;
        }
        if (i >= 0) {
            try {
                c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(s[i]));
            } catch (NumberFormatException ex) {
            }
            i--;
        }
        if (i >= 0) {
            try {
                c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(s[i]));
            } catch (NumberFormatException ex) {
            }
            i--;
        }
        if (i >= 0) {
            try {
                c.set(Calendar.MONTH, Integer.parseInt(s[i]) - 1);
            } catch (NumberFormatException ex) {
            }
            i--;
        }
        if (i >= 0) {
            try {
                c.set(Calendar.YEAR, Integer.parseInt(s[i]));
            } catch (NumberFormatException ex) {
            }
            i--;
        }
        return c;
    }
}
