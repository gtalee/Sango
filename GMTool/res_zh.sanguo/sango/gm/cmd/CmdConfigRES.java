package sango.gm.cmd;

public interface CmdConfigRES {
	String serverSetSucceed = "成功将连接服务器设置为: socket:{0}:{1}";
	String envSetSucceed = "成功设置环境变量:{0}";
	String envNameErr = "环境变量名称错误:{0}";
	String noEnv ="没有环境变量:{0}";
	String envListOk = "环境变量:{0}=[{1}]";
	String cmdName = "环境配置";
	String description = "  设置本窗口标题 /set title &lt;标题&gt; \n" +
	    "  设置连接服务器  /set server &lt;主机名称或IP&gt;:&lt;端口&gt;\n" +
	    "  设置快捷转义文本 /set shortcut [&lt;快捷或转义文本&gt;] \n" +
	    "                快捷文本是在命令输入栏内通过Ctrl-回车能弹出的方便输入的文本\n" +
	    "                转义文本是形式同“\\pip;”的转义标示。可用“set shortcut \\\\pip;掌上明珠技术有限公司”定义\n" +
	    "                转义文本定义后，在命令输入栏内输入“\\pip;”后将会用“掌上明珠技术有限公司”替换\n" +
	    "" +
	    "";
}
