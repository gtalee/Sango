package sango.gm.cmd;

public interface CmdKickRES {
	String minuteFull = "分钟";
	String minute = "分";
	String hour = "时";
	String hourFull = "小时";
	String day = "天";
	String kickMsg = "将 {0} 踢下线 {1}";
	String muteMsg = "将 {0} 禁言 {1}";
	String cmdName = "踢玩家";
	String cmdDesc = "kick 角色ID 时间 // 时间可以是小时，分钟，或天。如12小时（缺省是分钟）,为0则";
	String muteName = "禁言";
	String muteDesc = "mute 角色ID 频道 时间 //频道为 世国场籍私信 之一。时间可以是小时，分钟，或天。如12小时（缺省是分钟）";
}
