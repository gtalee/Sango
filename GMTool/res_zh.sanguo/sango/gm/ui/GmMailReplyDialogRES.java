package sango.gm.ui;

public interface GmMailReplyDialogRES {
	String id = "流水号";
	String time = "提交时间";
	String helpMsg = "求助内容";
	String pos = "位置";
	String device = "机型";
	String inProcessing = "您提交的问题已经在处理当中";
	String inProcessingContent = "您提交的问题已经在处理当中。感谢您对掌上明珠游戏的大力支持。";
	String playerId = "玩家ID";
	String content = "内容";
	String detail = "GM求助信息详情";
	String samePlayer = "同角色相关求助";
	String title = "标题";
	String qa = "答疑";
	String markAll = "全部标注已处理";
	String markAllTip = "本次回复包括列表中所有求助信息，他们都将被标注为已经处理。";
	String quitAfter = "回复后退出";
	String quitAfterTip = "回复求助信息后退出本求助界面，退到GM工具主窗口。";
	String reply = "回复";
	String replyTip = "回复选中的玩家求助，然后退出本界面，回到GM工具主窗口";
	String cancel = "取消";
	String cancelTip = "直接返回GM工具主窗口，不处理这些求助信息。";
	String fullScr = "切换全屏";
	String fullScrTip = "切换本窗口的全屏幕显示模式";

	String samePlayerDone = "近期处理此角色求助信息";
	String[] doneTitles = {"时间", "求助内容", "GM", "动作", "处理意见"};
}
