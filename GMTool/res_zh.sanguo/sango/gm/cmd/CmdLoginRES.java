package sango.gm.cmd;

public interface CmdLoginRES {
	String accountErr = "帐号不存在或密码错误";
	String wrongPara = "非法的参数\n";
	String cmdName = "GM管理者登陆";
	String loginSucceed = "游戏在线管理员登录成功";
	String cmdDesc = "  登陆     /gmlogin ＜用户名＞ ＜密码＞ [＜主机＞ ＜端口＞]  \n  退出登录　/gmlogin off";
	String serverId = "服务器ID";
}
