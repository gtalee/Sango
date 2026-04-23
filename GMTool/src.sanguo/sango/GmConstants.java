package sango;

/**
 * 三国客服客户端协议
 */

public interface GmConstants  {
	/**
	 * 聊天信息
	 * channel						byte 0 世界 1 国家 2 地区 3 同乡 4 帮派 5 队伍 6 私聊 7 系统(系统频道不可用，私聊需要加上对方Id，其他忽略)
	 * sourceId						int 
	 * name							string 
	 * message						string
	 * attachment					byte[] 如果是物品{01(byte),itemId,instanceId(int),name(string),showType(byte),quality(byte)},如果是任务{02{byte},questId(int,name(string)}
	 * 
	 */
	public static final short CHAT_SERVER = 202; // GmCmdChat
	/**
	 * ADMIN错误
	 * serial								int
	 * type									byte
	 * message								string
	 */
	public static final short ADMIN_ERROR = 1000; // xml
	/**
	 * ADMIN客户端登陆
	 * serial								int
	 * name									string
	 * password								string
	 */
	public static final short ADMIN_LOGIN_CLIENT = 1001; // GMLogin
//	
//	/**
//	 * ADMIN客户端登陆成功
//	 * serial								int
//	 */
//	public static final short ADMIN_LOGIN_SERVER = 1002; // done in xml
//	
//	/**
//	 * 查看当前玩家列表
//	 * serial								int
//	 * mapId								int(-1 代表所有玩家)
//	 */
//	public static final short ADMIN_WHO_CLIENT = 1003; // done in xml
//	
//	/**
//	 * 玩家列表
//	 * serial								int
//	 * count								short
//	 * 循环N次
//	 * 	玩家ID								int
//	 *  name								string
//	 *  性别									byte
//	 *  等级									byte
//	 *  职业									byte (0 武将 1 刺客 2 谋士 3 方士)
//	 *  阵营									byte (1 魏 2 蜀 3 吴)
//	 *  玩家mapId							int
//	 *  x									short
//	 *  y									short
//	 *  
//	 */
//	public static final short ADMIN_WHO_SERVER = 1004; // done in CmdWho
//	
//	/**
//	 * 取指定玩家信息
//	 * serial								int
//	 * id									int
//	 * name									string(优先name,如果name长度为0,那么根据id载入玩家) 
//	 */
//	public static final short ADMIN_PLAYER_INFO_CLIENT = 1005; // done in xml
//	
//	/**
//	 * 玩家信息
//	 * serial								int
//	 * 基本信息								byte[]
//	 * 背包信息								byte[]
//	 * 技能信息								byte[]
//	 */
//	public static final short ADMIN_PLAYER_INFO_SERVER = 1006; // done in playerInfo. need detail
//	
//	/**
//	 * 获取玩家帐号信息
//	 * serial								int
//	 * 角色ID								int
//	 */
//	public static final short ADMIN_ACCOUNT_INFO_CLIENT = 1007; // done in xml
//	
//	/**
//	 * 玩家帐号信息
//	 * serial								int
//	 * 帐号Id								int
//	 * 帐号名								string
//	 * 帐号密码								string
//	 * 电话									string
//	 */
//	public static final short ADMIN_ACCOUNT_INFO_SERVER = 1008; // done in xml
//	
//	/**
//	 * 玩家角色列表
//	 * serial								int
//	 * 帐号Id								int
//	 */
//	public static final short ADMIN_PLAYERLIST_CLIENT = 1009; // xml
//	
//	/**
//	 * 玩家角色列表
//	 * serial								int
//	 * 帐号Id								int
//	 * count								short
//	 * 循环N次
//	 * 	Id						int
//	 * 	名字						string
//	 * 	性别						byte
//	 * 	等级						byte
//	 *	职业						byte
//	 *	阵营						byte
//	 *  地图Id					short
//	 */
//	public static final short ADMIN_PLAYERLIST_SERVER = 1010; // xml
//	
//	
//	/**
//	 * 改变账号状态
//	 * serial									int
//	 * 帐号Id									int
//	 * 状态										int(0 封号 1 解封)
//	 * 信息										string
//	 */
//	public static final short ADMIN_ACCOUNT_STATUS_CLIENT = 1011; // xml
//	
//	/**
//	 * 封账号成功(因为此协议认证是没有返回的，所以这个包只能代表已经向认证服务器发送了请求)
//	 * serial									int
//	 */
//	public static final short ADMIN_ACCOUNT_STATUS_SERVER = 1012; // xml
//	
//	/**
//	 * 禁言/解封
//	 * serial									int
//	 * playerId									int
//	 * flag										int( 1 世界 1<<2 国家 1<<3 地图 1<<4 家乡 1<<5 私聊 0 解封)	
//	 * time										long
//	 */
//	public static final short ADMIN_CHAT_FORBID_CLIENT = 1013; // Mute
//	
//	/**
//	 * 禁言/解封成功
//	 * serial									int
//	 */
//	public static final short ADMIN_CHAT_FORBID_SERVER = 1014; // xml
//	
//	/**
//	 * 禁飞鸽/解禁
//	 * serial									int
//	 * playerId									int
//	 * time										int
//	 */
//	public static final short ADMIN_MAIL_FORBID_CLIENT = 1015; // mute
//	
//
//	
//	/**
//	 * 禁飞鸽/解禁成功
//	 * serial									int
//	 */
//	public static final short ADMIN_MAIL_FORBID_SERVER = 1016; // xml
//	
//	/**
//	 * 进入聊天频道
//	 * serial									int
//	 * channel									int (0 世界 1 国家 2 地区 3 家乡 4 帮派 7 系统)
//	 * targetId									int (如果是国家:1 魏 2 蜀 3 吴;如果是地区:mapId;如果是帮派:帮派id)
//	 * 家乡名称									string
//	 */
//	public static final short ADMIN_JOIN_CHATCHANNEL_CLIENT = 1016;  // GmCmdChat
//	
//	/**
//	 * 进入聊天频道成功
//	 * serial									int
//	 */
//	public static final short ADMIN_JOIN_CHATCHANNEL_SERVER = 1017; // xml
//	
//	
//	/**
//	 * 发送信息
//	 * channel									int
//	 * targetId									int
//	 * nativeString								string
//	 * message									string
//	 */
//	public static final short ADMIN_CHAT_CLIENT = 1018; // GmCmdChat
//	
//	
//	/**
//	 * 踢玩家
//	 * playerId									int
//	 * time										long (0 解除)
//	 */
//	public static final short ADMIN_KICK_CLIENT = 1019;  // Kick

	
	
