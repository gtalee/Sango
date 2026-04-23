package sango.gm.cmd;

public interface CmdGmHelpRES {
	String noHelpMsg =  "没有取得求助信息，无法保存";
	String titles[] = {"流水号","玩家ID","角色名称","内容","位置","时间","状态"};
	String deleted = "已删除";
	String processed = "已处理";
	String raw = "未处理";
	String savedToFile = "当前求助信息已经保存到{0}";
	String noDelAuth = "您没有权限删除求助信息";
	String cmdName = "GM求救热线消息";
	String cmdDesc = "  获得GM消息 /gmsg get\n" +
		"  获得指定日期的GM消息 /gmsg get ＜日期＞ // 日期的格式为 yyyy-MM-dd\n" +
		"  发送GM消息 /gmsg reply ＜流水号＞ ＜玩家id＞＜标题＞ ＜邮件内容＞ \n" +
	    "  删除GM消息 /gmsg del ＜消息序号＞*\n" +
	    "  保存GM消息 /gmsg save ＜文件名＞\n" +
	    "";
	String process = "处理";
	String processKick = "踢下线";
	String issueRaise = "举报";
	String dateFormatShouldBe = "日期格式应该为： \"yyyy-MM-dd\", 例如:2011-06-27";
	String markMail = "标注GM请求已经处理";
}
