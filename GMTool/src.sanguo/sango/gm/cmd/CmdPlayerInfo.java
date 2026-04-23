package sango.gm.cmd;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.mina.common.ByteBuffer;
import pip.gm.fw.GmFunction;
import pip.gm.fw.IMessage;
import pip.gm.fw.PDProcessor;
import pip.io.uwap.PDataFactory;
import pip.io.uwap.UAData;
import pip.io.uwap.UWapData;
import pip.util.Res;
import pip.util.StringUtil;
import sango.GmConstants;
import pip.gm.fw.*;
import pip.util.*;

public class CmdPlayerInfo  extends GmFunction {
    public static XmlResourceBundle xmlRes = XmlResourceBundle.getRes("/sango/gm/cmd/CmdPlayerInfo.xml");
	public void registerPackage(PDataFactory factory) {
		factory.register((int)GmConstants.ADMIN_PLAYER_INFO_SERVER, PPlayerDataReceived.class);
	}
	public PDProcessor getPackageProcessor() {
		return new PDProcessor() {
			public boolean process(pip.gm.fw.AbstractClient master, UWapData data) {
				if (data instanceof PPlayerDataReceived) {
					PPlayerDataReceived d = (PPlayerDataReceived)data;
					StringBuilder buf = d.genInfo(new StringBuilder());
					master.onMessage(IMessage.MSG_TYPE_LOG, buf.toString(), null);
				} else {
					return false;
				}
				return true;
			}
		};
	}
	public long getAuth() {
		return -1;
	}

	/** 角色属性信息。
	 *  需要同步服务器代码：peony.game.admin.AdminPlayerInfoCall.callFinish()中组包部分。
	 */
    public static class PPlayerDataReceived extends UAData implements GmConstants {
		public int getAppDataType() {
			return ADMIN_PLAYER_INFO_SERVER;
		}
		public int serialNum;
		
		public byte[] baseData;
		public BaseData base;

		public byte[] bagData;
		public BagData bag;
		
		public byte[] skillData;
		public SkillData skills;

		public HorseBag horseBag;
		public int weekCredit;
		
		public byte[] depotData;
		public BagData depot;