	/**
	 * ADMIN客户端登陆成功
	 * serial								int
	 */
	public static final short ADMIN_LOGIN_SERVER = 1002;
	
	/**
	 * 查看当前玩家列表
	 * serial								int
	 * mapId								int(-1 代表所有玩家)
	 */
	public static final short ADMIN_WHO_CLIENT = 1003;
	
	/**
	 * 玩家列表
	 * serial								int
	 * count								short
	 * 循环N次
	 * 	玩家ID								int
	 *  name								string
	 *  性别									byte
	 *  等级									byte
	 *  职业									byte (0 武将 1 刺客 2 谋士 3 方士)
	 *  阵营									byte (1 魏 2 蜀 3 吴)
	 *  玩家mapId							int
	 *  x									short
	 *  y									short
	 *  帮派									string
	 *  
	 */
	public static final short ADMIN_WHO_SERVER = 1004;
	
	/**
	 * 取指定玩家信息
	 * serial								int
	 * id									int
	 * name									string(优先name,如果name长度为0,那么根据id载入玩家) 
	 */
	public static final short ADMIN_PLAYER_INFO_CLIENT = 1005;
	
	/**
	 * 玩家信息
	 * serial								int
	 * 基本信息								byte[]
	 * 背包信息								byte[]
	 * 技能信息								byte[]
	 */
	public static final short ADMIN_PLAYER_INFO_SERVER = 1006;
	
