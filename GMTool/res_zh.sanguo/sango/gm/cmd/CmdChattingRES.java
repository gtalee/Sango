package sango.gm.cmd;

public interface CmdChattingRES {
	char channelCodes[] = {'世', '国', '区', '乡', '帮', '队', '私', '系'};
	String channelCode = String.valueOf(channelCodes);
	String countries[] = {"魏", "蜀", "吴"};
	String taskInfo = "[任务{0}:{1}]"; // 0.task-id, 1.task-title
	String tooManyWin = "最多只能同时回复四个玩家";
	String alreadyOpened = "此玩家聊天室已经打开";
	String normalPlayerOnly = "只能同普通玩家开聊天室";
	String authFail = "您没有广播的权限";
	String teamAuthFail = "您没有队伍广播的权限";
	String gangAuthFail = "您没有帮派广播的权限";
	String chatFail = "您没有私聊的权限";
	String noteam = "您没有选定一个队伍";
	String noArea = "您需要指定一个区域";
	String noGang = "您没有选定一个帮派";
	String overhearAuthFail = "您没有此项权限";
	String playerNotInList = "选中的玩家没在列表中";
	String wrongPara = "非法的参数\n";
	String broadcastMsg = "{0}(你)广播：【{1}】";
	String chatMsg = "{0}(你) 对[{1}]说：【{2}】";
	String cmdName = "消息";
	String cmdDesc = "  发布广播消息 /m b ＜广播内容＞\n" +
					"  私聊   /m t ＜玩家id＞ ＜聊天内容＞\n" +
			"  系统广播   /m s ＜系统广播内容＞\n";
	String overhearCmdDEsc = "  城聊   /m c ＜场景id＞ ＜聊天内容＞\n" +
		"  窃听   /m l [＜频道＞] <id> // id 为0关闭，频道取 \"区帮\"等.\n";
	String onGotGmMsg = "收到GM聊天信息：{0}";
}
