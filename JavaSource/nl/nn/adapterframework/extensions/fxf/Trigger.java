/*
 * $Log: Trigger.java,v $
 * Revision 1.3  2011-11-30 13:51:51  europe\m168309
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:54  Peter Leeuwenburgh <peter.leeuwenburgh@ibissource.org>
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.1  2009/03/04 15:56:57  Gerrit van Brakel <gerrit.van.brakel@ibissource.org>
 * added support for FXF 2.0
 *
 */
package nl.nn.adapterframework.extensions.fxf;

import java.util.ArrayList;
import java.util.List;

/**
 * Placeholder forTrigger message.
 * 
 * @author  Gerrit van Brakel
 * @since   FXF 2.0
 * @version Id
 */
public class Trigger {
	
	private String version;
	private String action;
	private String transporthandle;
	private List transfers;
	
	public Trigger() {
		super();
		transfers=new ArrayList();
	}


	public void setVersion(String string) {
		version = string;
	}
	public String getVersion() {
		return version;
	}

	public void setAction(String string) {
		action = string;
	}
	public String getAction() {
		return action;
	}

	public void setTransporthandle(String string) {
		transporthandle = string;
	}
	public String getTransporthandle() {
		return transporthandle;
	}

	public List getTransfers() {
		return transfers;
	}

	public void registerTransfer(Transfer transfer) {
		transfers.add(transfer);
	}
	public Transfer getTransfer(int index) {
		return (Transfer)transfers.get(index);
	}
}