	/**
	 * 获取玩家帐号信息
	 * serial								int
	 * 角色ID								int
	 */
	public static final short ADMIN_ACCOUNT_INFO_CLIENT = 1007;
	
	/**
	 * 玩家帐号信息
	 * serial								int
	 * 帐号Id								int
	 * 帐号名								string
	 * 帐号密码								string
	 * 电话									string
	 */
	public static final short ADMIN_ACCOUNT_INFO_SERVER = 1008;
	
	/**
	 * 玩家角色列表
	 * serial								int
	 * 帐号Id								int
	 */
	public static final short ADMIN_PLAYERLIST_CLIENT = 1009;
	
	/**
	 * 玩家角色列表
	 * serial								int
	 * 帐号Id								int
	 * count								short
	 * 循环N次
	 * 	Id						int
	 * 	名字						string
	 * 	性别						byte
	 * 	等级						byte
	 *	职业						byte
	 *	阵营						byte
	 *  地图Id					short
	 */
	public static final short ADMIN_PLAYERLIST_SERVER = 1010;
	
	
	/**
	 * 改变账号状态
	 * serial									int
	 * 帐号Id									int
	 * 状态										int(0 封号 1 解封)
	 * 信息										string
	 */
	public static final short ADMIN_ACCOUNT_STATUS_CLIENT = 1011;
	
	/**
	 * 封账号成功(因为此协议认证是没有返回的，所以这个包只能代表已经向认证服务器发送了请求)
	 * serial									int
	 */
	public static final short ADMIN_ACCOUNT_STATUS_SERVER = 1012;
	
	/**
	 * 禁言/解封
	 * serial									int
	 * playerId									int
	 * flag										int( 1 世界 1<<1 国家 1<<2 地图 1<<3 家乡 1<<4 私聊 )	
	 * time										long (如果时间是0，那么就是解封)
	 */
	public static final short ADMIN_CHAT_FORBID_CLIENT = 1013;
	
	/**
	 * 禁言/解封成功
	 * serial									int
	 */
	public static final short ADMIN_CHAT_FORBID_SERVER = 1014; // xml
	
	/**
	 * 禁飞鸽/解禁
	 * serial									int
	 * playerId									int
	 * time										long(如果时间是0，那么就是解封)
	 */
	public static final short ADMIN_MAIL_FORBID_CLIENT = 1015; // Mute.MuteMail
	

	
	/**
	 * 禁飞鸽/解禁成功
	 * serial									int
	 */
	public static final short ADMIN_MAIL_FORBID_SERVER = 1016; // xml
	
	/**
	 * 进入聊天频道
	 * serial									int
	 * channel									int (0 世界 1 国家 2 地区 3 家乡 4 帮派 7 系统)
	 * targetId									int (如果是国家:1 魏 2 蜀 3 吴;如果是地区:mapId;如果是帮派:帮派id)
	 * 家乡名称									string
	 */
	public static final short ADMIN_JOIN_CHATCHANNEL_CLIENT = 1016; // GmCmdChat.GMPChatConfig  
	
	/**
	 * 进入聊天频道成功
	 * serial
	 */
	public static final short ADMIN_JOIN_CHATCHANNEL_SERVER = 1017; // xml
	
	/**
	 * 发送信息
	 * channel									int
	 * targetId									int
	 * nativeString								string
	 * message									string
	 */
	public static final short ADMIN_CHAT_CLIENT = 1018; // GmCmdChat.GmPChatMessage
	
	
	/**
	 * 踢玩家
	 * playerId									int
	 * time										long (0 解除)
	 */
	public static final short ADMIN_KICK_CLIENT = 1019;  // Kick.Package
	
