package sango.gm.cmd;

import pip.gm.fw.Auth;
import pip.gm.fw.AuthConstants;
import pip.gm.fw.GmFunction;
import pip.gm.fw.IMessage;
import pip.gm.fw.PDProcessor;
import pip.io.uwap.PDataFactory;
import pip.io.uwap.UAData;
import pip.io.uwap.UWapData;
import sango.GmConstants;

public class AccountBindingInfo extends GmFunction {
	public void registerPackage(PDataFactory factory) {
		factory.register((int)GmConstants.ADMIN_ACCOUNT_BIND_INFO_SERVER, PBindInfo.class);
	}
	public PDProcessor getPackageProcessor() {
		return new Processor();
	}



	public long getAuth() {
    	return AuthConstants.show;
    }
    public String getCommand(Auth auth) {
    	return null;
    }
    public String getName(Auth auth) {
    	return null;
    }
    public String getDescription(Auth auth) {
    	return null;
    }
    
	public static class PBindInfo extends UAData implements GmConstants {
		public int getAppDataType() {
			return ADMIN_ACCOUNT_BIND_INFO_SERVER;
		}
		public int serialNum;
		public String phone;
		public String personalId;
		public String question;
		public String email;
		public String[] getProperties() {
			return new String[] { "serialNum", "phone", "personalId", "question", "email"};
		}
	}
	
	public class Processor implements PDProcessor {
	    public boolean process(pip.gm.fw.AbstractClient master, UWapData data) {
	        if (data instanceof PBindInfo) {
	        	PBindInfo d = (PBindInfo)data;
	        	StringBuffer buf = new StringBuffer();
	        	if (d.phone != null && d.phone.length() > 0) {
	        		buf.append(AccountBindingInfoRES.mobile);
	        	}
	        	if (d.personalId != null && d.personalId.length() > 0) {
	        		buf.append(AccountBindingInfoRES.id);
	        	}
	        	if (d.question != null && d.question.length() > 0) {
	        		buf.append(AccountBindingInfoRES.qa);
	        	}
	        	if (d.email != null && d.email.length() > 0) {
	        		buf.append(AccountBindingInfoRES.mail);
	        	}
	        	if (buf.length() == 0) {
		        	master.onMessage(IMessage.MSG_TYPE_LOG, AccountBindingInfoRES.none, null);
	        	} else {
		        	buf.insert(0, AccountBindingInfoRES.title);
		        	master.onMessage(IMessage.MSG_TYPE_LOG, buf.toString(), null);
	        	}
	        } else {
	            return false;
	        }
	        return true;
	    }
	}
}