		public byte[] attendantBagData;
		public AttendantBagData attendantBag;
		
//		public void read(ByteBuffer data) throws Exception {
//			String s[] = getProperties();
////    		super.read(data);
//    		for (int i = 0; i < s.length; i++) {
//    			try {
//    				System.out.println("Reading: " + s[i]);
//    				readField(data, s[i]);
//    			} catch (Exception e) {
//    				e.printStackTrace();
//    				throw new Exception("读取属性 " + s[i] + " 异常:" + e.getMessage() + "[" + reportBuffer("", data) + "]");
//    			}
//    		}
//    		System.out.println(reportBuffer("LeftData", data));
//    	}
		public String[] getProperties() {
			return new String[] { "serialNum", "baseData", "bagData", "skillData",
					"horseBag",
					"weekCredit",
					"depotData",
					"?attendantBagData"
					}; 
		} // 1020
		public StringBuilder genInfo(StringBuilder buf) {
			if (base == null && baseData != null) {
//				System.out.println("Base ==" + baseData.length);
				ByteBuffer sd = ByteBuffer.wrap(baseData);
				base = new BaseData();
				try {
					base.init(sd);
				} catch (Exception e) {
//					System.out.println(StringUtil.getBytesReport("基础信息:", baseData, 0));
					e.printStackTrace();
				}
			}
			
			if (bag == null && bagData != null) {
//				System.out.println("Bag ==" + bagData.length);
				ByteBuffer sd = ByteBuffer.wrap(bagData);
				bag = new BagData();
				try {
					bag.init(sd);
				} catch (Exception e) {
//					System.out.println(StringUtil.getBytesReport("背包信息:", bagData, 0));
					e.printStackTrace();
				}
			}
			if (depot == null && depotData != null) {
				ByteBuffer sd = ByteBuffer.wrap(depotData);
				depot = new BagData();
				try {
					depot.init(sd);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (attendantBag == null && attendantBagData != null) {
				ByteBuffer sd = ByteBuffer.wrap(attendantBagData);
				attendantBag = new AttendantBagData();
				try {
					attendantBag.init(sd);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (skills == null && skillData != null) {
//				System.out.println("Skill ==" + skillData.length);
				ByteBuffer sd = ByteBuffer.wrap(skillData);
				skills = new SkillData();
				try {
					skills.init(sd);
				} catch (Exception e) {
//					System.out.println(StringUtil.getBytesReport("技能信息:", skillData, 0));
					e.printStackTrace();
				}
			}
			buf.append(xmlRes.format("playerInfo", this));
			
			return buf;
		}
	}
    /** 人物基本属性信息
     * From ServerSide: Player.toClientBytes
     */
    public static class BaseData extends UAData {
    	public int getAppDataType() {
			return 0;
		}
		public int id;
		public String name;
		public byte sex;
		public byte level;
		public byte clazz;
		public byte faction;
		public short maxhp;
		public short maxmp;
		public short hp;
		public short mp;
		public short strength;
		public short agility;
		public short stamina;
		public short intellect;
		public short attackpowerup;
		public short attackpowerdown;
		public short spellpower;
		public short spellheal; // 2009年10月14日添加属性
		public short defense;
		public short spelldefense;
		public short critical;
		public short spellcritical;
		public short hit;
		public short spellhit;
		public short dodge;
		public short spelldodge;
		public short anticrit;
		public short defensePercent;
		public short healthrestore;
		public short manarestore;
		public short skillPoint;
		public short propertyPoint;
		public int exp;
		public int upLevelExp;
		public int money;
		public short mapId;
		public int mapInstanceId;
		public short x;
		public short y;
		public short direct;
		public short state;
		public int credit;
		public String creditString;
		public String guildName;
		public GameItem equip0;
		public GameItem equip1;
		public GameItem equip2;
		public GameItem equip3;
		public GameItem equip4;
		public GameItem equip5;
		public GameItem equip6;
		public GameItem equip7;
		public GameItem equip8;
		public GameItem equip9;

		public int headScore;
		public int bodyScore;
		public int weaponScore;
		public byte flashLevel; // CHG:2010/5/10 Added
		public ChatOptions chatOp;
		public CoolDownData coolDownData[];
		public BuffsData buffs;
		public int honor;
		public String title;
		public short officerLevel; // CHG 2011-09-07 add TODO TODO 
		public byte lock; // CHG 2011-09-07 add 
		public int monthPaySize;// SKY 2011-09-07 add  包月服务个数
		public int monthpay_teleport; // SKY 2011-09-07 传送包月
		public byte monthpay_teleport_state; // SKY 2011-09-07 传送包月状态
		public int salary; // LEO 2012-07-10 工资
		public CardsData cardsData; // LEO 2012-07-10 卡片信息
		
		public String[] props_head = new String[] { "id", "name", "sex", "level", "clazz", "faction", "maxhp", "maxmp", "hp", 
				"mp", "strength", "agility", "stamina", "intellect", "attackpowerup", "attackpowerdown", 
				"spellpower", "spellheal", "defense", "spelldefense", "critical", "spellcritical", "hit", "spellhit", "dodge", 
				"spelldodge", "anticrit", "defensePercent", "healthrestore", "manarestore", "skillPoint", "propertyPoint", 
				"exp", "upLevelExp", "money", "mapId", "mapInstanceId", "x", "y", "direct", "state", "credit", "creditString", 
				"guildName",
				"equip0","equip1","equip2","equip3","equip4","equip5","equip6","equip7","equip8","equip9",
				"headScore", "bodyScore", "weaponScore", "flashLevel"/** CHG:2010/5/10 added */, "chatOp",  "coolDownData", 
				"buffs", "honor", "title"
				};
//    	public void read(ByteBuffer data) throws Exception {
//    		super.read(data);
//    		System.out.println(reportBuffer("LeftData", data));
//    	}

		public String getListenMapInfo() {
			return (pip.util.ui.RichConsole.genActionTag(xmlRes.getMessage("reviveTitle"), "m l 区 " + mapId)) +
			xmlRes.getMessage("pos") +"["+ mapId + "("+ x + ","+ y + "）]</action>";
		}
		public String getGuildNameWithoutTag() {
			return StringUtil.formal(guildName);
		}
		
		public void read(ByteBuffer data) throws Exception {
		    if(BaseConfig.CVS_BRANCH > BaseConfig.FixVersion_2011_07_26){
		    	addToPropsHead("officerLevel");
		    	addToPropsHead("lock");
		    }
		    
			if(BaseConfig.CVS_BRANCH > BaseConfig.FixVersion_2011_10_25){
		    	addToPropsHead("monthPaySize");
		    }
		    
			read(data, props_head);

			if(BaseConfig.CVS_BRANCH > BaseConfig.FixVersion_2011_10_25){
				String[] props_monthpay = new String[] {
					"monthpay_teleport", "monthpay_teleport_state"
				};
				
				if(monthPaySize > 0){
					read(data, props_monthpay);
				}
			}
			
			if(BaseConfig.CVS_BRANCH >= BaseConfig.FixVersion_2012_07_10){
			    read(data, new String[]{"salary", "cardsData"});
			}
		}
		
		private void addToPropsHead(String para){
			String[] tmp_head = new String[props_head.length + 1];
	        System.arraycopy(props_head, 0, tmp_head, 0, props_head.length);
	        tmp_head[props_head.length] = para;
	        props_head = tmp_head;
		}
    }
    /** 聊天配置信息 
     */
    public static class ChatOptions extends UAData {
    	public int getAppDataType() {
			return 0;
		}
		public byte world;
		public byte country;
		public byte nativeArea;
		public byte area;
		public byte army;
		public byte privateConv;
		public byte team;
		public byte system;
		
		public String nativeName;
		public String[] getProperties() {
			return new String[] { "world", "country", "nativeArea", "area", "army", "privateConv", "team", "system", "nativeName"};
		}
		String _chanelPalette[] = {"0xFFFFFF", "0xC0C0C0", "0x808080", "0xFF0000", "0xFFFF00", "0x00FF00", 
				"0x00FFFF", "0x6FBBF9", "0xFF00FF", "0xFFFF80", "0x00FF80", "0x80FFFF", "0x8080FF", "0xFF0080", "0xFF8000"};

		public String getConfig(String type, byte b) {
			int idx = b & 0xf;
			return "<font name=\"chat" + idx + "\" color=\"" + _chanelPalette[idx] + "\">" + type + ":" + idx +
			((((b >>4) & 1) == 1) ? "开" : "关") +  ((((b >>5) & 1) == 1) ? "警" : "") + "</font>"; 
		}
		public String genInfo() {
			return genInfo(new StringBuilder()).toString();
		}
		public StringBuilder genInfo(StringBuilder buf) {
			buf.append("|").append(getConfig("世", world));
			buf.append("|").append(getConfig("国", country));
			buf.append("|").append(getConfig("乡{" + nativeName + "}", nativeArea));
			buf.append("|").append(getConfig("区", area));
			buf.append("|").append(getConfig("军", army));
			buf.append("|").append(getConfig("私", privateConv));
			buf.append("|").append(getConfig("队", team));
			buf.append("|").append(getConfig("系", system));
			buf.append("|");
			return buf;
		}
    }
    /** 使用类型信息
     * from ServerSide UseType.toClientBytes() 
     */
    public static class UseType extends UAData {
    	public int getAppDataType() {
			return 0;
		}
    	public byte flag;
    	public short spellTime;
    	public short coolDownId;
    	public int coolDownTime;
    	public byte distance;
    	public byte useCount;
    	public byte useClazz;
    	public String useConfirm;
    	public void read(ByteBuffer data) throws Exception {
    		flag = (byte)(data.get() & 0x7f);
    		if (flag != 0) {
    			super.read(data);
    		}
    	}
    	public String[] getProperties() {
			return new String[] { "spellTime", "coolDownId", "coolDownTime", "distance", "useCount", "useClazz", "useConfirm"};
		}
    }
    // peony.game.itemenhance.ItemEnhance.toClientBytes()
    public static class ItemEnhance extends UAData {
    	public int getAppDataType() {
			return 0;
		}
    	public short len;
    	
    	public byte addHole;
    	public byte addMaxHole;
    	public byte numJewels;
    	public byte star;
    	public byte numEnhance;
    	
    	public Hole hole;
    	public Hole[] holes;
    	public NaturalEnhance enhance;
    	public NaturalEnhance[] enhances;
    	public String markString; // 2010-2-4添加
    	public byte addCardHole; // CHG: 2011-09-07 添加 卡槽数
    	public byte addMaxCardHole; // CHG: 2011-09-07 添加 最大卡槽数
    	public byte numCards; // CHG: 2011-09-07 添加 附魔的卡片数量
    	public CardsEnhance cards[];
    	public void read(ByteBuffer data) throws Exception {
    		len = (short)data.getShort();
    		if (len == 0) {
    			return;
    		}
    		super.read(data);
    		if (numJewels > 0) {
    			holes = new Hole[numJewels];
    			for (int i = 0; i < numJewels; i++) {
    				readField(data, "hole");
    				holes[i] = hole;
    			}
    		}
    		readField(data, "star");
    		readField(data, "numEnhance");
    		if (numEnhance > 0) {
    			enhances = new NaturalEnhance[numEnhance];
    			for (int i = 0; i < numEnhance; i++) {
    				readField(data, "enhance");
    				enhances[i] = enhance;
    			}
    		}
    		readField(data, "markString"); // 2010-2-4添加
    		if (BaseConfig.CVS_BRANCH > BaseConfig.FixVersion_2011_07_26) {
    			read(data, new String[]{"addCardHole", "addMaxCardHole", "numCards"});
    			cards = new CardsEnhance[numCards];
    			for (int i = 0; i < numCards; i++) {
    				cards[i] = new CardsEnhance();
    				cards[i].read(data);
    			}
    		}
    		
    	}
    	public String[] getProperties() {
			return new String[] { "addHole", "addMaxHole", "numJewels"};
		}
    }
    /** Server: peony.game.itemenhance.ItemEnhance.toClientBytes() 中 cards 部分 */
    public static class CardsEnhance extends UAData {
    	public static DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    	public byte cardId;
    	public byte showType;
    	public String name;
    	public String description;
    	public int validTime; // in second
    	
    	public int getAppDataType() {
			return 0;
		}
    	public String getExpireDate() {
    		Calendar c = Calendar.getInstance();
    		c.add(Calendar.SECOND, validTime);
    		return fmt.format(c.getTime());
    	}
    	public String[] getProperties() {
			return new String[] { "cardId", "showType", "name", "description", "validTime"};
		}
    }
    /** Server: peony.game.itemenhance.ItemEnhance.toClientBytes() 中 naturals 部分 */
    public static class NaturalEnhance extends UAData {
    	public byte level;
    	public byte attType;
    	public short value;
    	public byte percent;
    	
    	public int getAppDataType() {
			return 0;
		}
    	
    	public String[] getProperties() {
			return new String[] { "level", "attType", "value", "percent"};
		}
    	public int getRealLevel() {
    		return level+1;
    	}
    	public String getEffResult() {
    		StringBuilder buf = new StringBuilder();
    		if (value > 0) {
    			buf.append("+").append(value);
    		} else if (value != 0) {
    			buf.append(value);
    		}
    		if (percent > 0) {
    			buf.append("+").append(percent).append("%");
    		} else if (percent != 0) {
    			buf.append(percent).append("%");
    		}	
    		return buf.toString();
    	}
    }
    // src.peony.game.itemenhance.ItemEnhance.toClientBytes() 中 jewels 部分
    public static class Hole extends UAData {
    	public int getAppDataType() {
			return 0;
		}
    	
    	public byte holeId;
    	public byte showType;
    	public String name;
    	public byte attType;
    	public short mixValue; // 12 位后为使用级别
    	public byte jewelUpgrade; // 升级宝石数 CHG 2011-8-5
    	public String[] getProperties() {
    		if (BaseConfig.CVS_BRANCH <= BaseConfig.FixVersion_2011_07_26) {
    			return new String[] { "holeId", "showType", "name", "attType", "mixValue"};
    		}else{
    			return new String[] { "holeId", "showType", "name", "attType", "mixValue", "jewelUpgrade"};
    		}
		}
    	public int getEnhanceValue() {
    		return mixValue & 0xfff;
    	}
    }
    /** 装备属性信息
     * from ServerSide: Equipments.toClientBytes()
     */
	public static class GameItem extends UAData {
    	// from ServerSide: GameItem.toClientBytes() -> ItemTemplate.toClientBytes()
    	public int id;
    	public String name;
    	public byte maxCount;
    	public short type;
    	public byte useLevel;
    	public byte quality;
    	public int price;
    	public UseType useType;
    	public Equip equip;
    	
    	public byte leaveUseCount;
    	public int validTime;
    	public int bind;
    	public short duration;
    	public short ebHanceLen;
    	public int instanceId;
    	public byte count;
    	public ItemEnhance itemEnhance;
    	public byte primaryEnhance; // CHG: 2010-12-29 基础强化
    	public byte starEnhance;  // CHG: 2010-12-29 星级强化
    	public byte naturalEnhance;  // CHG: 2010-12-29 资质强化
    	public byte jewelsEnhance;  // CHG: 2010-12-29 宝石强化
    	public byte hasProduceArea; // CHG: 2011-07-04
    	public String produceArea; // CHG: 2011-07-04 
    	public byte iconImage; //CHG: 2012-05-29
    	
    	public void read(ByteBuffer data) throws Exception {
    		// from ServerSide: Equipments.toClientBytes()
    		count = data.get();
    		if (count == 0) {
    			return;
    		}
    		// from ServerSide: peony.game.GameItem.toClientBytes() -> peony.game.ItemTemplate.toClientBytes()
    		super.read(data);
    		
    		if(BaseConfig.CVS_BRANCH >= BaseConfig.FixVersion_2012_05_29){
    		    readField(data, "iconImage");
            }
    		
    		read(data, new String[]{"type", "useLevel", "quality", "price", "useType", "equip"});
    		
    		// from ServerSide: peony.game.GameItem.toClientBytes() 
    		if (useType.flag != 0) { // can use
    			readField(data, "leaveUseCount");
    		}
    		readField(data, "validTime");
    		readField(data, "bind");
    		if (equip.itemTypeId == 1) {
    			read(data, new String[]{"duration", "itemEnhance", "primaryEnhance", "starEnhance", "naturalEnhance", "jewelsEnhance"});
    		}
    		readField(data, "instanceId");
    		readField(data, "hasProduceArea");
    		if (hasProduceArea != 0) {
    			readField(data, "produceArea");
    		}
    		
    	}
    	public boolean isEquip() {
    		return equip.itemTypeId == 1;
    	}
    	public int getAppDataType() {
			return 0;
		}
    	public String[] getProperties() {
			return new String[] {"id", "name", "maxCount"};
		}
    	public String equipLevel[] = {"白","绿","蓝","紫","橙","黄"};
		public boolean multipleNumber() {
			return count > 1;
		}
    	public String getFormatName() {
    		if (name == null) {
    			return "";
    		}
    		StringBuilder buf = new StringBuilder();
    		if (quality >= 0 && quality < equipLevel.length) {
				buf.append("<style name=\"" + equipLevel[quality] + "\">");
			}
			buf.append(name);
			if (quality >= 0 && quality < equipLevel.length) {
				buf.append("</style>");
			}
			return buf.toString();
    	}
    	public String getTagedName() {
    		if (quality < 0 || quality >= equipLevel.length) {
    			return name;
    		}
    		return "<style name=\"" + equipLevel[quality] + "\">" + name + "</style>";
    	}
    }
    /**  通用物品模板以及装备模板信息 */
    public static class Equip extends UAData {
    	public int getAppDataType() {
			return 0;
		}
    	public byte type; //类型

    	public byte useLevel; //可装备等级
    	public byte clazz; //职业限制  -1 所有职业
    	public byte minorType; //小类
    	public short strengthLimit;
    	public short agilityLimit;
    	public byte initHole;
    	public byte maxHole;
    	public byte initCardCount; // CHG 2011-09-07 add 初始卡槽数
    	public byte maxCardCount; // CHG 2011-09-07 add 最大卡槽数
    	public byte markCharCount; // 2010-2-4添加
    	public byte mask1;
    	public byte mask2;
    	public byte mask3;
    	
    	public short intelligentLimit;
    	public short staminaLimit; //力量限制，敏捷限制，智力限制，耐力限制

    	
    	public short maxhp; //最大生命上限
    	public short maxmp; //最大魔法上限
    	public short strength; //力量
    	public short agility; //敏捷
    	public short stamina; //耐力
    	public short intellect; //智力
    	public short attackpower;  //攻击力
    	public short spellpower; //魔法攻击力
    	
    	public short armor; //物理防御力(护甲)
    	public short spelldefense; //魔法防御力
    	public short hitrating; //命中等级
    	public short dodgerating; //闪避等级
    	public short criticalrating; //暴击
    	public short spelldodgerating; //魔法闪避
    	public short healthrestore; //生命回复
    	public short manarestore; //魔法回复

    	public short minattack,maxattack;  //武器的物理攻击上限，下限。等同攻击力，两属性可同时存在
    	public short duration; //耐久 如果耐久是0，则认为是永久不损耗的
    	public short anticritrating; //免暴等级
    	public short speed; // 坐骑速度
    	
    	public boolean showRandom; // 如果为true，则隐藏实际属性，显示为“随机属性”
    	
    	public byte itemTypeId;
    	public void read(ByteBuffer data) throws Exception {
    		// from ItemTemplate.toClientBytes()
    		itemTypeId = data.get();
    		if (itemTypeId != 1) {
    			return; // not quipment
    		}
    		// from peony.game.EquipMentTemplate.toClientBytes()
    		String[] s = getProperties();
    		for (int i = 0; i < s.length; i++) {
    			try {
    				readField(data, s[i]);
    			} catch (Exception e) {
    				e.printStackTrace();
    				throw new Exception("读取属性 " + s[i] + " 异常:" + e.getMessage() + "[" + reportBuffer("", data) + "]");
    			}
    		}
			if((mask1 & 1) != 0){
				readField(data, "maxhp");
			}
			if((mask1 & 2) != 0){
				readField(data, "maxmp");
			}
			if((mask1 & 4) != 0){
				readField(data, "strength");
			}
			if((mask1 & 8) != 0){
				readField(data, "agility");
			}
			if((mask1 & 0x10) != 0){
				readField(data, "stamina");
			}
			if((mask1 & 0x20) != 0){
				readField(data, "intellect");
			}
			if((mask1 & 0x40) != 0){
				readField(data, "attackpower");
			}
			if((mask1 & 0x80) != 0){
				readField(data, "spellpower");
			}
			
			if((mask2 & 2) != 0){
				readField(data, "spelldefense");
			}
			if((mask2 & 4) != 0){
				readField(data, "hitrating");
			}
			if((mask2 & 8) != 0){
				readField(data, "dodgerating");
			}
			if((mask2 & 0x10) != 0){
				readField(data, "criticalrating");
			}

			if((mask2 & 0x20) != 0){
				readField(data, "spelldodgerating");
			}
			if((mask2 & 0x40) != 0){
				readField(data, "healthrestore");
			}
			if((mask2 & 0x80) != 0){
				readField(data, "manarestore");
			}
			
			if((mask3 & 1) != 0){
				readField(data, "armor");
			}
			if((mask3 & 6) != 0){
				readField(data, "minattack");
				readField(data, "maxattack");
			}
			if((mask3 & 8) != 0){
				readField(data, "duration");
			}
			if((mask3 & 0x10) != 0){
				readField(data, "anticritrating");
			}
			// 0x20 为 showRandom
			if((mask3 & 0x40) != 0){
				readField(data, "speed");
			}
    	}
		public String[] getProperties() {
			if (BaseConfig.CVS_BRANCH <= BaseConfig.FixVersion_2011_07_26) {
				return new String[] {"useLevel", "clazz", "minorType", 
						"strengthLimit", "agilityLimit","initHole", "maxHole",
						"markCharCount", // 2010-2-4添加
						"mask1", "mask2", "mask3"};
			} else {
				return new String[] {"useLevel", "clazz", "minorType", 
						"strengthLimit", "agilityLimit","initHole", "maxHole",
						"initCardCount", "maxCardCount", // 2011-09-07 
						"markCharCount", // 2010-2-4添加
						"mask1", "mask2", "mask3"};
			}
		}
    }
    /** 马包格属性信息	
     * From ServerSide: peony.game.HorseBag.toClientBytes()
     */
    public static class HorseBag extends UAData {
    	public short maxSize;
    	public short cSize;
    	public HorseData grid[];
    	public void read(ByteBuffer data) throws Exception {
    		data.getInt();
    		maxSize = data.get();
    		int n = data.get() & 0xff;
    		cSize = (short)n;
    		grid = new HorseData[n];
    		for (int i = 0; i < n; i++) {
    			grid[i] = new HorseData();
    			grid[i].init(data);
    		}
    	}
    	public int getAppDataType() {
			return 0;
		}
		public String[] getProperties() { 
			return null;
		}
    }
    /** 马匹信息
     * From ServerSide: peony.game.Horse.toClientBytes()
     */
    public static class HorseData extends UAData {
    	public int instanceId;
    	public String name;
    	public byte level;
    	public int exp;
    	public int upExp;
    	public short point;
    	public short maxDegree;
    	public short degree;
    	public int summonTime;
    	public short strength;
    	public short agility;
    	public short intellect;
    	public short stamina;
    	public short speed;
    	public short score;
    	public short imgId;
    	public short imgIdChange;
    	public short iconId;
    	public byte skillSize;
    	public HorseSkillData horseskillData;
    	public HorseEquip equips;
    	public boolean foodType;
    	public String showName;
    	public byte agentHorse;
    	public byte stat; // CHG 2011/5/24 //坐骑状态    0：已过期未激活    1：已激活   2：未过期 未激活
    	public byte fixCount; //坐骑合成次数
    	public byte iconImage; // CHG 2012/05/29
    	
    	public short strengthFix; // CHG 2012/06/26
    	public short agilityFix; // CHG 2012/06/26
    	public short intellectFix; // CHG 2012/06/26
    	public short staminaFix; // CHG 2012/06/26
    	public short speedFix; // CHG 2012/06/26
    	
    	public int getAppDataType() {
			return 0;
		}
//    	public void read(ByteBuffer data) throws Exception {
//        	try {
//				this.readField(data, "instanceId");
//				this.readField(data, "name");
//				this.readField(data, "level");
//				this.readField(data, "exp");
//				this.readField(data, "upExp");
//				this.readField(data, "point");
//				this.readField(data, "maxDegree");
//				this.readField(data, "degree");
//				this.readField(data, "summonTime");
//				this.readField(data, "strength");
//				this.readField(data, "agility");
//				this.readField(data, "intellect");
//				this.readField(data, "stamina");
//				this.readField(data, "speed");
//				this.readField(data, "score");
//				this.readField(data, "imgId");
//				this.readField(data, "iconId");
//				this.readField(data, "skillSize");
//				this.readField(data, "horseskillData");
//				this.readField(data, "equips");
//				this.readField(data, "foodType");
//				this.readField(data, "showName");
//				this.readField(data, "agentHorse");
//				this.readField(data, "stat");
//			} catch (Exception e) {
//				e.printStackTrace();
//				throw e;
//			}
//    	}
		public String[] getProperties() {
		    List<String> tmpList = new ArrayList<String>();
		    
		    tmpList.add("instanceId");
		    tmpList.add("name");
		    tmpList.add("level");
		    tmpList.add("exp");
		    tmpList.add("upExp");
		    tmpList.add("point");
		    tmpList.add("maxDegree");
		    tmpList.add("degree");
		    tmpList.add("summonTime");
		    tmpList.add("strength");
		    tmpList.add("agility");
		    tmpList.add("intellect");
		    tmpList.add("stamina");
		    tmpList.add("speed");
		    
		    if(BaseConfig.CVS_BRANCH >= BaseConfig.FixVersion_2012_06_26){
                tmpList.add("strengthFix");
                tmpList.add("agilityFix");
                tmpList.add("intellectFix");
                tmpList.add("staminaFix");
                tmpList.add("speedFix");
            }
		    
		    tmpList.add("score");
		    tmpList.add("imgId");
		    
		    if(BaseConfig.CVS_BRANCH > BaseConfig.FixVersion_2011_10_25){
		    	tmpList.add("imgIdChange");
		    }
		    
		    if(BaseConfig.CVS_BRANCH >= BaseConfig.FixVersion_2012_05_29){
		        tmpList.add("iconImage");
		    }
		    
		    tmpList.add( "iconId");
		    tmpList.add("skillSize");
		    tmpList.add("horseskillData");
		    tmpList.add("equips");
		    tmpList.add("foodType");
		    tmpList.add("showName");
		    tmpList.add("agentHorse");
		    tmpList.add("stat");
		    
		    if(BaseConfig.CVS_BRANCH > BaseConfig.FixVersion_2011_10_25){
		    	tmpList.add("fixCount");
		    }
		    
		    String[] result = new String[tmpList.size()];
		    tmpList.toArray(result);
		    
		    return result;
		}
		public boolean isNotActivated() {
			return (stat & 1) == 0;
		}
		public boolean isAgentHorse() {
			return agentHorse == 1;
		}
    }
    /** 马匹装备信息
     * From ServerSide: peony.game.HorseEquipments.toClientBytes()
     */
    public static class HorseEquip extends UAData {
    	public GameItem equip[] = new GameItem[7];
    	public GameItem tmp;
    	public int getAppDataType() {
			return 0;
		}
		public String[] getProperties() { 
			return new String[]{};
		}
		public void read(ByteBuffer data) throws Exception {
			for (int i = 0; i < equip.length; i++) {
				equip[i] = new GameItem();
				equip[i].init(data);
			}
    	}
    }

    /** 背包属性信息	
     * From ServerSide: poeny.game.TransactionBag.toClientBytes()
     */
    public static class BagData extends UAData {
    	public BagGridData grid[];
    	public void read(ByteBuffer data) throws Exception {
    		int n = data.get() & 0xff;
    		grid = new BagGridData[n];
    		for (int i = 0; i < n; i++) {
    			grid[i] = new BagGridData();
    			grid[i].init(data);
    		}
    	}
    	public int getAppDataType() {
			return 0;
		}
		public String[] getProperties() { 
			return null;
		}
    }
    /** 包格信息
     * From ServerSide: TransactionBagGrid.toClientBytes()
     */
    public static class BagGridData extends UAData {
    	public byte gridId;
    	public GameItem item;
    	public int getAppDataType() {
			return 0;
		}
		public String[] getProperties() { 
			return new String[]{"gridId", "item"};
		}
    }

    /** 马技能数据
     * from server: peony.game.Horse.toClientBytes() 中部分 */
    public static class HorseSkillData extends UAData {
    	public Skill skill[];
    	public void read(ByteBuffer data) throws Exception {
    		int n = data.get() & 0xff;
    		skill = new Skill[n];
    		for (int i = 0; i < n; i++) {
    			skill[i] = new Skill();
    			skill[i].init(data);
    		}
    	}
    	public int getAppDataType() {
			return 0;
		}
		public String[] getProperties() { 
			return null;
		}
    }
    /** 技能数据
     * from server: Skills.toClientBytes() */
    public static class SkillData extends UAData {
    	public Skill skill[];
    	int currentBookSkillSize;
    	public Skill bookskill[];
    	public void read(ByteBuffer data) throws Exception {
//    		System.out.println(reportBuffer("技能数据", data));
    		int n = data.get() & 0xff;
    		skill = new Skill[n];
    		for (int i = 0; i < n; i++) {
    			skill[i] = new Skill();
    			skill[i].init(data);
    		}
    		currentBookSkillSize = data.get();
    		n = data.get() & 0xff;
    		bookskill = new Skill[n];
    		for (int i = 0; i < n; i++) {
    			bookskill[i] = new Skill();
    			bookskill[i].init(data);
    		}
    	}
    	public int getAppDataType() {
			return 0;
		}
		public String[] getProperties() { 
			return null;
		}
    }
    /** 技能描述
     * from serverside: AbstractSkill.toClientBytes(Unit)*/
    public static class Skill extends UAData {
    	public short groupId;
    	public byte level;
    	public String name;
    	public short distance;
    	public short actTime;
    	public short cdGroup;
    	public int cdTime;
    	public byte range;
    	public byte type;
    	public byte targetType;
    	public byte point;
    	public int prepareAnimation;
    	public int iconId;
    	public short mp;
    	public int[] weapon;
    	public boolean hasNexeLevel;
    	public short nextLevelPoint;
    	public short maxLevel;
    	public byte clazz;
    	public void read(ByteBuffer data) throws Exception {
    		super.read(data);
    		int n = data.get() & 0xff;
    		weapon = new int[n];
    		for (int i = 0; i < n; i++) {
    			weapon[i] = data.get() & 0xff;
    		}
    		readField(data, "hasNexeLevel");
    		readField(data, "nextLevelPoint");
    		readField(data, "maxLevel");
    		readField(data, "clazz");
    	}
    	public int getAppDataType() {
			return 0;
		}
		public String[] getProperties() { 
			return new String[]{"groupId", "level", "name", "distance", "actTime", "cdGroup", "cdTime", 
					"range", "type", "targetType", "point", "prepareAnimation", "iconId", "mp"};
		}
    }
    /** CD数据
     * from server: CoolDownList.toClientBytes() */
    public static class CoolDownData extends UAData {
    	public byte id;
    	public int startTime;
    	public int endTime;
    	public int getAppDataType() {
			return 0;
		}
		public String[] getProperties() { 
			return new String[]{"id", "startTime", "endTime"};
		}
		public StringBuilder genInfo(StringBuilder buf) {
			return buf;
		}
    }
    /** BUFF数据
     * from server: peony.game.buff.Buffs.toClientBytes() */
    public static class BuffsData extends UAData {
    	public int ids[];
    	public int icons[];
    	public int endTimes[];
    	public int getAppDataType() {
			return 0;
		}
    	public void read(ByteBuffer data) throws Exception {
    		int n = data.get() & 0xff;
    		ids = new int[n];
    		icons = new int[n];
    		endTimes = new int[n];
    		for (int i = 0; i < n; i++) {
    			ids[i] = data.getInt();
    			icons[i] = data.getInt();
    			endTimes[i] = data.getInt();
    		}
    	}
		public String[] getProperties() { 
			return null;
		}
    }
    
    public static class AttendantBagData extends UAData {
    	public byte maxSize; // 随从栏最大栏数
    	public byte num; // 随从数量
    	public AttendantBagGrid grid[];
    	
    	public void read(ByteBuffer data) throws Exception {
    		read(data, new String[]{"maxSize", "num"});
    		grid = new AttendantBagGrid[num];
    		for (int i = 0; i < num; i++) {
    			grid[i] = new AttendantBagGrid();
    			grid[i].init(data);
    		}
    	}
    	public int getAppDataType() {
			return 0;
		}
		public String[] getProperties() { 
			return null;
		}
    	public class AttendantBagGrid extends UAData {
			public int instanceId; // 随从instanceid
			public String name; // 随从名字
			public byte sex; // 性别
			public short animateId; //动画ID
			public byte qulity; //随从品质(1-9品)
			public String qulityName; //品质名称
			public int loyal; // 忠诚度
			public byte maxLoyal; // 最大忠诚度
			public short hp; // 生命
			public short maxHp; //最大生命
			public short mp; //精力
			public short maxMp; //最大精力
			public short armor; //护甲
			public short magicArmor; //法防
			public short weaponAP1; //武器攻击下限
			public short weaponAP2; //武器攻击上限
			public short critical; //物理暴击
			public short spellcritical; //法术暴击
			public short spellpower; //法攻
			public short dodge; //物闪
			public short spelldodge; //法闪
			public short decritical; //免爆
			public short hit; //物理命中
			public short spellhit; //法术命中
			public short strength; //力
			public short agility; //敏
			public short intellect; //智
			public short stamina; //体
			public byte skillSize; //技能size
			public AttendantSkillData[] skills;
			public GameItem equips[] = new GameItem[10]; // 装备信息
			private String [] props = {"instanceId", "name", "sex", "animateId", "qulity", "qulityName", "loyal", 
					"maxLoyal", "hp", "maxHp", "mp", "maxMp", "armor", "magicArmor", "weaponAP1", "weaponAP2", 
					"critical", "spellcritical", "spellpower", "dodge", "spelldodge", "decritical", "hit", 
					"spellhit", "strength", "agility", "intellect", "stamina", "skillSize"};
			public void read(ByteBuffer data) throws Exception {
	    		read(data, props);
	    		skills = new AttendantSkillData[skillSize];
	    		for (int i = 0; i < skillSize; i++) {
	    			skills[i] = new AttendantSkillData();
	    			skills[i].init(data);
	    		}
	    		for (int i = 0; i < equips.length; i++) {
	    			equips[i] = new GameItem();
	    			equips[i].init(data);
	    		}
	    	}
	    	public int getAppDataType() {
				return 0;
			}
			public String[] getProperties() { 
				return null;
			}
			public class AttendantSkillData extends UAData {
				public byte canLight; //是否允许点亮	(0不允许、1允许)	
				public byte light; // 是否点亮（1点亮0未点亮）
				public byte hasSkill; // 是否有初始技能（1有0没有）
				public Skill skill;// 技能信息
				public void read(ByteBuffer data) throws Exception {
		    		read(data, new String[]{"canLight", "light", "hasSkill"});
		    		if (hasSkill == 1) {
		    			readField(data, "skill");
		    		}
		    	}
		    	public int getAppDataType() {
					return 0;
				}
				public String[] getProperties() { 
					return null;
				}
			}
    	}
    }
    
    public static class CardsData extends UAData{
        public CardInfo[] equipCards;
        public CardInfo[] horseEquipCards;
        
        public int getAppDataType(){
            return 0;
        }
        
        public String[] getProperties(){
            return null;
        }
        
        public void read(ByteBuffer data) throws Exception{
            int size = data.get() & 0xFF;
            equipCards = new CardInfo[size];
            
            for(int i = 0; i < size; i++){
                byte flag = data.get();
                
                if(flag != 0){
                    equipCards[i] = new CardInfo();
                    equipCards[i].read(data);
                }
            }
            
            size = data.get() & 0xFF;
            horseEquipCards = new CardInfo[size];
            
            for(int i = 0; i < size; i++){
                byte flag = data.get();
                
                if(flag != 0){
                    horseEquipCards[i] = new CardInfo();
                    horseEquipCards[i].read(data);
                }
            }
        }
        
        public class CardInfo extends UAData{
            public int cardId;
            public String cardTitle;
            public byte cardLevel;
            public String cardEnhanceDesc;
            
            public int getAppDataType(){
                return 0;
            }
            
            public String[] getProperties(){
                return new String[]{
                                "cardId", "cardTitle", "cardLevel", "cardEnhanceDesc"
                };
            }
        }
    }
}