	/**
	 * 获取GM请求列表
	 * serial									int
	 * 每页条数									short
	 * 页数										short
	 */
	public static final short ADMIN_GMREQUEST_LIST_CLIENT = 1022; // GmCmdMail
	/**
	 * GM请求列表
	 * serial									int
	 * 每页条数									short
	 * 页数										short
	 * 信件的总数								int
	 * 本页实际条数								short
	 * 循环N次
	 * 	请求Id						int
	 *  请求类型						byte(暂时都为0，以后分各种问题组)
	 * 	请求玩家Id					int
	 * 	请求玩家名					string
	 * 	请求内容						string
	 * 	请求状态						byte(0 未解决 1 解决)
	 * 	解决方案						string
	 * 	提交时间						long
	 *  玩家机型						string
	 *  玩家mapId					short
	 *  玩家x坐标					short
	 *  玩家y坐标					short
	 */
	public static final short ADMIN_GMREQUEST_LIST_SERVER = 1023; // GmCmdMail
	
	/**
	 * 解决GM请求
	 * 请求Id									int 
	 * 角色Id									int(如果为0，那么请求Id有效，如果不为0，那么角色Id有效)
	 * 解决邮件Title								string
	 * 解决方案									string
	 */
	public static final short ADMIN_GMREQUEST_SOLVE_CLIENT = 1024; // GmCmdMail
	
	/**
	 * 解决GM请求成功
	 * 请求Id									int
	 * 角色Id									int
	 * 解决GM名字								string
	 * 解决方案									string
	 */
	public static final short ADMIN_GMREQUEST_SOLVE_SERVER  = 1025; // GmCmdMail
	
	/**
	 * 删除GM请求
	 * 数量										short
	 * 循环N次
	 * 	请求Id									int
	 */
	public static final short ADMIN_GMREQUEST_DELETE_CLIENT = 1026; // GmCmdMail
	
	/**
	 * 删除GM请求成功
	 * 数量										short
	 * 循环N次
	 * 	请求Id									int
	 */
	public static final short ADMIN_GMREQUEST_DELETE_SERVER = 1027; // GmCmdMail
	
	/**
	 * 新增GM请求
	 * 	请求Id						int
	 * 	请求玩家Id					int
	 * 	请求玩家名					string
	 * 	请求内容						string
	 * 	请求状态						byte(0 未解决 1 解决)
	 * 	解决方案						string
	 * 	提交时间						long
	 */
	public static final short ADMIN_GMREQUEST_ADDED_SERVER = 1028;
	/**
	 * 发送信件
	 * serial									int
	 * playerId									int
	 * title									string
	 * content									string
	 * ItemId									int (如果-1那么就是金钱)
	 * 数量										int
	 * 是否强制绑定								byte(0 不强制绑定 1 强制绑定)
	 * 星级										byte
	 * 孔数										byte(新增的孔数，不包括初始的孔数)
	 */
	public static final short ADMIN_SENDMAIL_CLIENT = 1029;

	public static final short ADMIN_ACCOUNT_BIND_INFO_SERVER = 1041;
	
	
	
	/**
	 * 保存客服公告到xml文件
	 * serial                   int
	 * minLevel                 int                 等级下限
	 * maxLevel                 int                 等级上限
	 * textexplation            String            文字说明
	 * 循环N次
	 * size                     short             
	 *    	activeitem               String            活动名称
	 * 		detail                   String            更新具体内容
	 */
	public static final short ADMIN_SAVECLIENTBBS_CLIENT = 1116;
	
	/**
	 * 保存客服公告到xml文件成功
	 * serial                    int
	 */
	public static final short ADMIN_SAVECLIENTBBS_SERVER = 1117;

	/**
	 * 聊天消息发送成功
	 * serial                    int
	 */
	public static final short ADMIN_CHAT_SERVER = 1130;
	/**
	 * 批量标注GM请求已被处理
	 * serial									int
	 * 请求Ids									int[] 
	 */
	public static final short ADMIN_MULTIGMREQUEST_SOLVE_CLIENT = 1131;
	
	/**
	 * 批量标注GM请求已被处理返回
	 * serial									int
	 * 成功标注的请求ids							int[]
	 */
	public static final short ADMIN_MULTIGMREQUEST_SOLVE_SERVER = 1132;

}
