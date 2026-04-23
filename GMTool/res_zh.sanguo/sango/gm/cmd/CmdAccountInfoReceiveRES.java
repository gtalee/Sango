package sango.gm.cmd;

public interface CmdAccountInfoReceiveRES {
	public String info = "帐号:<action title=\"查看本帐号角色信息\" command=\"showplayers {0}\">" +
		"<action title=\"查看本帐号密保信息\" command=\"bindinfo {0}\">{0}[{1}]</action></action> 电话:[{3}]"; 
	public String superInfo = "帐号:<action title=\"查看本帐号角色信息\" command=\"showplayers {0}\">" +
		"<action title=\"查看本帐号密保信息\" command=\"bindinfo {0}\">{0}[{1}]</action></action> 密码:[{2}] 电话:[{3}]"; 
}
