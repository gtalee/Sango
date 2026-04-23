package sango.gm.cmd;

public interface CmdWhoRES {
	String err1 = "没有找到玩家,不能侦听其所在场景";
	String errPara = "非法的参数\n";
	String list = "列表:";
	String totalNum = "共{0}人";
	String numPercent = "\n当前在线{0}人,(查询命中比例{1})";
	String cmdName = "查询在线玩家";
	String cmdDesc = "  设置静止玩家比较点 /search rec\n  查询静止玩家列表　 /search idle";
	String totalOnline = "当前在线共 {0} 人。";
	String onlineCountry = "\n    阵营分布: 魏国{0}人({1})   蜀国{2}人({3})   吴国{4}人({5})";
	String onlineGender =  "\n    性别分布: 男{0}人({1})  女{2}人({3})";
	String onlineCareer =  "\n    职业分布: 武将{0}人({1}) 刺客{2}人({3}) 谋士{4}人({5}) 方士{6}人({7})";
	String onlineLevel =   "\n    级别段分布: ";
	String onlineLevelData = "{0}~{1}级{2}人({3}) ";
	String onlineTopScenes =   "\n    密集场景分布: ";
	String onlineTopLevel =   "\n    密集级别分布: ";
	String onlineTopSceneData = "\n      {0}: {1}人({2}) {3}";
	String onlineTopLevelData = "\n      {0}级: {1}人({2}) {3}";
	String allScene = "所有场景";
	String playerName = "角色名称";
	String playerNameTip = "输入匹配正则表达式，（[其中之一]^头$尾{次数}?可有+多次）.如：\"^[梅煤]川内?[库酷]\"";
	String idRange = "ID区间";
	String levelRange = "级别区间";
	String gangName = "帮派名称";
	String gangNameTip = "输入匹配正则表达式，（[其中之一]^头$尾{次数}?可有+多次）.如：\"龙[傲敖]轩\"";
	String scenePos = "所在场景";
	String hideOption = "隐藏查询选项";
	String hidOptionTip = "隐藏本敞口，随时可以通过查询命令再次打开本窗口。";
	String search = "查询";
	String searchTip = "查询结果将在输出终端上显示。";
}
