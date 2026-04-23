package sango.data;

import pip.gm.MainApp;
import pip.gm.ui.data.NewGameCharacter;
import pip.io.uwap.UAData;
import pip.util.Res;
import sango.GmConstants;
import sango.GmConstantsRES;
public class POnlinePlayer extends UAData implements GmConstants, NewGameCharacter {
	public static final String COUNTRY[] = {GmConstantsRES.kingdomWei, GmConstantsRES.kingdomShu, GmConstantsRES.kingdomWu};
    public int getAppDataType() {
        return 0;
    }
    public int getId() {
    	return id;
    }
    public POnlinePlayer() {
    	
    }
    public POnlinePlayer(int id) {
    	this.id = id;
    }
    public String getName() {
    	return name;
    }
    public void setName(String name) {
    	this.name = name;
    }
    private String countryName = null;
    public int getCountryId() {
    	if (countryName == null) {
    		if ((country & 0xFC) != 0) {
    			career = (byte)(country & 0x03);
    			country >>= 2;
    		}
	    	if (country > 0 && country < 4) {
	    		countryName = COUNTRY[country - 1];
	    	} else {
	    		countryName = GmConstantsRES.kingdom;
	    	}
    	} 
    	return country;
    }
    public String getCountry() {
    	if (countryName == null) {
    		if ((country & 0xFC) != 0) {
    			career = (byte)(country & 0x03);
    			country >>= 2;
    		}
	    	if (country > 0 && country < 4) {
	    		countryName = COUNTRY[country - 1];
	    	} else {
	    		countryName = GmConstantsRES.kingdom;
	    	}
    	} 
    	return countryName;
    }
    public int id;
	public String name;
	public byte gender;
	public byte level;
	public byte career; //  (0 武将 1 刺客 2 谋士 3 方士)
	public byte country; //  (1 魏 2 蜀 3 吴)
	public int stageId;
	public short x;
	public short y;
	public String tongName;
	public String title;
	public String sceneName = "---"; // 从某处获得？ 
	public String[] getProperties() {
		return new String[] { "id", "name", "gender", "level", /**"career", */ "country",  "stageId", "x", "y", "tongName"}; //, "title"};
	}
}
